package com.psw.shortTrack.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.postgresql.util.PSQLException;

import com.psw.shortTrack.data.Account;
import com.psw.shortTrack.data.Group;
import com.psw.shortTrack.data.GroupTask;
import com.psw.shortTrack.data.Notification;
import com.psw.shortTrack.data.Notification.NotificationType;
import com.psw.shortTrack.data.Task;

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
			
			String group_id = notif.getRefGroup() == null ? null : toSQL(notif.getRefGroup().getID());
			String task_id = notif.getRefTask() == null ? null : toSQL(notif.getRefTask().getID());
			
			notif.setId(executeQueryReturnInt(
				"INSERT INTO projeto.notifications (type, source, destination, group_id, task_id)\r\n"
				+ "VALUES (" + toSQL((int)notif.getTypeAsInt()) + "," + toSQL((String)notif.getSource().getEmail()) + "," 
				+ toSQL((String)notif.getDestination().getEmail()) + "," + group_id + "," + task_id + ")\r\n"
				+ "RETURNING id;")
			);
			return true;
		} catch (PSQLException psql) {
			if (psql.getSQLState().startsWith("23")) {
				System.out.println(psql);
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
				"SELECT notifications.id,"
				+ "		type,"
				+ "		source,"
				+ "		s_acc.name AS source_name,"
				+ "		notifications.group_id,"
				+ "		groups.name AS group_name,"
				+ "		task_id,"
				+ "		group_tasks.name AS task_name\r\n"
				+ "FROM projeto.notifications\r\n"
				+ "LEFT JOIN projeto.groups ON group_id=groups.id\r\n"
				+ "LEFT JOIN projeto.group_tasks ON task_id=group_tasks.id\r\n"
				+ "LEFT JOIN projeto.account s_acc ON source=s_acc.email\r\n"
				+ "WHERE destination=" + toSQL((String)user.getEmail()) + ";"
			);
			
			ArrayList<Notification> allNotif = new ArrayList<Notification>();
			
			while (rs.next()) {
				
				Account source = new Account(rs.getString("source"), rs.getString("source_name"));
				int group_id = rs.getInt("group_id");
				int task_id = rs.getInt("task_id");
				Group ref_group = group_id != 0 ? new Group(group_id, rs.getString("group_name")) : null;
				Task ref_task = task_id != 0 ? new GroupTask(task_id, rs.getString("task_name")) : null;
				
				allNotif.add(new Notification(	rs.getInt("id"),
												rs.getInt("type"),
												source,
												user,
												ref_group,
												ref_task));
				
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
				+ "WHERE type=" + toSQL(NotificationType.invitateToGroup.toInt()) +" AND destination=" + toSQL((String)destination) + " AND group_id=" + group_id + ");"
		);
		
	}
	
	/**
	 * Checks if there is a help request in a task
	 * 
	 * @param task_id Task to check
	 * @return (True) Exists; (False) Otherwise
	 * 
	 * @throws SQLException If there was a connection error
	 */
	public static boolean checkHelpRequest(int task_id) throws SQLException {
		
		return executeQueryReturnBoolean(
			"SELECT EXISTS (SELECT 1 FROM projeto.notifications\r\n"
			+ "WHERE type=" + toSQL(NotificationType.askForHelp.toInt()) + " AND task_id=" + toSQL(task_id) + ");"	
		);
		
	}
	
	/**
	 * Deletes all the help request of a task
	 * 
	 * @param task_id
	 * @return (True) Deleted; (False) Deleted nothing (didn't exist any)
	 * 
	 * @throws SQLException If there was a connection error
	 */
	public static boolean clearHelpRequests(int task_id) throws SQLException {
		
		return executeUpdate(
				"DELETE FROM projeto.notifications WHERE task_id=" + toSQL(task_id) + ";"
		) > 0;
		
	}
}
