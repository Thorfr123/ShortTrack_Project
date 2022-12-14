package com.psw.shortTrack.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

import org.postgresql.util.PSQLException;

import com.psw.shortTrack.data.Account;
import com.psw.shortTrack.data.Group;
import com.psw.shortTrack.data.GroupTask;
import com.psw.shortTrack.data.Task;

public class GroupsDatabase extends Database{

	/**
	 * Creates a new group in the database. Updates the groups' id.
	 * It doesn't verify if the members' accounts exists
	 * 
	 * @param group Group that you want to add in the database
	 * @return (True) Success; (False) If the manager account doesn't exist
	 * 
	 * @throws NotFoundException If the manager account doesn't exist
	 * @throws SQLException If a database access error occurs
	 */
	public static boolean createGroup(Group group) throws NotFoundException, SQLException {
		
		try {
			
			group.setID(executeQueryReturnInt(
					"INSERT INTO projeto.groups (name, manager, members)\r\n"
					+ "VALUES (" + toSQL((String)group.getName()) + "," + toSQL((String)group.getManagerEmail()) + "," 
					+ toSQL((ArrayList<String>)group.getMemberEmails()) + ")\r\n"
					+ "RETURNING id;"));
			return true;
			
		} catch(PSQLException psql) {
			if (psql.getSQLState().startsWith("23")) {
				throw new NotFoundException();
			}
			throw psql;
		}
		
	}
	
	/**
	 * Deletes a group from the database.
	 * It also deletes the tasks of this group, because the database is configured to delete on cascade
	 * 
	 * @param id Group's id
	 * @return (True) If it was deleted; (False) If the group still exists
	 * 
	 * @throws SQLException If a database access error occurs
	 */
	public static boolean deleteGroup(int id) throws SQLException {
		
		if (executeUpdate(
			"DELETE FROM projeto.groups WHERE id=" + toSQL(id) + ";"
		) > 0) {
			return true;
		}
		else {
			return !existGroup(id);
		}
		
	}
	
