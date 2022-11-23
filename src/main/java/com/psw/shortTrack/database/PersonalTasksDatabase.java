package com.psw.shortTrack.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

import com.psw.shortTrack.data.PersonalTask;
import com.psw.shortTrack.data.Task;

public class PersonalTasksDatabase extends Database {
	
	/**
	 * Creates a new task in the database and returns the new task's id.
	 * Doesn't verify if the list exists in the database.
	 * 
	 * @param task Task to be added to the database
	 * @param lst List where the task belong
	 * @return New id of the task
	 * 
	 * @throws SQLException If there was an error in the database connection
	 */
	public static int createTask(Task task) throws SQLException{
		String		name			= task.getName(),
					description 	= task.getDescription(),
					createdDateString = null,
					deadlineDateString = null;
		LocalDate 	createdDate 	= task.getCreatedDate(),
					deadlineDate 	= task.getDeadlineDate();
		Boolean 	state			= task.chekCompleted();
		
		if (name != null)
			name = "'" + name + "'";
		if (description != null)
			description = "'" + description + "'";
		if (createdDate != null)
			createdDateString = "'" + createdDate + "'";
		if (deadlineDate != null)
			deadlineDateString = "'" + deadlineDate + "'";
		
		String query = "INSERT INTO projeto.personal_tasks (list_id, name, description, created_date, deadline_date, state)\r\n"
				+ "VALUES ('" + task.getParentID() + "," + name + "," + description + "," 
				+ createdDateString + "," + deadlineDateString + ",'" + state + "')"
				+ " RETURNING id;";
		
		task.setID(Integer.parseInt(executeQueryReturnSingleColumn(query)));
		
		return 1;
	}

	/**
	 * Deletes the task with the respective id from the database
	 * 
	 * @param id ID of the task (in the database)
	 * @return (True) Success; (False) Nothing was deleted
	 * @throws SQLException If there was an error in the database connection
	 */
	public static boolean deleteTask(int id) throws SQLException {		
		return (executeUpdate("DELETE FROM projeto.personal_tasks WHERE id='" + id + "';") > 0);
	}
	
	/**
	 * Returns the task with this id from the database
	 * 
	 * @param id Tasks's id (in the database)
	 * @return Desired task
	 * 
	 * @throws SQLException If there was an error in the database connection
	 */
	public static Task getTask(int id) throws SQLException{
		String query = "SELECT * FROM projeto.personal_tasks WHERE id='" + id + "';";
		
		try (Connection connection = getConnection()){
			if (connection != null) {
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				if (rs.next()) {
					String deadline_str = rs.getString("deadline_date");
					LocalDate deadline = null;
					if (deadline_str != null)
						deadline = LocalDate.parse(deadline_str);
					String created_str = rs.getString("created_date");
					LocalDate created = null;
					if (created_str != null)
						created = LocalDate.parse(created_str);				
					
					return new PersonalTask(rs.getString("name"),
											rs.getInt("id"),
											rs.getString("description"),
											created,
											deadline,
											rs.getBoolean("state"),
											rs.getInt("list_id"));
				}
			} else {
				throw new SQLException("Connection failed");
			}
		}
		return null;
	}
	
	/**
	 * Returns all the tasks from the list in the database
	 * 
	 * @param id_list List
	 * @return ArrayList with all the list's tasks
	 * 
	 * @throws SQLException If there was an error in the database connection
	 */
	public static ArrayList<Task> getAllTasks (int id_list) throws SQLException {
		String query = "SELECT * FROM projeto.personal_tasks WHERE list_id = '" + id_list + "';";
		
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
					LocalDate created = null;
					if (created_str != null)
						created = LocalDate.parse(created_str);
					
					arrayTask.add(new PersonalTask( rs.getString("name"),
													rs.getInt("id"),
													rs.getString("description"),
													created,
													deadline,
													rs.getBoolean("state"),
													rs.getInt("list_id")));
				}
				return arrayTask;
			} else {
				throw new SQLException("Connection failed");
			}
		}
	}
	
	public static boolean updateTask(Task task) throws SQLException {
		String description = task.getDescription(),
			   deadlineDateString = null;
		LocalDate deadlineDate = task.getDeadlineDate();
		
		if (description != null)
			description = "'" + description + "'";
		if (deadlineDate != null)
			deadlineDateString = "'" + deadlineDate + "'";
		
		String query = "UPDATE projeto.personal_tasks SET name='" + task.getName() + "', description=" + description + ", deadline_date=" + deadlineDateString + ", state='" + task.chekCompleted() + "'\r\n"
				+ "WHERE id='" + task.getID() + "';";
		return (executeUpdate(query) > 0);
	}

}
