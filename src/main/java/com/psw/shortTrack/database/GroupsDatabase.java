package com.psw.shortTrack.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.postgresql.util.PSQLException;

import com.psw.shortTrack.data.Account;
import com.psw.shortTrack.data.Group;
import com.psw.shortTrack.data.Task;

public class GroupsDatabase extends Database{

	/**
	 * Creates a new group in the database. Updates the groups' id.
	 * It doesn't verify if the members' accounts exists
	 * 
	 * @param group Group that you want to add in the database
	 * @return (True) Success; (False) Error - If the manager account doesn't exist or null values
	 * 
	 * @throws SQLException If a database access error occurs
	 */
	public static boolean createGroup(Group group) throws SQLException {
		
		try {
			group.setID(executeQueryReturnInt(
				"INSERT INTO projeto.groups (name, manager, members)\r\n"
				+ "VALUES (" + toSQL((String)group.getName()) + "," + toSQL((String)group.getManagerEmail()) + "," 
				+ toSQL((ArrayList<String>)group.getMemberEmails()) + ")\r\n"
				+ "RETURNING id;")
			);
			return true;
		} catch(PSQLException psql) {
			if (psql.getSQLState().startsWith("23")) {
				return false;
			}
			throw psql;
		}
		
	}
	
	/**
	 * Deletes a group from the database.
	 * It also deletes the tasks of this group, because the database is configured to delete on cascade
	 * 
	 * @param id Group's id
	 * @return (True) If it was deleted; (False) If the group doesn't exist
	 * 
	 * @throws SQLException If a database access error occurs
	 */
	public static boolean deleteGroup(int id) throws SQLException {
		
		return (executeUpdate(
			"DELETE FROM projeto.groups WHERE id=" + toSQL(id) + ";"
		) > 0);
		
	}
	
	/**
	 * Returns every groups with the user's email, as a manager or as a member.
	 * If the user is the manager, it returns all the tasks of that group.
	 * If the user is only a member, it only returns the tasks assigned to him.
	 * 
	 * @param email String with user's email
	 * @return ArrayList every group with the user's email
	 * 
	 * @throws SQLException If a database access error occurs
	 */
	public static ArrayList<Group> getAllGroups(Account user) throws SQLException {
		
		try (Connection connection = getConnection()) {
			
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(
				"SELECT id, groups.name, members, account.email AS manager_email, account.name AS manager_name\r\n"
				+ "FROM projeto.groups JOIN projeto.account ON manager=email\r\n"
				+ "WHERE manager=" + toSQL((String)user.getEmail()) + " OR " + toSQL((String)user.getEmail()) + "=ANY(members);"
			);
			
			ArrayList<Group> all_groups = new ArrayList<Group>();
			while (rs.next()) {
				Account managerAccount = null;
				ArrayList<Task> tasks = null;
				ArrayList<Account> memberAccounts = new ArrayList<Account>();
				
				for (String member : (String[]) rs.getArray("members").getArray()) {
					Account memberAccount = AccountsDatabase.getAccount(member);
					if (memberAccount != null) {
						memberAccounts.add(memberAccount);
					}
				}
				
				if (rs.getString("manager_email").equals(user.getEmail())) {
					managerAccount = user;
					tasks = GroupTasksDatabase.getAllTasks(rs.getInt("id"));
				}
				else {
					managerAccount = new Account(rs.getString("manager_email"), rs.getString("manager_name"));
					tasks = GroupTasksDatabase.getAllTasks(rs.getInt("id"), user);
				}
				
				all_groups.add(	new Group( 	rs.getString("name"),
											managerAccount,
											rs.getInt("id"),
											tasks,
											memberAccounts));
			}
			return all_groups;
		}
		
	}

	/**
	 * Changes the name of the group in the database.
	 * 
	 * @param id Integer with group's id
	 * @param newGroupName String with group's new name
	 * @return (True) Success; (False) Error (Group doesn't exist)
	 * 
	 * @throws SQLException If a database access error occurs
	 */
	public static boolean changeName(int id, String newGroupName) throws SQLException {
		
		return (executeUpdate(
			"UPDATE projeto.groups SET name=" + toSQL((String)newGroupName) + " WHERE id=" + toSQL(id) + ";"
		) > 0);
		
	}
	
	/**
	 * Removes a member from one group and assigns his tasks to NULL.
	 * 
	 * @param id Group's id
	 * @param member Account of the member to remove
	 * @return (True) Success; (False) Member wasn't in the group or the group doesn't exist
	 * 
	 * @throws SQLException If a database access error occurs
	 */
	public static boolean removeMember(int id, Account member) throws SQLException {
		//TODO: verificar se este codigo é todo necessário
		return(executeUpdate(
			"UPDATE projeto.groups SET members=("
			+ "SELECT array_remove(members,'" + member.getEmail() + "') FROM projeto.groups WHERE id='" + id + "') "
			+ "WHERE id='"+id+"';\r\n"
			+ "UPDATE projeto.group_tasks SET assigned_to=NULL WHERE group_id='" + id + "' AND assigned_to='" + member.getEmail() + "';"
		) > 0);
	}

	// TODO: Verify code
	/**
	 * Adds a member to a group in the database.
	 * 
	 * @param id Group's id
	 * @param member Member's account
	 * @return (True) Success; (False) Group doesn't exist
	 * 
	 * @throws SQLException If a database access error occurs
	 */
	public static boolean addMember(int id, Account member) throws SQLException {
		
		return (executeUpdate(
			"UPDATE projeto.groups SET members=("
			+ "SELECT array_append(members,'" + member.getEmail() + "') FROM projeto.groups WHERE id='" + id + "') "
			+ "WHERE id='" + id + "';\r\n"
		) > 0);
		
	}
}
