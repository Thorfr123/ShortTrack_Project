package com.psw.shortTrack.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import org.postgresql.util.PSQLException;

import com.psw.shortTrack.data.PersonalTask;
import com.psw.shortTrack.data.Task;

public class PersonalTasksDatabase extends Database {
	
	/**
	 * Creates a new task in the database and updates the task's id.
	 * 
	 * @param task Task to be added to the database
	 * @return (True) Success; (False) Error - List was not found or invalid null values
	 * 
	 * @throws SQLException If there was an error in the database connection
	 */
	public static boolean createTask(Task task) throws SQLException{
		
		try {
			task.setID(executeQueryReturnInt(
				"INSERT INTO projeto.personal_tasks (list_id, name, description, created_date, deadline_date, state)\r\n"
				+ "VALUES (" + toSQL(task.getParentID()) + "," + toSQL((String)task.getName()) + "," 
				+ toSQL((String)task.getDescription()) + "," + toSQL((LocalDate)task.getCreatedDate()) + "," 
				+ toSQL((LocalDate)task.getDeadlineDate()) + "," + toSQL(task.isCompleted()) + ")\r\n"
				+ "RETURNING id;"
			));
			return true;
		} catch (PSQLException psql) {
			if (psql.getSQLState().startsWith("23")) {
				return false;
			}
			throw psql;
		}
		
	}

	/**
	 * Deletes the task with the respective id from the database
	 * 
	 * @param id ID of the task
	 * @return (True) Success; (False) Nothing was deleted
	 * 
	 * @throws SQLException If there was an error in the database connection
	 */
	public static boolean deleteTask(int id) throws SQLException {
		
		return (
			executeUpdate("DELETE FROM projeto.personal_tasks WHERE id='" + id + "';"
		) > 0);
		
	}
	
	/**
	 * Updates the task with new info to the database. You need to provide all the info.
	 * 
	 * @param id Task's id
	 * @param newName Task's name
	 * @param newDescription Task's description
	 * @param newDeadline Task's deadline date
	 * @param newState Task's state
	 * @return (True) Success; (False) Error - Task doesn't exist or Null values
	 * 
	 * @throws SQLException If there was an error in the database connection
	 */
	public static boolean updateTask(int id, String newName, String newDescription, LocalDate newDeadline, Boolean newState) throws SQLException {
		
		return (executeUpdate(
			"UPDATE projeto.personal_tasks SET name=" + toSQL((String)newName) + ", description=" 
			+ toSQL((String)newDescription) + ", deadline_date=" + toSQL((LocalDate)newDeadline) 
			+ ", state=" + toSQL(newState) + "\r\n"
			+ "WHERE id=" + toSQL(id) + ";"
		) > 0);
		
	}
	
	/**
	 * Get a personal task from the result set. If the task is invalid, it returns null.
	 * 
	 * @param rs Result Set
	 * @return Task
	 * 
	 * @throws SQLException If there was an error
	 */
	protected static Task getTask(ResultSet rs) throws SQLException {
		
		int id = rs.getInt("task_id");
		if (id <= 0) {
			return null;
		}
			
		String deadline_str = rs.getString("deadline_date");
		LocalDate deadline = null;
		if (deadline_str != null)
			deadline = LocalDate.parse(deadline_str);
		String created_str = rs.getString("created_date");
		LocalDate created = null;
		if (created_str != null)
			created = LocalDate.parse(created_str);
		
		return new PersonalTask(rs.getString("task_name"),
								id,
								rs.getString("description"),
								created,
								deadline,
								rs.getBoolean("state"),
								rs.getInt("list_id"));
		
	}

}
