package com.psw.shortTrack.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.postgresql.util.PSQLException;

import com.psw.shortTrack.data.List;
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
	
	/**
	 * Returns all the lists and tasks of a user.
	 * 
	 * @param email User's email
	 * @return ArrayList with all the lists of the user
	 * 
	 * @throws SQLException If there was an error in the database connection
	 */
	public static ArrayList<List> getAllLists (String email) throws SQLException {
		
		try (Connection connection = getConnection()){
			
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(
				"SELECT * FROM projeto.personal_lists WHERE email=" + toSQL((String)email) + ";"
			);
			
			ArrayList<List> arrayList = new ArrayList<List>();
			while (rs.next()) {
				int lst_id = rs.getInt("id");
				
				arrayList.add(new List(	rs.getString("name"), 
										lst_id, 
										PersonalTasksDatabase.getAllTasks(lst_id))
				);
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
