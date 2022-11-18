package com.psw.shortTrack.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.psw.shortTrack.data.List;
import com.psw.shortTrack.data.User;

public class PersonalListsDatabase extends Database{
	
	/**
	 * Creates a new list in the database and returns the database id.
	 * This function does not add the tasks of the list to the database. To do that, you have to call the createTask function
	 * to add that task to the database
	 * 
	 * @param lst List to add in the database
	 * @return New id of the list
	 * 
	 * @throws SQLException If there was an error in the database connection
	 */
	public static int createList(List lst) throws SQLException {
		String email = User.getAccount().getEmail();
		if (email == null)
			return -1;
		
		String query = "INSERT INTO projeto.personal_lists (name, email)\r\n"
					 + "VALUES ('" + lst.getName() + "','" + email +"') RETURNING id;";
		
		return Integer.parseInt(executeQueryReturnSingleColumn(query));
	}
	
	/**
	 * Deletes the list from the database.
	 * It does not delete the tasks inside the list
	 * 
	 * @param id ID of the list to delete
	 * 
	 * @throws SQLException If there was an error in the database connection
	 */
	public static void deleteList(int id) throws SQLException {
		executeUpdate("DELETE FROM projeto.personal_lists WHERE id='" + id + "';");
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
		String query = "SELECT * FROM projeto.personal_lists WHERE email='" + email + "';";
		
		try (Connection connection = getConnection()){
			if (connection != null) {
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				ArrayList<List> arrayList = new ArrayList<List>();
				while (rs.next()) {
					int lst_id = rs.getInt("id");
					List lst = new List(rs.getString("name"), lst_id, PersonalTasksDatabase.getAllTasks(lst_id));
					
					arrayList.add(lst);
				}
				return arrayList;
			} else {
				System.out.println("Connection failed");
			}
		}
		
		return null;
	}
	
	/**
	 * Changes the list's name in the database
	 * 
	 * @param id ID of the list
	 * @param new_name String with the new list's name
	 * 
	 * @throws SQLException If there was an error in the database connection
	 */
	public static void changeName(int id, String new_name) throws SQLException {		
		executeUpdate("UPDATE projeto.personal_lists SET name='" + new_name + "' WHERE id='" + id + "';");
	}
}
