package com.psw.shortTrack.database;

import java.sql.*;

import com.psw.shortTrack.data.Account;

public class AccountsDatabase extends Database{
	
	/**
	 * Verifies if the user can login
	 * 
	 * @param email String with user's email
	 * @param password String with user's password
	 * @return True - Both user and password are correct; False - Otherwise
	 * @throws SQLException If there's a network error
	 */
	public static boolean checkLogin(String email, String password) throws SQLException{
		String query = "SELECT EXISTS (SELECT 1 FROM projeto.account WHERE email='" + email + "' AND password='" + password + "');";
		
		return (executeQueryReturnSingleColumn(query).equals("t"));
	}
	
	/**
	 * Checks if the email is already in use
	 * 
	 * @param email String with user's email
	 * @return True - This email isn't in use; False - This email is already in use
	 * @throws SQLException If there is a network error
	 * 
	 */
	public static boolean checkEmail(String email) throws SQLException{
		String query = "SELECT EXISTS(SELECT 1 FROM projeto.account WHERE email = '" + email + "');";
		
		return (executeQueryReturnSingleColumn(query).equals("f"));
	}
	
	/**
	 * Creates a new account in the database
	 * 
	 * @param account User's new account
	 * @return (True) - if the account is created; (False) - if the account isn't created (i.e. it's already in use)
	 * @throws SQLException If there is a network error
	 */
	public static boolean createAccount(Account account) throws SQLException {		
		String query = "INSERT INTO projeto.account (email, password, name)"
					   + "	VALUES ('" + account.getEmail() + "', '" + account.getPassword() + "','" + account.getName() + "');";
		
		return (executeUpdate(query) > 0);
	}
	
	/**
	 * Deletes an account from the database
	 * 
	 * @param email String with user's email
	 * @return Either True if it succeeds or False if it fails
	 * @throws SQLException If there is a network error
	 */
	public static boolean deleteAccount(String email) throws SQLException{
		String query = "DELETE FROM projeto.account WHERE email='" + email + "'";
		
		return (executeUpdate(query) > 0);
	}
	
	/**
	 * Gets account data from the database
	 * 
	 * @param email String with user's email
	 * @param password String with user's password
	 * @return Desired account or null if it fails
	 * @throws SQLException If there is a network error
	 */
	public static Account getAccount (String email, String password) throws SQLException{
		String query = 	  "SELECT email, name FROM projeto.account "
				+ "WHERE email='" + email + "' AND password='" + password + "';";
		
		try (Connection connection = getConnection()){
			if (connection != null) {
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				if (rs.next()) {
					return new Account( rs.getString("email"), rs.getString("name"));
				}
			} else {
				throw new SQLException("Connection failed");
			}
		}
		
		return null;
	}
	
	/**
	 * Changes the password of the user in the database
	 * 
	 * @param email String with the email
	 * @param new_password String with the new password
	 * @return Either (True) if it succeeds or (False) if it fails
	 * @throws SQLException If there is a network error
	 */
	public static boolean changePassword(String email, String old_password, String new_password) throws SQLException{
		String query = "UPDATE projeto.account SET password='" + new_password + "'\r\n"
						+"WHERE email='"+ email + "' AND password='" + old_password +"';";
		
		return (executeUpdate(query) > 0);
	}
	
	/**
	 * Changes the name of the user in the database
	 * 
	 * @param email String with the email
	 * @param new_name String with the new name
	 * @return Either (True) if it succeeds or (False) if it fails
	 * @throws SQLException If there is a network error
	 */
	public static boolean changeName(String email, String new_name) throws SQLException {
		String query = "UPDATE projeto.account SET name='" + new_name + "'\r\n"
						+"WHERE email='" + email + "';";
		
		return (executeUpdate(query) > 0);
	}
	
}