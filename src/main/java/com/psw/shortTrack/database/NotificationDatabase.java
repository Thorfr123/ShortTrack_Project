package com.psw.shortTrack.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.postgresql.util.PSQLException;
import org.postgresql.util.PSQLState;

import com.psw.shortTrack.data.Account;
import com.psw.shortTrack.data.Notification;

public class NotificationDatabase extends Database{

	//TODO: Maybe change returns
	/**
	 * Sends a new notification to the database
	 * 
	 * @param notif New notification to add
	 * 
	 * @throws AccountNotFound If at least one of the accounts (source or destination) don't exist
	 * @throws SQLException If there is a connection error
	 */
	public static void createNotification(Notification notif) throws SQLException, AccountNotFound {
		try {
			
			notif.setId(Integer.parseInt(executeQueryReturnSingleColumn(
					"INSERT INTO projeto.notifications (type, source, destination, message)\r\n"
					+ "VALUES (" + toSQL((int)notif.getType()) + "," + toSQL((String)notif.getSource().getEmail()) + "," 
					+ toSQL((String)notif.getDestination().getEmail()) + "," + toSQL((String)notif.getMessage()) + ")\r\n"
					+ "RETURNING id;")));
			
		} catch (PSQLException psql) {
			if (psql.getSQLState().equals(PSQLState.FOREIGN_KEY_VIOLATION.getState())) {
				throw new AccountNotFound(psql);
			}
			throw psql;
		}
	}
	
	/**
	 * Deletes a notification from the database
	 * 
	 * @param id Notification's id
	 * @return (True) If it succeeds; (False) Otherwise (Notification didn't exist)
	 * @throws SQLException
	 */
	public static boolean deleteNotification(int id) throws SQLException {
		return (executeUpdate(
				"DELETE FROM projeto.notifications WHERE id=" + toSQL(id) + ";"
			) > 0);
	}
	
	/**
	 * Returns all the notifications from the database that have the user as destination
	 * 
	 * @param user Notifications' Destination Account
	 * @return ArrayList with every notifications found
	 * 
	 * @throws SQLException If there is a connection error
	 */
	public static ArrayList<Notification> getAllNotifications(Account user) throws SQLException {
		
		try (Connection connection = getConnection()) {
			if (connection == null)
				throw new SQLException("Connection error");
			
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(
				"SELECT id, type, source, message FROM projeto.notifications WHERE destination=" + toSQL((String)user.getEmail()) + ";"
			);
			
			ArrayList<Notification> allNotif = new ArrayList<Notification>();
			
			while (rs.next()) {
				Account dest = AccountsDatabase.getAccount(rs.getString("source"));
				if (dest == null) {
					continue;
				}
				
				allNotif.add(new Notification(	rs.getInt("id"),
												rs.getInt("type"),
												dest,
												user,
												rs.getString("message")));
			}
			return allNotif;
		}
		
	}
}
