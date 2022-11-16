package com.psw.shortTrack.database;

import java.sql.*;

import java.time.LocalDate;

import com.psw.shortTrack.data.Account;

public class AccountsDatabase extends Database{
	/**
	 * Verifies if the user can login
	 * 
	 * @param user String with user's username or email
	 * @param password String with user's password
	 * @return True - Successfully login; False - Otherwise
	 */
	public static boolean checkLogin(String user, String password) throws SQLException{
		String query = null;
		
		/* Como não sabemos à partida se o utilizador está a logar com o username ou com o email,
		   verificamos qual é através da existencia de @ */
		if (user.contains("@")) {
			query = "SELECT EXISTS (SELECT 1 FROM projeto.account WHERE email='" + user + "' AND password='" + password + "');";
		} else {
			query = "SELECT EXISTS (SELECT 1 FROM projeto.account WHERE username='" + user + "' AND password='" + password + "');";
		}
		
		return (executeQuery_SingleColumn(query).equals("t"));
	}
	
	/**
	 * Checks if the email is already in use
	 * 
	 * @param email String with email
	 * @return True - There is no email; False - There is already
	 */
	public static boolean checkEmail(String email) throws SQLException{
		String query =   "SELECT EXISTS(SELECT 1 FROM projeto.account "
							+ "		WHERE email = '" + email + "');";
		
		return executeQuery_SingleColumn(query).equals("f");
	}
	
	/**
	 * Checks if the username is already in use
	 * 
	 * @param username String with username
	 * @return True - There is no username; False - There is already
	 */
	public static boolean checkUsername(String username) throws SQLException{
		String query =   "SELECT EXISTS(SELECT 1 FROM projeto.account "
							+ "		WHERE username = '" + username + "');";
		
		return executeQuery_SingleColumn(query).equals("f");
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
		LocalDate createdDate = LocalDate.now();
		
		String query = "INSERT INTO projeto.account (username, password, email, register_date, first_name, last_name)" +
					   "	VALUES ('" + username + "', '" + password + "', '" + email +
					   "	', '" + createdDate + "', '" + first_name + "', '" + last_name + "');";
		
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
		
		try (Connection connection = getConnection()){
			if (connection != null) {
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				if (rs.next()) {
					String email = rs.getString("email");
					String firstName = rs.getString("first_name");
					String lastName = rs.getString("last_name");
					return new Account(username, password, firstName + " " + lastName, email);
				}
			} else {
				System.out.println("Connection failed");
			}
		}
		
		return null;
	}
	
	/**
	 * Changes the username in the database
	 * 
	 * @param old_username String with the old username
	 * @param new_username String with the new username
	 * @return Either True if it succeeds or False if it fails
	 */
	public static boolean changeUserName(String old_username, String new_username) throws SQLException{
		String query = "UPDATE projeto.account SET username='" + new_username + "'" +
					   " WHERE username='"+ old_username + "'";
		
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
		String query = "UPDATE projeto.account SET password='" + new_password +"'" +
					   " WHERE username='"+ username + "'";
		
		if (executeUpdate(query) > 0)
			return true;
		else
			return false;
	}
	
	/**
	 * Gets the user's first name from the database
	 * 
	 * @param username String with user's username
	 * @param password String with user's password
	 * @return String with user's first name
	 */
	/*public static String getFirstName(String username, String password) throws SQLException{
		String query = 	  "SELECT first_name FROM projeto.account "
						+ "WHERE username='" + username + "' AND password='" + password + "';";
		
		return executeQuery_SingleColumn(query);
	}*/
	
	/**
	 * Gets the user's last name from the database
	 * 
	 * @param username String with user's username
	 * @param password String with user's password
	 * @return String with user's last name
	 */
	/*public static String getLastName(String username, String password) throws SQLException{
		String query = 	  "SELECT last_name FROM projeto.account "
						+ "WHERE username='" + username + "' AND password='" + password + "';";
		
		return executeQuery_SingleColumn(query);
	}*/
	
	/**
	 * Gets the user's username from the database
	 * 
	 * @param email String with user's email
	 * @param password String with user's password
	 * @return String with user's username
	 */
	/*public static String getUsername(String email, String password) throws SQLException{
		String query = 	  "SELECT username FROM projeto.account "
						+ "WHERE email='" + email + "' AND password='" + password + "';";
			
		return executeQuery_SingleColumn(query);
	}*/
	
	/**
	 * Gets the user's register Date from the database
	 * 
	 * @param username String with user's username
	 * @param password String with user's password
	 * @return Date with user's register date
	 */
	/*public static LocalDate getRegisterDate(String username, String password) throws SQLException{
		String query = 	  "SELECT username FROM projeto.account "
						+ "WHERE username='" + username + "' AND password='" + password + "';";
		
		String dateString = executeQuery_SingleColumn(query);
		LocalDate register_date = null;
		
		if (dateString != null) {
			register_date = LocalDate.parse(dateString);
		}
		return register_date;
	}*/
}