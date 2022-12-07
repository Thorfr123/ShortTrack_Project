package com.psw.shortTrack.database;

import java.sql.*;

import org.postgresql.util.PSQLException;
import org.postgresql.util.PSQLState;

import com.psw.shortTrack.data.Account;

public class AccountsDatabase extends Database{

	/**
	 * Verifies if the user can login. Meaning email and password matches.
	 * If email doesn't exist in database, it returns false same way.
	 * 
	 * @param email String with user's email
	 * @param password String with user's password
	 * @return True - Both user and password are correct; False - Otherwise
	 * @throws SQLException If there's a network error
	 */
	public static boolean checkLogin(String email, String password) throws SQLException{
		
		return executeQueryReturnBoolean(
			"SELECT EXISTS (SELECT 1 FROM projeto.account\r\n"
			+ "WHERE email=" + toSQL((String)email) + " AND password=" + toSQL((String)password) + ");"
		);
		
	}
	
	/**
	 * Checks if the email is already in use.
	 * It doesn't verify consistency of the email, for example, if you try to check with email "example" it will return true.
	 * 
	 * @param email String with user's email
	 * @return True - This email is available; False - This email is already in use
	 * @throws SQLException If there is a network error
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
	 * @return (True) - if the account is created; (False) - If the email is already in use
	 * @throws SQLException If there is a network error
	 */
	public static boolean createAccount(Account account, String password) throws SQLException {
		
		try {
			
			return (executeUpdate(
				"INSERT INTO projeto.account (email, password, name)\r\n"
				+ "VALUES (" + toSQL((String)account.getEmail()) + "," + toSQL((String)password) + "," 
				+ toSQL((String)account.getName()) + ");"
			) > 0);
			
		} catch (PSQLException sqle) {
			if (sqle.getSQLState().equals(PSQLState.UNIQUE_VIOLATION.getState())) {
				return false;
			}
			throw sqle;
		}
	}
	
	/**
	 * Deletes an account from the database.
	 * If it returns false, it means the email doesn't exist.
	 * 
	 * @param email String with user's email
	 * @return Either (True) If it succeeds or (False) If it fails (Account didn't exist)
	 * @throws SQLException If there is a network error
	 */
	public static boolean deleteAccount(String email) throws SQLException {
		
		return (executeUpdate(
			"DELETE FROM projeto.account WHERE email=" + toSQL((String)email) + ";"
		) > 0);
		
	}
	
	/**
	 * Gets account data from the database.
	 * 
	 * @param email String with user's email
	 * @return Desired account, with user's email and name, or null If email doesn't exist
	 * @throws SQLException If there is a network error
	 */
	public static Account getAccount (String email) throws SQLException{
		
		try (Connection connection = getConnection()){
			
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(
				"SELECT name FROM projeto.account WHERE email=" + toSQL((String)email) + ";"
			);
			
			if (rs.next())
				return new Account(email, rs.getString("name"));
			else
				return null;
		}
		
	}
	
	/**
	 * Changes the password of the user in the database.
	 * 
	 * @param email String with the email
	 * @param old_password String with the old password
	 * @param new_password String with the new password
	 * @return Either (True) If it succeeds or (False) If it fails (wrong password or email doesn't exist)
	 * 
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
	 * @return Either (True) if it succeeds or (False) If it fails (Email doesn't exist)
	 * 
	 * @throws SQLException If there is a network error
	 */
	public static boolean changeName(String email, String new_name) throws SQLException {
		
		return (executeUpdate(
			"UPDATE projeto.account SET name=" + toSQL((String)new_name) + " WHERE email=" + toSQL((String)email) + ";"
		) > 0);
		
	}

	// TODO: Exception when new email is in use
	/**
	 * Changes the email of the user in the database.
	 * 
	 * @param email String with user's current email
	 * @param newEmail String with user's new email
	 * @return Either (True) Success; (False) Something failed (Email doesn't exist)
	 * 
	 * @throws PSQLException (UNIQUE_VIOLATION) If new email is already in use
	 * @throws SQLException If there is a network error
	 */
	public static boolean changeEmail(String email, String newEmail) throws AccountNotFoundException, SQLException{
		try {
			return (executeUpdate(
				"UPDATE projeto.account SET email=" + toSQL((String)newEmail) + " WHERE email=" + toSQL((String)email) + ";"
				+ "UPDATE projeto.groups SET members=(SELECT array_replace(members," + toSQL((String)email) + "," + toSQL((String)newEmail) + "));"
			) > 0);
		} catch (PSQLException psql) {
			if (psql.getSQLState().equals(PSQLState.UNIQUE_VIOLATION.getState())) {
				throw new AccountNotFoundException();
			}
			throw psql;
		}
	}
}