package com.psw.shortTrack.database;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.psw.shortTrack.fileIO.FileIO;


/*
 * Error codes uteis
 * 
 *  - 23502 - Not null violation
 *  - 23503 - Foreign key violation
 *  - 23505 - Unique violation
 *
 */

public class Database {
	private static final String driver 		= "org.postgresql.Driver";
	private static final String url 		= "jdbc:postgresql://db.fe.up.pt:5432/pswa0502";
	private static final String user 		= "pswa0502";
	private static final String password 	= "jKWlEeAs";
	
	private static ComboPooledDataSource dataSource;
	
	// Initial setup for the database
	static {
		// Cria uma forma de manter a conecção persistente
		try {
			config();
			setup();
		} catch (PropertyVetoException pve) {
			System.out.println("Unreachable!");
			pve.printStackTrace();
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
		return dataSource.getConnection();
	}
	
	/**
	 * Configures the database access pool
	 * 
	 * @throws PropertyVetoException If the specified driver is not valid (unreachable)
	 */
	private static void config() throws PropertyVetoException {
		dataSource = new ComboPooledDataSource();
		dataSource.setDriverClass(driver);
		dataSource.setJdbcUrl(url);
		dataSource.setUser(user);
		dataSource.setPassword(password);
		
		// Define o numero máximo de conexões
		dataSource.setMaxPoolSize(30);
		// Timeout de checkout (getConnection)
		dataSource.setCheckoutTimeout(3000);
		// Define o numero máximo de tentativas de checkout
		dataSource.setAcquireRetryAttempts(5);
		// Define o delay entre cada tentativa de checkout
		dataSource.setAcquireRetryDelay(1000);
		// Define um teste obrigatório da conexão ao chamar getConnection
		dataSource.setTestConnectionOnCheckout(true);
		// Segundos que as conexões não usadas para além do minimo (3 conexões) duram
		dataSource.setMaxIdleTimeExcessConnections(180);
		// Define a query a executar nos testes (query mais rapida)
		dataSource.setPreferredTestQuery("SELECT 1");
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
			if (connection != null) {
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				if (rs.next()) {
					return rs.getString(1);
				}
			} else {
				throw new SQLException("Connection failed!");
			}
		}
		
		return null;
	}
	
	protected static boolean executeQueryReturnBoolean(String query) throws SQLException {
		
		try (Connection connection = getConnection()){
			if (connection != null) {
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				if (rs.next()) {
					return rs.getBoolean(1);
				}
			} else {
				throw new SQLException("Connection failed!");
			}
		}
		
		return false;
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
			if (connection != null) {
				Statement stmt = connection.createStatement();
				return stmt.executeUpdate(query);
			} else {
				throw new SQLException("Connection failed!");
			}
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
		String str = null;
		if (date != null) {
			str = "'" + date.toString() + "'";
		}
		return str;
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
	
	/**
	 * Returns true if all string are not null and not empty. False otherwise
	 * 
	 * @param AllParams
	 * @return
	 */
	protected static boolean checkStringParams(String ...AllParams) {
		for (String s : AllParams) {
			if (s == null || s.isBlank())
				return false;
		}
		return true;
	}
}

