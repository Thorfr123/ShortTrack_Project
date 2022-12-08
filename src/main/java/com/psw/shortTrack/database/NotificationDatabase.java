package com.psw.shortTrack.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.postgresql.util.PSQLException;

import com.psw.shortTrack.data.Account;
import com.psw.shortTrack.data.Notification;
import com.psw.shortTrack.data.Notification.NotificationType;

public class NotificationDatabase extends Database{

	/**
	 * Sends a new notification to the database
	 * 
	 * @param notif New notification to add
	 * @return (True) Success; (False) Error - If at least one of the accounts (source or destination) don't exist
	 * 
	 * @throws SQLException If there is a connection error
	 */
	public static boolean createNotification(Notification notif) throws SQLException {
		
		try {	
			notif.setId(executeQueryReturnInt(
				"INSERT INTO projeto.notifications (type, source, destination, group_id)\r\n"
				+ "VALUES (" + toSQL((int)notif.getTypeAsInt()) + "," + toSQL((String)notif.getSource().getEmail()) + "," 
				+ toSQL((String)notif.getDestination().getEmail()) + "," + toSQL(notif.getGroup_id()) + ")\r\n"
				+ "RETURNING id;")
			);
			return true;
		} catch (PSQLException psql) {
			if (psql.getSQLState().startsWith("23")) {
				return false;
			}
			throw psql;
		}
		
	}
	
	/**
	 * Deletes a notification from the database
	 * 
	 * @param id Notification's id
	 * @return (True) If it succeeds; (False) Otherwise (Notification didn't exist)
	 * 
	 * @throws SQLException If there is a connection error
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
			
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(
				"SELECT notifications.id, type, source, group_id, groups.name AS group_name\r\n"
				+ "FROM projeto.notifications JOIN projeto.groups ON group_id=groups.id\r\n"
				+ "WHERE destination=" + toSQL((String)user.getEmail()) + ";"
			);
			
			ArrayList<Notification> allNotif = new ArrayList<Notification>();
			while (rs.next()) {
				Account source = AccountsDatabase.getAccount(rs.getString("source"));
				if (source != null) {
					allNotif.add(new Notification(	rs.getInt("id"),
													rs.getInt("type"),
													source,
													user,
													rs.getString("group_name"),
													rs.getInt("group_id")));
				}
			}
			return allNotif;
		}
		
	}
	
	/**
	 * Checks if there is an invitation sent to destination to this group.
	 * 
	 * @param destination Destination User Email
	 * @param group_id Group's id
	 * @return (True) There is an invitation already; (False) There isn't
	 * 
	 * @throws SQLException If there is a connection error
	 */
	public static boolean checkInvitation(String destination, int group_id) throws SQLException {
		
		return executeQueryReturnBoolean(
				"SELECT EXISTS (SELECT 1 FROM projeto.notifications "
				+ "WHERE type=" + toSQL(NotificationType.invitateToGroup.toInt()) +"AND destination=" + toSQL((String)destination) + " AND group_id=" + group_id + ");"
		);
		
	}
}
