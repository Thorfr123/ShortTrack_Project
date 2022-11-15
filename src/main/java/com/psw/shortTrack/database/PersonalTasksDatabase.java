package com.psw.shortTrack.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

import com.psw.shortTrack.data.List;
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
		
		return Integer.parseInt(executeQuery_SingleColumn(query));
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
		
		try (Connection connection = dataSource.getConnection()){
			if (connection != null) {
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				if (rs.next()) {
					Task tsk = new Task(rs.getString("name"));
					tsk.setID(rs.getInt("id"));
					tsk.setDescription(rs.getString("description"));
					//TODO: Falta setCreatedDate
					String str = rs.getString("deadline_date");
					if (str != null) {
						tsk.setDeadline(LocalDate.parse(str));
					}
					tsk.setCompleted(rs.getBoolean("state"));
					
					return tsk;
				}
			} else {
				System.out.println("Connection failed");
			}
		}
		return null;
	}
	
	public static ArrayList<Task> getAllTasks (int id_list) throws SQLException {
		String query = "SELECT * FROM projeto.personal_tasks WHERE list_id = '" + id_list + "';";
		
		try (Connection connection = dataSource.getConnection()){
			if (connection != null) {
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				ArrayList<Task> arrayTask = new ArrayList<Task>();
				while (rs.next()) {
					Task tsk = new Task(rs.getString("name"));
					tsk.setID(rs.getInt("id"));
					tsk.setDescription(rs.getString("description"));
					//Falta setCreatedDate
					String str = rs.getString("deadline_date");
					if (str != null) {
						tsk.setDeadline(LocalDate.parse(str));
					}
					tsk.setCompleted(rs.getBoolean("state"));
					arrayTask.add(tsk);
				}
				return arrayTask;
			} else {
				System.out.println("Connection failed");
			}
		}
		return null;
	}

}
