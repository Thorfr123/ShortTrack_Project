package com.psw.shortTrack.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

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
	public static int createGroup(Group group) throws SQLException {
		//TODO: change return
		String query = 	"INSERT INTO projeto.groups (name, manager, members)\r\n"
				   		+ "VALUES (" + toSQL((String)group.getName()) + "," + toSQL((String)group.getManager()) + "," 
				   		+ toSQL((ArrayList<String>)group.getMembers()) + ")\r\n"
				   		+ "RETURNING id;";
		
		group.setID(Integer.parseInt(executeQueryReturnSingleColumn(query)));
		
		return 1;
	}
	
	public static int deleteGroup(int id) throws SQLException {
		return (executeUpdate("DELETE FROM projeto.groups WHERE id=" + toSQL(id) + ";"));
	}
	
	/**
	 * Returns every groups with the user's email, as a manager or as a member.
	 * It also assembles the group's tasks
	 * @param email String with user's email
	 * @return ArrayList every group with the user's email
	 * @throws SQLException If a database access error occurs
	 */
	public static ArrayList<Group> getAllGroups(String email) throws SQLException {
		String query = 	"SELECT * FROM projeto.groups WHERE manager=" + toSQL((String)email) + " OR " + toSQL((String)email) 
						+ "=ANY(members);";
		
		try (Connection connection = getConnection()){
			if (connection != null) {
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				ArrayList<Group> all_groups = new ArrayList<Group>();
				while (rs.next()) {
					ArrayList<String> members = new ArrayList<String>();
					for (String member: (String[]) rs.getArray("members").getArray()) {
						members.add(member);
					}
					Group group = new Group(rs.getString("name"), 
											rs.getString("manager"), 
											rs.getInt("id"), 
											GroupTasksDatabase.getAllTasks(rs.getInt("id")), 
											members);	
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
	 * @param group Group to update in database (TODO: Alterar)
	 * @return (True) Success; (False) Error
	 * @throws SQLException If a database access error occurs
	 */
	public static boolean updateGroup(int id, String newGroupName, ArrayList<String> newMembers) throws SQLException {
		String query = 	"UPDATE projeto.groups SET name=" + toSQL((String)newGroupName) + ", members="
						+ toSQL((ArrayList<String>)newMembers) + "\r\n"
						+ "WHERE id=" + toSQL(id) + ";";
		
		return (executeUpdate(query) > 0);
	}
}
