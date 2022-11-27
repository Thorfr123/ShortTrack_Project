package com.psw.shortTrack.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.psw.shortTrack.data.Account;
import com.psw.shortTrack.data.Group;

public class GroupsDatabase extends Database{

	/**
	 * Creates a new group in the database. Returns the groups' id
	 * 
	 * @param group Group that you want to add in the database
	 * @return Group id
	 * 
	 * @throws SQLException If a database access error occurs
	 */
	public static void createGroup(Group group) throws SQLException {
		group.setID(Integer.parseInt(executeQueryReturnSingleColumn(
					"INSERT INTO projeto.groups (name, manager, members)\r\n"
			   		+ "VALUES (" + toSQL((String)group.getName()) + "," + toSQL((String)group.getManagerEmail()) + "," 
			   		+ toSQL((ArrayList<String>)group.getMemberEmails()) + ")\r\n"
			   		+ "RETURNING id;"
		   		)
			)
		);
	}
	
	/**
	 * Deletes a group from the database.
	 * It also deletes the tasks of this group, because the database is configured to delete on cascade
	 * 
	 * @param id Group's id
	 * @return (True) If it was deleted; (False) If the group doesn't exist
	 * @throws SQLException If a database access error occurs
	 */
	public static boolean deleteGroup(int id) throws SQLException {
		return (executeUpdate(
			"DELETE FROM projeto.groups WHERE id=" + toSQL(id) + ";"
		) > 0);
	}
	
	/**
	 * Returns every groups with the user's email, as a manager or as a member.
	 * It also assembles the group's tasks
	 * 
	 * @param email String with user's email
	 * @return ArrayList every group with the user's email
	 * @throws SQLException If a database access error occurs
	 */
	public static ArrayList<Group> getAllGroups(String email) throws SQLException {

		try (Connection connection = getConnection()){
			if (connection != null) {
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(
					"SELECT * FROM projeto.groups WHERE manager=" + toSQL((String)email) + " OR " + toSQL((String)email) 
					+ "=ANY(members);"
				);
				ArrayList<Group> all_groups = new ArrayList<Group>();
				while (rs.next()) {
					ArrayList<String> members = new ArrayList<String>();
					for (String member: (String[]) rs.getArray("members").getArray()) {
						if (member != null) {
							members.add(member);
						}
					}
					
					ArrayList<Account> memberAccounts = new ArrayList<Account>(0);
					for(String s : members) {
						Account memberAccount = AccountsDatabase.getAccount(s);
						if (memberAccount != null) {
							memberAccounts.add(memberAccount);
						}
					}
					Account managerAccount = AccountsDatabase.getAccount(rs.getString("manager"));
					
					Group group = new Group(rs.getString("name"),
											managerAccount,
											rs.getInt("id"),
											GroupTasksDatabase.getAllTasks(rs.getInt("id")),
											memberAccounts);
					all_groups.add(group);
				}
				return all_groups;
			} else {
				throw new SQLException("There was a connection error");
			}
		}
	}
	
	/**
	 * Updates, in the database, the name and members in a group
	 * 
	 * @param id Integer with group's id
	 * @param newGroupName String with group's new name
	 * @param newMembers ArrayList(Account) with members' accounts
	 * @return (True) Success; (False) Error
	 * @throws SQLException If a database access error occurs
	 */
	public static boolean updateGroup(int id, String newGroupName, ArrayList<Account> newMembers) throws SQLException {
		
		ArrayList<String> members = new ArrayList<String>(0);
		for(Account a : newMembers) {
			members.add(a.getEmail());
		}
		
		return (executeUpdate(
			"UPDATE projeto.groups SET name=" + toSQL((String)newGroupName) + ", members=" + toSQL((ArrayList<String>)members) + "\r\n"
			+ "WHERE id=" + toSQL(id) + ";"
		) > 0);
	}
	
	/**
	 * Removes a member from one group and assigns his tasks to NULL
	 * 
	 * @param id Group's id
	 * @param member Account of the member to remove
	 *  
	 * @return (True) Success; (False) Otherwise
	 * @throws SQLException If a database access error occurs
	 */
	public static boolean removeMember(int id, Account member) throws SQLException {
		return(executeUpdate(
			"UPDATE projeto.groups SET members=("
			+ "SELECT array_remove(members,'" + member.getEmail() + "') FROM projeto.groups WHERE id='" + id + "') WHERE id='"+id+"';\r\n"
			+ "UPDATE projeto.group_tasks SET assigned_to=NULL WHERE group_id='" + id + "' AND assigned_to='" + member.getEmail() + "';"
		) > 0);
	}
}
