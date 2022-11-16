package com.psw.shortTrack.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

import com.psw.shortTrack.data.List;
import com.psw.shortTrack.data.PersonalTask;
import com.psw.shortTrack.data.Task;
import com.psw.shortTrack.data.User;

public class PersonalTasksDatabase extends Database {
	
	/**
	 * Creates a new task in the database and returns the database id
	 * @param task Task to be added to the database
	 * @return New id for the task
	 * @throws SQLException
	 */
	public static int createTask(Task task, List lst) throws SQLException{
		String 		email			= User.getAccount().getEmail(),
					name			= task.getName(),
					description 	= task.getDescription(),
					createdDateString = null,
					deadlineDateString = null;
		LocalDate 	createdDate 	= task.getCreatedDate(),
					deadlineDate 	= task.getDeadlineDate();
		Boolean 	state			= task.chekCompleted();
		
		if (email != null)
			email = "'" + email + "'";
		if (name != null)
			name = "'" + name + "'";
		if (description != null)
			description = "'" + description + "'";
		if (createdDate != null)
			createdDateString = "'" + createdDate + "'";
		if (deadlineDate != null)
			deadlineDateString = "'" + deadlineDate + "'";
		
		String query = "INSERT INTO projeto.personal_tasks (list_id, email, name, description, created_date, deadline_date, state)\r\n"
				+ "VALUES ('" + lst.getID() + "'," + email + "," + name + "," + description + "," 
				+ createdDateString + "," + deadlineDateString + ",'" + state + "')"
				+ " RETURNING id;";
		
		return Integer.parseInt(executeQueryReturnSingleColumn(query));
	}

	/**
	 * Deletes the task with the respective id from the database
	 * @param id ID of the task (in the database)
	 * @throws SQLException
	 */
	public static void deleteTask(int id) throws SQLException {		
		executeUpdate("DELETE FROM projeto.personal_tasks WHERE id='" + id + "';");
	}
	
	public static Task getTask(int id) throws SQLException{
		String query = "SELECT * FROM projeto.personal_tasks WHERE id='" + id + "';";
		
		try (Connection connection = getConnection()){
			if (connection != null) {
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				if (rs.next()) {
					String name = rs.getString("name");
					String description = rs.getString("description");
					int taskId = rs.getInt("id");
					int listId = rs.getInt("list_id");
					String deadline_str = rs.getString("deadline_date");
					LocalDate deadline = null;
					if (deadline_str != null)
						deadline = LocalDate.parse(deadline_str);
					String created_str = rs.getString("created_date");
					LocalDate created = null;
					if (created_str != null)
						created = LocalDate.parse(created_str);
					Boolean state = rs.getBoolean("state");					
					
					return new PersonalTask(name, taskId, description, created, deadline, state, listId);
				}
			} else {
				System.out.println("Connection failed");
			}
		}
		return null;
	}
	
	public static ArrayList<Task> getAllTasks (int id_list) throws SQLException {
		String query = "SELECT * FROM projeto.personal_tasks WHERE list_id = '" + id_list + "';";
		
		try (Connection connection = getConnection()){
			if (connection != null) {
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				ArrayList<Task> arrayTask = new ArrayList<Task>();
				while (rs.next()) {
					String name = rs.getString("name");
					String description = rs.getString("description");
					int taskId = rs.getInt("id");
					int listId = rs.getInt("list_id");
					String deadline_str = rs.getString("deadline_date");
					LocalDate deadline = null;
					if (deadline_str != null)
						deadline = LocalDate.parse(deadline_str);
					String created_str = rs.getString("created_date");
					LocalDate created = null;
					if (created_str != null)
						created = LocalDate.parse(created_str);
					Boolean state = rs.getBoolean("state");	
					
					arrayTask.add(new PersonalTask(name, taskId, description, created, deadline, state, listId));
				}
				return arrayTask;
			} else {
				System.out.println("Connection failed");
			}
		}
		return null;
	}

}
