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
	public static void createTask(Task task) throws SQLException{
		task.setID(Integer.parseInt(executeQueryReturnSingleColumn(
					"INSERT INTO projeto.personal_tasks (list_id, name, description, created_date, deadline_date, state)\r\n"
					+ "VALUES (" + toSQL(task.getParentID()) + "," + toSQL((String)task.getName()) + "," 
					+ toSQL((String)task.getDescription()) + "," + toSQL((LocalDate)task.getCreatedDate()) + "," 
					+ toSQL((LocalDate)task.getDeadlineDate()) + "," + toSQL(task.chekCompleted()) + ")\r\n"
					+ "RETURNING id;"
				)
			)
		);
	}

	/**
	 * Deletes the task with the respective id from the database
	 * 
	 * @param id ID of the task (in the database)
	 * @return (True) Success; (False) Nothing was deleted
	 * @throws SQLException If there was an error in the database connection
	 */
	public static boolean deleteTask(int id) throws SQLException {		
		return (
			executeUpdate("DELETE FROM projeto.personal_tasks WHERE id='" + id + "';"
		) > 0);
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
		
		try (Connection connection = getConnection()){
			if (connection != null) {
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(
					"SELECT * FROM projeto.personal_tasks WHERE id='" + id + "';"
				);
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
		
		try (Connection connection = getConnection()){
			if (connection != null) {
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(
					"SELECT * FROM projeto.personal_tasks WHERE list_id = '" + id_list + "';"
				);
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
	
	public static boolean updateTask(int id, String newName, String newDescription, LocalDate newDeadline, Boolean newState) throws SQLException {
		return (executeUpdate(
			"UPDATE projeto.personal_tasks SET name=" + toSQL((String)newName) + ", description=" 
			+ toSQL((String)newDescription) + ", deadline_date=" + toSQL((LocalDate)newDeadline) 
			+ ", state=" + toSQL(newState) + "\r\n"
			+ "WHERE id=" + toSQL(id) + ";"
		) > 0);
	}

}
