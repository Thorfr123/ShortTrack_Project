package com.psw.shortTrack.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

import com.psw.shortTrack.data.Account;
import com.psw.shortTrack.data.GroupTask;
import com.psw.shortTrack.data.Task;

public class GroupTasksDatabase extends Database {

	/**
	 * Creates a new task in the database
	 * 
	 * @param task Group task to add to the database
	 * @return Task's new id
	 * @throws SQLException If there was an error to connect to the database
	 */
	public static int createTask(GroupTask task) throws SQLException {
		//TODO: change return
		String query = 	"INSERT INTO projeto.group_tasks (group_id, assigned_to, name, description, created_date, deadline_date, state)\r\n"
						+ "VALUES (" + toSQL(task.getParentID()) + "," + toSQL((String)task.getAssignedToEmail()) + "," 
						+ toSQL((String)task.getName()) + "," + toSQL((String)task.getDescription()) + "," 
						+ toSQL((LocalDate)task.getCreatedDate()) + "," + toSQL((LocalDate)task.getDeadlineDate()) + "," 
						+ toSQL(task.chekCompleted()) + ")\r\n"
						+ "RETURNING id;";

		task.setID(Integer.parseInt(executeQueryReturnSingleColumn(query)));
		
		return 1;
	}
	
	/**
	 * Deletes a group task from database
	 * 
	 * @param id Task's id
	 * @return (True) If it succeeds; (False) The task didn't exist
	 * @throws SQLException If there was an error in the connection to the database
	 */
	public static boolean deleteTask(int id) throws SQLException {
		return (executeUpdate(
			"DELETE FROM projeto.group_tasks WHERE id=" + toSQL(id) + ";"
		) > 0);
	}
	
	/**
	 * Returns all the tasks form the group identified with this id
	 * 
	 * @param group_id Desired Group's id
	 * @return ArrayList with all the group's tasks
	 * 
	 * @throws SQLException If there was an error in the connection to the database
	 */
	public static ArrayList<Task> getAllTasks(int group_id) throws SQLException {

		try (Connection connection = getConnection()){
			if (connection != null) {
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(
						"SELECT * FROM projeto.group_tasks WHERE group_id=" + toSQL(group_id) + ";"
				);
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
					
					Account assignTo = AccountsDatabase.getAccount(rs.getString("assigned_to"));
					
					arrayTask.add(new GroupTask(rs.getString("name"),
												rs.getInt("id"),
												rs.getString("description"),
												createdDate,
												deadline,
												rs.getBoolean("state"),
												rs.getInt("group_id"),
												assignTo
												));
				}
				return arrayTask;
			} else {
				throw new SQLException("Failed to connect");
			}
		}
	}
	
	/**
	 * TODO: Refactoring this code plys :)
	 */
	public static ArrayList<Task> getAllMemberTasks(int group_id, String member) throws SQLException {
		try (Connection connection = getConnection()){
			if (connection != null) {
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(
						"SELECT * FROM projeto.group_tasks WHERE group_id=" + toSQL(group_id) + " AND assigned_to=" + toSQL((String)member) + ";"
				);
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
					
					Account assignTo = AccountsDatabase.getAccount(rs.getString("assigned_to"));
					
					arrayTask.add(new GroupTask(rs.getString("name"),
												rs.getInt("id"),
												rs.getString("description"),
												createdDate,
												deadline,
												rs.getBoolean("state"),
												rs.getInt("group_id"),
												assignTo
												));
				}
				return arrayTask;
			} else {
				throw new SQLException("Failed to connect");
			}
		}
	}
	
	/**
	 * Updates groupTask data in the database
	 * 
	 * @param task//TODO: edit
	 * @return (True) Success; (False) Otherwise
	 * @throws SQLException
	 */
	public static boolean updateTask(int id, String newName, String newDescription, LocalDate newDeadline, Boolean newState, String newAssignTo) throws SQLException {
		return (executeUpdate(
				"UPDATE projeto.group_tasks SET name=" + toSQL((String)newName) + ",description=" 
				+ toSQL((String)newDescription) + ",deadline_date=" + toSQL((LocalDate)newDeadline) 
				+ ",state=" + toSQL(newState) + ",assigned_to=" + toSQL((String)newAssignTo) + "\r\n"
				+ "WHERE id=" + toSQL(id) + ";"
		) > 0);
	}

}
