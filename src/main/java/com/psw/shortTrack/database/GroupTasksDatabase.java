package com.psw.shortTrack.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

import com.psw.shortTrack.data.GroupTask;
import com.psw.shortTrack.data.Task;

public class GroupTasksDatabase extends Database {

	/**
	 * Creates a new task in the database
	 * 
	 * @param tsk Group task to add to the database
	 * @return Task's new id
	 *  
	 * @throws NumberFormatException If it returns a no integer (unreachable)
	 * @throws SQLException If there was an error to connect to the database
	 */
	public static int createTask(GroupTask tsk) throws SQLException {
		String  description 	= null,
				assigned_to 	= null,
				deadlineString 	= null;
		
		if (tsk.getDescription() != null) {
			description = "'" + tsk.getDescription() + "'";
		}
		if (tsk.getDeadlineDate() != null) {
			deadlineString = "'" + tsk.getDeadlineDate() + "'";
		}
		if (tsk.getAssignedTo() != null) {
			assigned_to = "'" + tsk.getAssignedTo() + "'";
		}
		
		String query = "INSERT INTO projeto.group_tasks (group_id, assigned_to, name, description, created_date, deadline_date, state)\r\n"
				+ "VALUES ('" + tsk.getParentID() + "'," + assigned_to + ",'" + tsk.getName() + "'," 
				+ description + ",'" + tsk.getCreatedDate() + "'," + deadlineString + ",'" + tsk.chekCompleted() + "') RETURNING id;";
		
		return Integer.parseInt(executeQueryReturnSingleColumn(query));
	}
	
	/**
	 * Returns all the tasks form one group
	 * 
	 * @param group_id Desired Group's id
	 * @return ArrayList with all the group's tasks
	 * 
	 * @throws SQLException If there was an error in the connection to the database
	 */
	public static ArrayList<Task> getAllTasks(int group_id) throws SQLException {
		String query = "SELECT * FROM projeto.group_tasks WHERE group_id = '" + group_id + "';";
		
		try (Connection connection = getConnection()){
			if (connection != null) {
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				ArrayList<Task> arrayTask = new ArrayList<Task>();
				while (rs.next()) {
					String deadline_str = rs.getString("deadline_date");
					LocalDate deadline = null;
					if (deadline_str != null)
						deadline = LocalDate.parse(deadline_str);
					String created_str = rs.getString("created_date");
					LocalDate createdDate = null;
					if (created_str != null)
						createdDate = LocalDate.parse(created_str);
					
					arrayTask.add(new GroupTask(rs.getString("name"),
												rs.getInt("id"),
												rs.getString("description"),
												createdDate,
												deadline,
												rs.getBoolean("state"),
												rs.getInt("group_id"),
												rs.getString("assigned_to")));
				}
				return arrayTask;
			} else {
				throw new SQLException("Failed to connect");
			}
		}
	}

}
