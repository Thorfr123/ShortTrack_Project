package com.psw.shortTrack.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.postgresql.util.PSQLException;

import com.psw.shortTrack.data.Account;
import com.psw.shortTrack.data.List;
import com.psw.shortTrack.data.Task;
import com.psw.shortTrack.data.User;

public class PersonalListsDatabase extends Database{
	
	/**
	 * Creates a new list in the database and returns the database id.
	 * This function does not add the tasks of the list to the database. To do that, you have to call the createTask function.
	 * 
	 * @param lst List to add in the database
	 * @return (True) Success; (False) Error - The account email is not in use or Null values
	 * 
	 * @throws SQLException If there was an error in the database connection
	 */
	public static boolean createList(List lst) throws SQLException {
		
		try {
			lst.setID(Integer.parseInt(executeQueryReturnSingleColumn(
						"INSERT INTO projeto.personal_lists (name, email)\r\n"
						+ "VALUES (" + toSQL((String)lst.getName()) + "," + toSQL((String)User.getAccount().getEmail()) +")\r\n"
						+ "RETURNING id;"
					)
				)
			);
			return true;
		} catch (PSQLException psql) {
			if (psql.getSQLState().startsWith("23")) {
				return false;
			}
			throw psql;
		}
		
	}
	
	/**
	 * Deletes the list from the database.
	 * It does not delete the tasks inside the list
	 * 
	 * @param id ID of the list to delete
	 * @return (True) Success; (False) The list didn't exist in the database
	 * 
	 * @throws SQLException If there was an error in the database connection
	 */
	public static boolean deleteList(int id) throws SQLException {
		
		return (executeUpdate("DELETE FROM projeto.personal_lists WHERE id=" + toSQL(id) + ";") > 0);
		
	}
	
	public static ArrayList<List> getAllLists (Account account) throws SQLException {
		
		try (Connection connection = getConnection()){
			
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(
				"SELECT l.id AS list_id, l.name AS list_name, l.email, t.id AS task_id, t.name AS task_name,\r\n"
				+ "t.description, t.created_date, t.deadline_date, t.state, t.list_id\r\n"
				+ "FROM projeto.personal_lists l\r\n"
				+ "LEFT JOIN projeto.personal_tasks t ON t.list_id=l.id\r\n"
				+ "WHERE email=" + toSQL((String)account.getEmail()) + "\r\n"
				+ "ORDER BY l.id, t.id;"
			);
			
			ArrayList<List> arrayList = new ArrayList<List>();
			List atual = null;
			
			while (rs.next()) {
				
				int lst_id = rs.getInt("list_id");
				if (atual == null || lst_id != atual.getID()) {
					
					atual = new List(rs.getString("list_name"), lst_id, new ArrayList<Task>());
					arrayList.add(atual);
					
				}
				
				Task t = PersonalTasksDatabase.getTask(rs);
				if (t != null) {
					atual.addTask(t);
				}
				
			}
			
			return arrayList;
		}
		
	}
	
	/**
	 * Changes the list's name in the database
	 * 
	 * @param id ID of the list
	 * @param new_name String with the new list's name
	 * @return (True) Success; (False) The list was not found
	 * 
	 * @throws SQLException If there was an error in the database connection
	 */
	public static boolean changeName(int id, String newListName) throws SQLException {
		
		return (executeUpdate(
			"UPDATE projeto.personal_lists SET name=" + toSQL((String)newListName) + " WHERE id=" + toSQL(id) + ";"
		) > 0);
		
	}
	
}
