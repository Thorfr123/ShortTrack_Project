package database;

import data.*;

import java.beans.PropertyVetoException;
import java.sql.*;
import com.mchange.v2.c3p0.*;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class AccountsDatabase{
	
	private static ComboPooledDataSource dataSource = null;
	
	// Initial setup for the database
	static {
		
		// Cria uma forma de manter a conecção persistente
		dataSource = new ComboPooledDataSource();
		try {
			dataSource.setDriverClass("org.postgresql.Driver");
			dataSource.setJdbcUrl("jdbc:postgresql://db.fe.up.pt:5432/pswa0502");
			dataSource.setUser("pswa0502");
			dataSource.setPassword("jKWlEeAs");
			dataSource.setCheckoutTimeout(1000);
			System.out.println(dataSource.getCheckoutTimeout());
		} catch (PropertyVetoException e){
			System.out.println(e);
		}
		
		// Inicializa a tabela se não existir - não utilizado, mas é uma boa prática		
		String query =    "CREATE SCHEMA IF NOT EXISTS projeto;"
						+ "CREATE TABLE IF NOT EXISTS projeto.account ();"
						+ "ALTER TABLE projeto.account ADD COLUMN IF NOT EXISTS username character varying(32) NOT NULL;"
						+ "ALTER TABLE projeto.account ADD COLUMN IF NOT EXISTS email character varying(64) NOT NULL;"
						+ "ALTER TABLE projeto.account ADD COLUMN IF NOT EXISTS password character varying(32) NOT NULL;"
						+ "ALTER TABLE projeto.account ADD COLUMN IF NOT EXISTS register_date date;"
						+ "ALTER TABLE projeto.account ADD COLUMN IF NOT EXISTS first_name character varying(32);"
						+ "ALTER TABLE projeto.account ADD COLUMN IF NOT EXISTS last_name character varying(32);"
						+ "ALTER TABLE ONLY projeto.account DROP CONSTRAINT IF EXISTS account_pkey;"
						+ "ALTER TABLE ONLY projeto.account ADD CONSTRAINT account_pkey PRIMARY KEY (username, email);"
						+ "CREATE TABLE IF NOT EXISTS projeto.tasks ();"
						+ "ALTER TABLE projeto.tasks ADD COLUMN IF NOT EXISTS id integer NOT NULL;"
						+ "ALTER TABLE projeto.tasks ADD COLUMN IF NOT EXISTS email character varying(64) NOT NULL;"
						+ "ALTER TABLE projeto.tasks ADD COLUMN IF NOT EXISTS name character varying(32) NOT NULL;"
						+ "ALTER TABLE projeto.tasks ADD COLUMN IF NOT EXISTS description character varying(128);"
						+ "ALTER TABLE projeto.tasks ADD COLUMN IF NOT EXISTS created_date date;"
						+ "ALTER TABLE projeto.tasks ADD COLUMN IF NOT EXISTS deadline_date date;"
						+ "ALTER TABLE projeto.tasks ADD COLUMN IF NOT EXISTS state boolean NOT NULL;"
						+ "CREATE SEQUENCE IF NOT EXISTS projeto.tasks_id_seq\r\n"
						+ "    AS integer\r\n"
						+ "    START WITH 1\r\n"
						+ "    INCREMENT BY 1\r\n"
						+ "    NO MINVALUE\r\n"
						+ "    NO MAXVALUE\r\n"
						+ "    CACHE 1;"
						+ "ALTER SEQUENCE projeto.tasks_id_seq OWNED BY projeto.tasks.id;"
						+ "ALTER TABLE ONLY projeto.tasks ALTER COLUMN id SET DEFAULT nextval('projeto.tasks_id_seq'::regclass);"
						+ "ALTER TABLE ONLY projeto.tasks DROP CONSTRAINT IF EXISTS tasks_pkey;"
						+ "ALTER TABLE ONLY projeto.tasks ADD CONSTRAINT tasks_pkey PRIMARY KEY (id);";
		
		try {
			executeUpdate(query);
		} catch (SQLException e) {
			System.out.println(e);
			System.out.println("There was an conection error in the database setup");
		}
	}

	/**
	 * Verifies if the user can login
	 * 
	 * @param user String with user's username or email
	 * @param password String with user's password
	 * @return True - Successfully login; False - Otherwise
	 */
	public static boolean checkLogin(String user, String password) throws SQLException{
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
	public static boolean checkEmail(String email) throws SQLException{
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
	public static boolean checkUsername(String username) throws SQLException{
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
	public static int createAccount(String username, String password, String email, String first_name, String last_name) throws SQLException{
		Date registerDate = new Date();
		SimpleDateFormat DateFor = new SimpleDateFormat("yyyy-MM-dd");
		String stringDate= DateFor.format(registerDate);
		
		String query = "INSERT INTO projeto.account (username, password, email, register_date, first_name, last_name)" +
						"VALUES ('" + username + "', '" + password + "', '" + email +
						"', '" + stringDate + "', '" + first_name + "', '" + last_name + "');";
		
		return executeUpdate(query);
	}
	
	/**
	 * Deletes an account from the database
	 * 
	 * @param email String with user's email
	 * @return Either True if it succeeds or False if it fails
	 */
	public static boolean deleteAccount(String email) throws SQLException{
		String query = "DELETE FROM projeto.account WHERE email='" + email + "'";
		
		if (executeUpdate(query) > 0)
			return true;
		else
			return false;
	}
	
	/**
	 * Gets account data from the database
	 * 
	 * @param username String with user's username
	 * @param password String with user's password
	 * @return Desired account
	 */
	public static Account getAccount (String username, String password) throws SQLException{
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
			
		}
		
		return null;
	}
	
	/**
	 * Gets the user's first name from the database
	 * 
	 * @param username String with user's username
	 * @param password String with user's password
	 * @return String with user's first name
	 */
	public static String getFirstName(String username, String password) throws SQLException{
		String query = 	  "SELECT first_name FROM projeto.account "
						+ "WHERE username='" + username + "' AND password='" + password + "';";
		
		return executeQuery_SingleColumn(query);
	}
	
	/**
	 * Gets the user's last name from the database
	 * 
	 * @param username String with user's username
	 * @param password String with user's password
	 * @return String with user's last name
	 */
	public static String getLastName(String username, String password) throws SQLException{
		String query = 	  "SELECT last_name FROM projeto.account "
						+ "WHERE username='" + username + "' AND password='" + password + "';";
		
		return executeQuery_SingleColumn(query);
	}
	
	/**
	 * Gets the user's username from the database
	 * 
	 * @param email String with user's email
	 * @param password String with user's password
	 * @return String with user's username
	 */
	public static String getUsername(String email, String password) throws SQLException{
		String query = 	  "SELECT username FROM projeto.account "
						+ "WHERE email='" + email + "' AND password='" + password + "';";
			
		return executeQuery_SingleColumn(query);
	}
	
	/**
	 * Gets the user's register Date from the database
	 * 
	 * @param username String with user's username
	 * @param password String with user's password
	 * @return Date with user's register date
	 */
	public static Date getRegisterDate(String username, String password) throws SQLException{
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
	
	/**
	 * Changes the username in the database
	 * 
	 * @param old_username String with the old username
	 * @param new_username String with the new username
	 * @return Either True if it succeeds or False if it fails
	 */
	public static boolean changeUserName(String old_username, String new_username) throws SQLException{
		String query = "UPDATE projeto.account " +
						"SET username='" + new_username +
						"'WHERE username='"+ old_username + "'";
		
		if (executeUpdate(query) > 0)
			return true;
		else
			return false;
	}
	
	/**
	 * Changes the password in the database
	 * 
	 * @param old_username String with the username
	 * @param new_username String with the new password
	 * @return Either True if it succeeds or False if it fails
	 */
	public static boolean changePassword(String username, String new_password) throws SQLException{
		String query = "UPDATE projeto.account " +
						"SET password='" + new_password +
						"'WHERE username='"+ username + "'";
		
		if (executeUpdate(query) > 0)
			return true;
		else
			return false;
	}
	
	private static String executeQuery_SingleColumn(String query) throws SQLException{
		
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
			
		}
		
		return null;
	}
	
	private static int executeUpdate(String query) throws SQLException{
		//Class.forName("org.postgresql.Driver");
		//connection = DriverManager.getConnection("jdbc:postgresql://db.fe.up.pt:5432/pswa0502","pswa0502","jKWlEeAs");
		
		try (Connection connection = dataSource.getConnection()){
			if (connection != null) {
				Statement stmt = connection.createStatement();
				return stmt.executeUpdate(query);
			} else {
				System.out.println("Connection failed");
			}
		}
		
		return -1;
	}
}

