package com.psw.shortTrack.database;

import java.sql.*;

import com.psw.shortTrack.data.Account;

public class AccountsDatabase extends Database{
	
	/**
	 * Verifies if the user can login.
	 * 
	 * @param email String with user's email
	 * @param password String with user's password
	 * @return True - Both user and password are correct; False - Otherwise
	 * @throws SQLException If there's a network error
	 */
	public static boolean checkLogin(String email, String password) throws SQLException{		
		return executeQueryReturnBoolean(
			"SELECT EXISTS (SELECT 1 FROM projeto.account WHERE email=" + toSQL((String)email)
			+ " AND password=" + toSQL((String)password) + ");"
		);
	}
	
	/**
	 * Checks if the email is already in use
	 * 
	 * @param email String with user's email
	 * @return True - This email is available; False - This email is already in use
	 * @throws SQLException If there is a network error
	 * 
	 */
	public static boolean checkEmail(String email) throws SQLException{
		return executeQueryReturnBoolean(
			"SELECT NOT EXISTS(SELECT 1 FROM projeto.account WHERE email=" + toSQL((String)email) + ");"
		);
	}
	
	/**
	 * Creates a new account in the database
	 * 
	 * @param account User's new account
	 * @return (True) - if the account is created; (False) - if the account isn't created (i.e. it's already in use)
	 * @throws SQLException If there is a network error
	 */
	public static boolean createAccount(Account account) throws SQLException {		
		return (executeUpdate(
			"INSERT INTO projeto.account (email, password, name)\r\n"
			+ "VALUES (" + toSQL((String)account.getEmail()) + "," + toSQL((String)account.getPassword()) + "," 
			+ toSQL((String)account.getName()) + ");"
		) > 0);
	}
	
	/**
	 * Deletes an account from the database
	 * 
	 * @param email String with user's email
	 * @return Either True if it succeeds or False if it fails
	 * @throws SQLException If there is a network error
	 */
	public static boolean deleteAccount(String email) throws SQLException{		
		return (executeUpdate(
			"DELETE FROM projeto.account WHERE email=" + toSQL((String)email) + ";"
		) > 0);
	}
	
	// TODO: Delete this method
	public static Account getAccount (String email, String password) throws SQLException{
		return getAccount(email);
	}
	
	/**
	 * Gets account data from the database
	 * 
	 * @param email String with user's email
	 * @return Desired account, with user's email and name, or null if it fails
	 * @throws SQLException If there is a network error
	 */
	public static Account getAccount (String email) throws SQLException{
				
		try (Connection connection = getConnection()){
			if (connection != null) {
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(
					"SELECT name FROM projeto.account WHERE email=" + toSQL((String)email) + ";"
				);
				if (rs.next()) {
					return new Account( email, rs.getString("name"));
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
	 * @param old_password String with the old password
	 * @param new_password String with the new password
	 * @return Either (True) if it succeeds or (False) if it fails
	 * @throws SQLException If there is a network error
	 */
	public static boolean changePassword(String email, String old_password, String new_password) throws SQLException{
		return (executeUpdate(
			"UPDATE projeto.account SET password=" + toSQL((String)new_password) + "\r\n"
			+"WHERE email="+ toSQL((String)email) + " AND password=" + toSQL((String)old_password) + ";"
		) > 0);
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
		return (executeUpdate(
			"UPDATE projeto.account SET name=" + toSQL((String)new_name) + " WHERE email=" + toSQL((String)email) + ";"
		) > 0);
	}
	
}