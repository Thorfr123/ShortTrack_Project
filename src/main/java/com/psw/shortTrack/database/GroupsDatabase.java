package com.psw.shortTrack.database;

import java.sql.Array;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.psw.shortTrack.data.Group;
import com.psw.shortTrack.data.Task;

public class GroupsDatabase extends Database{

	public static int createGroup(String name, String manager, ArrayList<String> members) throws SQLException{
		String query = "INSERT INTO projeto.groups (name, manager, members)" +
				   "	VALUES ('" + name + "', '" + manager + "\', '{";

		for (int i = 0; i < members.size(); i++) {
			if (i == members.size() - 1) {
				query += "\"" + members.get(i) + "\"";
			} else {
			query += "\"" + members.get(i) + "\",";
			}
 		}
		
		query += "}\') RETURNING id;";
		
		return Integer.parseInt(executeQueryReturnSingleColumn(query));
	}
	
	public static ArrayList<Group> getAllGroups(String email) throws SQLException {
		String query = "SELECT * FROM projeto.groups WHERE manager='" + email + "' OR '" + email + "'=ANY(members);";
		
		try (Connection connection = getConnection()){
			if (connection != null) {
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				ArrayList<Group> arrayGroup = new ArrayList<Group>();
				while (rs.next()) {
					int group_id = rs.getInt("id");
					String name = rs.getString("name");
					String manager = rs.getString("manager");
					
					Array temp = rs.getArray("members");					
					String[] members_temp = (String[]) temp.getArray();
					ArrayList<String> members = new ArrayList<String>(members_temp.length);
					for (String mb: members_temp) {
						members.add(mb);
					}
					ArrayList<Task> tasks = GroupTasksDatabase.getAllTasks(group_id);
					
					Group group = new Group(name, manager, group_id, tasks, members);
					
					arrayGroup.add(group);
				}
				return arrayGroup;
			} else {
				System.out.println("Connection failed");
			}
		}
		return null;
	}
	
}
