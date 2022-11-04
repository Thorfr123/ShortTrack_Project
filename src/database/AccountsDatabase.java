package database;

import data.*;
import java.sql.*;
import com.mchange.v2.c3p0.*;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class AccountsDatabase {
	
	private static ComboPooledDataSource dataSource = null;
	
	// Initial setup for the database
	static {
		
		// Cria uma forma de manter a conecção persistente
		dataSource = new ComboPooledDataSource();
		try {
			dataSource.setDriverClass("org.postgresql.Driver");
		} catch (Exception e){
			System.out.println(e);
		}
		dataSource.setJdbcUrl("jdbc:postgresql://db.fe.up.pt:5432/pswa0502");
		dataSource.setUser("pswa0502");
		dataSource.setPassword("jKWlEeAs");
		
		// Inicializa a tabela se não existir - não utilizado na prática, mas é uma boa prática
		String query =    "CREATE SCHEMA IF NOT EXISTS projeto;" 
						+ "ALTER SCHEMA projeto OWNER TO pswa0502;" 
						+ "CREATE TABLE IF NOT EXISTS projeto.account (\r\n"
						+ "    username character varying(32) NOT NULL,\r\n"
						+ "    email character varying(64) NOT NULL,\r\n"
						+ "    password character varying(32) NOT NULL,\r\n"
						+ "    register_date date,\r\n"
						+ "	   first_name character varying (32),\r\n"
						+ "	   last_name character varying (32)\r\n"
						+ ");" 
						+ "ALTER TABLE projeto.account OWNER TO pswa0502;";
		
		executeUpdate(query);	
	}
	
	public AccountsDatabase() {	};

	/**
	 * Verifies if the user can login
	 * 
	 * @param user String with user's username or email
	 * @param password String with user's password
	 * @return True - Successfully login; False - Otherwise
	 */
	public static boolean checkLogin(String user, String password) {
		String query;
		
		/* Como não sabemos à partida se o utilizador está a logar com o username ou com o email,
		   verificamos qual é através da existencia de @ */
		if (user.contains("@")) {
			query = "SELECT EXISTS (SELECT 1 FROM projeto.account WHERE email='" + user+"' AND password='"+password+"');";
		} else {
			query = "SELECT EXISTS (SELECT 1 FROM projeto.account WHERE username='"+user+"' AND password='"+password+"');";
		}
		
		String rs = executeQuery_SingleColumn(query);
		return (rs.equals("t"));
	}
	
	/**
	 * Checks if the email is already in use
	 * 
	 * @param email String with email
	 * @return True - There is no email; False - There is already
	 */
	public static boolean checkEmail(String email) {
		String checkEmail =   "SELECT EXISTS(SELECT 1 FROM projeto.account "
					+ "WHERE email = '" + email + "');";
		
		String rs = executeQuery_SingleColumn(checkEmail);
		
		return rs.equals("f");
	}
	
	/**
	 * Checks if the username is already in use
	 * 
	 * @param username String with username
	 * @return True - There is no username; False - There is already
	 */
	public static boolean checkUsername(String username) {
		String checkEmail =   "SELECT EXISTS(SELECT 1 FROM projeto.account "
					+ "WHERE username = '" + username + "');";
		
		String rs = executeQuery_SingleColumn(checkEmail);
		
		return rs.equals("f");
	}
	
	
	/**
	 * Adds a new account to the database
	 * 
	 * @param username String with user's username
	 * @param password String with user's password
	 * @param email String with user's email
	 * @param first_name String with user's first name
	 * @param last_name String with user's last name
	 * @return Either (1) non negative integer if the account is created; (2) -1 if there was a connection error
	 */
	public static int createAccount(String username, String password, String email, String first_name, String last_name) {
		Date registerDate = new Date();
		SimpleDateFormat DateFor = new SimpleDateFormat("yyyy-MM-dd");
		String stringDate= DateFor.format(registerDate);
		
		String query = "INSERT INTO projeto.account (username, password, email, register_date, first_name, last_name)" +
						"VALUES ('" + username + "', '" + password + "', '" + email +
						"', '" + stringDate + "', '" + first_name + "', '" + last_name + "');";
		
		return executeUpdate(query);
	}
	
	public static boolean deleteAccount(String email) {
		String query = "DELETE FROM projeto.account WHERE email='" + email + "'";
		
		if (executeUpdate(query) > 0)
			return true;
		else
			return false;
	}
	
	public static Account getAccount (String username, String password) {
		String query = 	  "SELECT email, first_name, last_name FROM projeto.account "
				+ "WHERE username='" + username + "' AND password='" + password+ "';";
		String email, firstName, lastName;
		
		try (Connection connection = dataSource.getConnection()){
			if (connection != null) {
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				if (rs.next()) {
					email = rs.getString("email");
					firstName = rs.getString("first_name");
					lastName = rs.getString("last_name");
					return new Account(username, password, firstName + " " + lastName, email);
				}
			} else {
				System.out.println("Connection failed");
			}
			
		} catch (Exception e) {
			System.out.println(e);
		}
		
		return null;
	}
	
	public static String getFirstName(String username) {
		String query = 	  "SELECT first_name FROM projeto.account "
						+ "WHERE username='" + username + "';";
		
		return executeQuery_SingleColumn(query);
	}
	
	public static String getLastName(String username, String password) {
		String query = 	  "SELECT last_name FROM projeto.account "
						+ "WHERE username='" + username + "' AND password='" + password + "';";
		
		return executeQuery_SingleColumn(query);
	}
	
	public static String getUsername(String email, String password) {
		String query = 	  "SELECT username FROM projeto.account "
						+ "WHERE email='" + email + "' AND password='" + password + "';";
			
		return executeQuery_SingleColumn(query);
	}
	
	public static Date getRegisterDate(String username, String password) {
		String query = 	  "SELECT username FROM projeto.account "
						+ "WHERE username='" + username + "' AND password='" + password + "';";
		
		String dateString = executeQuery_SingleColumn(query);
		Date register_date = null;
		
		if (dateString != null) {
			try {
				register_date = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return register_date;
	}
	
	public static boolean changeUserName(String old_username, String new_username) {
		String query = "UPDATE projeto.account " +
						"SET username='" + new_username +
						"'WHERE username='"+ old_username + "'";
		
		if (executeUpdate(query) > 0)
			return true;
		else
			return false;
	}
	
	public static boolean changePassword(String username, String new_password) {
		String query = "UPDATE projeto.account " +
						"SET password='" + new_password +
						"'WHERE username='"+ username + "'";
		
		if (executeUpdate(query) > 0)
			return true;
		else
			return false;
	}
	
	private static String executeQuery_SingleColumn(String query) {
		
		try (Connection connection = dataSource.getConnection()){
			if (connection != null) {
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				if (rs.next()) {
					return rs.getString(1);
				}
			} else {
				System.out.println("Connection failed");
			}
			
		} catch (Exception e) {
			System.out.println(e);
		}
		
		return null;
	}
	
	private static int executeUpdate(String query) {
		Connection connection = null;
		
		try {
			//Class.forName("org.postgresql.Driver");
			//connection = DriverManager.getConnection("jdbc:postgresql://db.fe.up.pt:5432/pswa0502","pswa0502","jKWlEeAs");
			connection = dataSource.getConnection();
			if (connection != null) {
				Statement stmt = connection.createStatement();
				int i = stmt.executeUpdate(query);
				return i;
			} else {
				System.out.println("Connection failed");
			}
			
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			if (connection != null) try { connection.close(); } catch (SQLException ignore) {}
		} 
		
		return -1;
	}
}

