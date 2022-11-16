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
	 * Creates a new list in the database and returns the database id
	 * 
	 * This function does not add the tasks of the list to the database. To do that, you have to call the createTask function
	 * to add that task to the database
	 * @param lst
	 * @return New id of the list
	 * @throws SQLException
	 */
	public static int createList(List lst) throws SQLException {
		String email = User.getAccount().getEmail();
		if (email == null)
			return -1;
		
		String query = "INSERT INTO projeto.personal_lists (name, email)\r\n"
					 + "VALUES ('" + lst.getName() + "','" + email +"') RETURNING id;";
		
		return Integer.parseInt(executeQuery_SingleColumn(query));
	}
	
	/**
	 * Deletes the list from the database.
	 * 
	 * It does not delete the tasks inside the list
	 * 
	 * @param id ID of the list to delete
	 * @throws SQLException
	 */
	public static void deleteList(int id) throws SQLException {
		executeUpdate("DELETE FROM projeto.personal_lists WHERE id='" + id + "';");
	}
	
	/**
	 * Returns the name of the list with this id
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	public static String getName (int id) throws SQLException {
		return executeQuery_SingleColumn("SELECT name FROM projeto.personal_lists WHERE id='"+id+"';");
	}
	
	/**
	 * Returns the list with this id. It also searchs for the tasks of the list.
	 * 
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	public static List getList(int id) throws SQLException{
		String query = "SELECT name FROM projeto.personal_lists WHERE id='" + id + "';";
		
		try (Connection connection = getConnection()){
			if (connection != null) {
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				if (rs.next()) {
					String name = rs.getString("name");
					
					List lst = new List(name, id, PersonalTasksDatabase.getAllTasks(id));
					return lst;
				}
			} else {
				System.out.println("Connection failed");
			}
		}
		return null;
	}
	
	/**
	 * Returns all the lists and tasks of a user
	 * @param email
	 * @return
	 * @throws SQLException
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
	 * Changes the name of the list with this id
	 * @param id
	 * @param new_name
	 * @throws SQLException
	 */
	public static void changeName(int id, String new_name) throws SQLException {		
		executeUpdate("UPDATE projeto.personal_lists SET name='" + new_name + "' WHERE id='" + id + "';");
	}
}
