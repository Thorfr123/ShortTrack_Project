package com.psw.shortTrack.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

import com.psw.shortTrack.data.Group;
import com.psw.shortTrack.data.GroupTask;
import com.psw.shortTrack.data.Task;

public class GroupTasksDatabase extends Database {

	public static int createTask(GroupTask tsk, Group group) throws NumberFormatException, SQLException {
		String description = tsk.getDescription();
		LocalDate deadline = tsk.getDeadlineDate();
		String assigned_to = tsk.getAssignedTo();
		String deadlineString = null;
		
		if (description != null) {
			description = "'" + description + "'";
		}
		if (deadline != null) {
			deadlineString = "'" + deadline + "'";
		}
		if (assigned_to != null) {
			assigned_to = "'" + assigned_to + "'";
		}
		
		String query = "INSERT INTO projeto.group_tasks (group_id, manager, assigned_to, name, description, created_date, deadline_date, state)\r\n"
				+ "VALUES ('" + group.getID() + "','" + group.getManager() + "'," + assigned_to + ",'" + tsk.getName() + "'," 
				+ description + ",'" + tsk.getCreatedDate() + "'," + deadlineString + ",'" + tsk.chekCompleted() + "') RETURNING id;";
	
		return Integer.parseInt(executeQueryReturnSingleColumn(query));
	}
	
	public static ArrayList<Task> getAllTasks(int group_id) throws SQLException {
		String query = "SELECT * FROM projeto.group_tasks WHERE group_id = '" + group_id + "';";
		
		try (Connection connection = getConnection()){
			if (connection != null) {
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				ArrayList<Task> arrayTask = new ArrayList<Task>();
				while (rs.next()) {
					String name = rs.getString("name");
					String description = rs.getString("description");
					String assignedTo = rs.getString("assigned_to");
					int taskId = rs.getInt("id");
					int groupID = rs.getInt("group_id");
					String deadline_str = rs.getString("deadline_date");
					LocalDate deadline = null;
					if (deadline_str != null)
						deadline = LocalDate.parse(deadline_str);
					String created_str = rs.getString("created_date");
					LocalDate createdDate = null;
					if (created_str != null)
						createdDate = LocalDate.parse(created_str);
					Boolean state = rs.getBoolean("state");	
					
					arrayTask.add(new GroupTask(name, taskId, description, createdDate, deadline, state, groupID, assignedTo));
				}
				return arrayTask;
			} else {
				System.out.println("Connection failed");
			}
		}
		return null;
	}

}