	/**
	 * Returns every groups with the user's email, as a manager or as a member.
	 * If the user is the manager, it returns all the tasks of that group.
	 * If the user is only a member, it only returns the tasks assigned to him.
	 * 
	 * @param email String with user's email
	 * @return ArrayList every group with the user's email
	 * 
	 * @throws NotFoundException If the user's account doesn't exist
	 * @throws SQLException If a database access error occurs
	 */
	public static ArrayList<Group> getAllGroups(Account user) throws NotFoundException, SQLException {
		
		try (Connection connection = getConnection()) {
			
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(
				"SELECT id, groups.name, members, account.email AS manager_email, account.name AS manager_name\r\n"
				+ "FROM projeto.groups INNER JOIN projeto.account ON manager=email\r\n"
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
			
			if (all_groups.size() == 0) {
				if (AccountsDatabase.checkEmail(user.getEmail())) {
					throw new NotFoundException();
				}
			}
			
			return all_groups;
		}
		
	}
	
	/*public static ArrayList<Group> getAllGroupsTeste(Account user) throws NotFoundException, SQLException {
		
		try (Connection connection = Database.getConnection()){
			
			Statement stmt = connection.createStatement();
			
			ResultSet rs = stmt.executeQuery(
				"SELECT group_tasks.*, groups.id , groups.manager as manager, groups.name as group_name, groups.members, account.name AS manager_name\r\n"
				+ "FROM projeto.group_tasks\r\n"
				+ "RIGHT JOIN projeto.groups ON group_id=groups.id\r\n"
				+ "LEFT JOIN projeto.account ON manager=email\r\n"
				+ "WHERE manager="+ toSQL((String)user.getEmail()) + " OR " + toSQL((String)user.getEmail()) + "=ANY(members)"
			);
			
			ArrayList<Group> groups = new ArrayList<Group>();
						
			Group anterior = null;
			
			while (rs.next()) {
				
				int group_id = rs.getInt("group_id_pk");
				String manager_email = rs.getString("manager");
				
				if (anterior == null || group_id != anterior.getID()) {
					Account managerAccount = null;
					if (user.getEmail().equals(manager_email)) {
						managerAccount = user;
					}
					else {
						managerAccount = new Account(manager_email, rs.getString("manager_name"));
					}
					
					ArrayList<Account> memberAccounts = new ArrayList<Account>();
					for (String member : (String[]) rs.getArray("members").getArray()) {
						Account memberAccount = AccountsDatabase.getAccount(member);
						if (memberAccount != null) {
							memberAccounts.add(memberAccount);
						}
					}
					
					Group novo = new Group(	rs.getString("group_name"),
											managerAccount,
											rs.getInt("id"),
											new ArrayList<Task>(),
											memberAccounts);
					groups.add(novo);
					anterior = novo;
				}
				
				String deadline_str = rs.getString("deadline_date");
				LocalDate deadline = null;
				if (deadline_str != null)
					deadline = LocalDate.parse(deadline_str);
				String created_str = rs.getString("created_date");
				LocalDate createdDate = null;
				if (created_str != null)
					createdDate = LocalDate.parse(created_str);
				
				Account assignTo = null;
				if (rs.getString("assigned_to") != null) {
					assignTo = new Account(rs.getString("assigned_to"), rs.getString("assigned_name"));
				}
				
				anterior.getTaskList().add(new GroupTask(rs.getString("name"),
												rs.getInt("id"),
												rs.getString("description"),
												createdDate,
												deadline,
												rs.getBoolean("state"),
												rs.getInt("group_id"),
												assignTo
												));				
			}
			
			
			
			
			
		}
		
		
		
		
		
		return null;
	}*/

	/**
	 * Changes the name of the group in the database.
	 * 
	 * @param id Integer with group's id
	 * @param newGroupName String with group's new name
	 * 
	 * @throws NotFoundException Group doesn't exist
	 * @throws SQLException If a database access error occurs
	 */
	public static boolean changeName(int id, String newGroupName) throws SQLException {
		
		if (executeUpdate(
			"UPDATE projeto.groups SET name=" + toSQL((String)newGroupName) + " WHERE id=" + toSQL(id) + ";"
		) > 0) {
			return true;
		}
		else if (!existGroup(id)) {
			throw new NotFoundException();
		}
		else {
			return false;
		}
		
	}
	
	//TODO: Verify code
	/**
	 * Removes a member from one group and assigns his tasks to NULL.
	 * 
	 * @param id Group's id
	 * @param member Account of the member to remove
	 * @return (True) Success; (False) Member still belongs to the group
	 * 
	 * @throws NotFoundException If group doesn't exist
	 * @throws SQLException If a database access error occurs
	 */
	public static boolean removeMember(int id, Account member) throws NotFoundException, SQLException {

		if (executeUpdate(
			"UPDATE projeto.groups SET members=("
			+ "SELECT array_remove(members,'" + member.getEmail() + "') FROM projeto.groups WHERE id='" + id + "') "
			+ "WHERE id='"+id+"';\r\n"
			+ "UPDATE projeto.group_tasks SET assigned_to=NULL WHERE group_id='" + id + "' AND assigned_to='" + member.getEmail() + "';"
		) > 0) {
			return true;
		}
		else if (!existGroup(id)) {
			throw new NotFoundException();
		}
		else {
			return !belongsToGroup(id, member.getEmail());
		}
			
	}

	// TODO: Verify code
	/**
	 * Adds a member to a group in the database.
	 * 
	 * @param id Group's id
	 * @param member Member's account
	 * @return (True) Success; (False) Member still doesn't belong to the group
	 * 
	 * @throws NotFoundException If the group doesn't exist
	 * @throws SQLException If a database access error occurs
	 */
	public static boolean addMember(int id, Account member) throws NotFoundException, SQLException {
		
		if (executeUpdate(
			"UPDATE projeto.groups SET members=("
			+ "SELECT array_append(members,'" + member.getEmail() + "') FROM projeto.groups WHERE id='" + id + "') "
			+ "WHERE id='" + id + "';\r\n"
		) > 0) {
			return true;
		}
		else if (!existGroup(id)) {
			throw new NotFoundException();
		}
		else {
			return (belongsToGroup(id, member.getEmail()));
		}
		
	}
	
	/**
	 * Checks if a group with the provided id exists
	 *
	 * @param id Group's id
	 * @return (True) It exists; (False) Otherwise
	 * 
	 * @throws SQLException If there is a connection error
	 */
	protected static boolean existGroup(int id) throws SQLException {
		
		return executeQueryReturnBoolean(
			"SELECT EXISTS(SELECT 1 FROM projeto.groups WHERE id=" + toSQL(id) + ";"
		);
		
	}
	
	/**
	 * Checks if a user belongs to the group with the provided id.
	 * 
	 * @param id Group's id
	 * @param email User's email
	 * @return (True) User is either the manager or a member of the group; (False) Otherwise
	 * 
	 * @throws SQLException If there is a connection error
	 */
	private static boolean belongsToGroup(int id, String email) throws SQLException {
		
		return executeQueryReturnBoolean(
			"SELECT EXISTS(SELECT 1 FROM projeto.groups WHERE id=" + toSQL(id) + " AND email=ANY(members);"	
		);
		
	}
	
}
