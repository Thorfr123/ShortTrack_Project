package com.psw.shortTrack.database;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

import com.psw.shortTrack.fileIO.FileIO;


public class Database {
	private static final String driver 		= "org.postgresql.Driver";
	private static final String url 		= "jdbc:postgresql://db.fe.up.pt:5432/pswa0502";
	private static final String user 		= "pswa0502";
	private static final String password 	= "jKWlEeAs";
	
	// Initial setup for the database
	static {
		try {
			config();
			setup();
		} catch (ClassNotFoundException pve) {
			System.out.println("Database postgresql driver not found!");
		} catch (SQLException sqle) {
			System.out.println("Connection error in database setup");
		}
	}
	
	/**
	 * Attempts to establish a connection with the database.
	 *
	 * @return a connection to the data source
	 * @throws SQLException If a database access error occurs
	 */
	protected static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(url, user, password);
	}
	
	/**
	 * Configures the database access pool
	 * 
	 * @throws PropertyVetoException If the specified driver is not valid (unreachable)
	 * @throws ClassNotFoundException 
	 */
	private static void config() throws ClassNotFoundException {
		
		Class.forName(driver);
		
	}
	
	/**
	 * Executes a query to the database and returns the first column of the response as a string
	 * 
	 * @param query SQL code to execute
	 * @return String - First column of the response
	 * 
	 * @throws SQLException If a database access error occurs 
	 */
	protected static String executeQueryReturnSingleColumn(String query) throws SQLException{
		
		try (Connection connection = getConnection()){
			
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			return (rs.next() ? rs.getString(1) : null);
		}

	}
	
	/**
	 * Executes a query to the database and returns the first column of the response as a boolean
	 * 
	 * @param query SQL code to execute
	 * @return Boolean - Response
	 * 
	 * @throws SQLException If a database access error occurs 
	 */
	protected static boolean executeQueryReturnBoolean(String query) throws SQLException {
		
		try (Connection connection = getConnection()){
				
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			return (rs.next() ? rs.getBoolean(1) : false);
		}
	}
	
	/**
	 * Executes a query to the database and returns the first column of the response as a boolean
	 * 
	 * @param query SQL code to execute
	 * @return Boolean - Response
	 * 
	 * @throws SQLException If a database access error occurs 
	 */
	protected static int executeQueryReturnInt(String query) throws SQLException {
		
		try (Connection connection = getConnection()){
				
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			return (rs.next() ? rs.getInt(1) : -1);
		}
		
	}
	
	/**
	 * Executes a update query to the database
	 * 
	 * @param query String with the sql code
	 * @return either (1) the row count for SQL Data Manipulation Language (DML) statementsor 
	 * (2) 0 for SQL statements that return nothing
	 * 
	 * @throws SQLException If a database access error occurs
	 */
	protected static int executeUpdate(String query) throws SQLException{
		
		try (Connection connection = getConnection()){
			
			Statement stmt = connection.createStatement();
			
			return stmt.executeUpdate(query);
		}
		
	}
	
	/**
	 * Executes the sql code to do the initial setup of the database.
	 * Creates the needed tables, columns, sequences, primary keys, etc.
	 * 
	 * @throws SQLException If a database access error occurs
	 */
	private static void setup() throws SQLException {
		
		String query = FileIO.readDatabaseSetup();
		
		if (query == null || query.isBlank()) {
			System.out.println("Couldn't read the database setup local file");
			return;
		}
		
		executeUpdate(query);
		
	}
	
	/**
	 * Parses a string to SQL String
	 * 
	 * @param str - String to parse
	 * @return SQL String
	 */
	protected static String toSQL(String str) {
		if (str != null) {
			str.replace("'", "\'");
			str = "'" + str + "'";
		}
		return str;
	}
	
	/**
	 * Parses an integer to SQL String
	 * 
	 * @param i - Integer to parse
	 * @return SQL String
	 */
	protected static String toSQL(int i) {
		return "'" + i + "'";
	}
	
	/**
	 * Parses a localDate to SQL String
	 * 
	 * @param date - LocalDate to parse
	 * @return SQL String
	 */
	protected static String toSQL(LocalDate date) {
		return (date == null ? null : "'" + date.toString() + "'");
	}
	
	/**
	 * Parses a boolean to SQL String
	 * 
	 * @param bool - boolean to parse
	 * @return SQL String
	 */
	protected static String toSQL(boolean bool) {
		return "'" + bool + "'";
	}
	
	/**
	 * Parses an ArrayList(String) to SQL Array
	 * 
	 * @param array - ArrayList to parse
	 * @return SQL Array
	 */
	protected static String toSQL(ArrayList<String> array) {
		String str = "'{";
		if (array != null) {
			for (int i = 0; i < array.size(); i++) {
				str += "\"" + array.get(i) + "\"";
				if (i != array.size() - 1) {
					str += ",";
				}
			}
		}
		return str + "}'";
	}
}

