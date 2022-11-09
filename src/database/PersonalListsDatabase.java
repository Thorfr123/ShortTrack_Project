package database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import data.List;
import data.User;

public class PersonalListsDatabase extends Database{
	
	public static int createList(List lst) throws SQLException {
		String email = User.getAccount().getEmail();
		if (email == null)
			return -1;
		
		String query = "INSERT INTO projeto.personal_lists (name, email)\r\n"
					 + "VALUES ('" + lst.getName() + "','" + email +"' RETURNING id;";
		
		return Integer.parseInt(executeQuery_SingleColumn(query));
	}
	
	public static void deleteList(int id) throws SQLException {
		executeUpdate("DELETE FROM projeto.personal_lists WHERE id='" + id + "';");
	}
	
	public static List getList(int id) throws SQLException{
		String query = "SELECT * FROM projeto.personal_lists WHERE id='" + id + "';";
		
		try (Connection connection = dataSource.getConnection()){
			if (connection != null) {
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				if (rs.next()) {
					List lst = new List(rs.getString("name"));
					lst.setId(rs.getInt("id"));
					return lst;
				}
			} else {
				System.out.println("Connection failed");
			}
		}
		
		return null;
	}
	
	public static ArrayList<List> getAllLists (String email) throws SQLException {
		String query = "SELECT * FROM projeto.personal_lists WHERE email='" + email + "';";
		
		try (Connection connection = dataSource.getConnection()){
			if (connection != null) {
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				ArrayList<List> arrayList = new ArrayList<List>();
				while (rs.next()) {
					List lst = new List(rs.getString("name"));
					lst.setId(rs.getInt("id"));
					arrayList.add(lst);
				}
				return arrayList;
			} else {
				System.out.println("Connection failed");
			}
		}
		
		return null;
	}
	
	// TODO
	public static void changeName(int id, String new_name) throws SQLException {
		
	}

}
