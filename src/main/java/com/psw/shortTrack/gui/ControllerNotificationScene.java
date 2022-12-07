package com.psw.shortTrack.gui;

import java.io.IOException;
import java.sql.SQLException;

import com.psw.shortTrack.data.Notification;
import com.psw.shortTrack.data.Notification.NotificationType;
import com.psw.shortTrack.data.User;
import com.psw.shortTrack.database.GroupsDatabase;
import com.psw.shortTrack.database.NotificationDatabase;

import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ControllerNotificationScene {
	
	@FXML
	private VBox notificationList;
	
	@FXML
    public void initialize() {
		
		for(Notification notification : User.getNotifications()) {
			
			if(notification.getType() == NotificationType.invitateToGroup) {
				InviteNotificationBox notificationBar = new InviteNotificationBox(notification);
				
				notificationBar.getAcceptButton().setOnAction(event -> {
					accept(notification, notificationBar);
		        });
				notificationBar.getRefuseButton().setOnAction(event -> {
					decline(notification, notificationBar);
		        });
				
				notificationList.getChildren().add(notificationBar);
				
			}
			else {
				SimpleNotificationBox notificationBar = new SimpleNotificationBox(notification);
				
				notificationBar.getOkButton().setOnAction(event -> {
					ok(notification, notificationBar);
		        });
				
				notificationList.getChildren().add(notificationBar);
			}
		}
    }
	
	public void close() throws IOException {
		
		App.loadMainScene();
		
	}
	
	public void ok(Notification notification, HBox notificationBar) {
		
		try {
			deleteNotification(notification, notificationBar);
		} catch (SQLException e) {
			App.connectionErrorMessage();
			return;
		}
		
	}
	
	public void accept(Notification notification, HBox notificationBar) {
		
		try {
			
			GroupsDatabase.addMember(notification.getGroup_id(), User.getAccount());
			deleteNotification(notification, notificationBar);
			
			Notification accepted = new Notification(NotificationType.acceptedInviteToGroup, User.getAccount(), notification.getSource(), notification.getGroup());
			NotificationDatabase.createNotification(accepted);
			
		} catch (SQLException sqle) {
			App.connectionErrorMessage();
			return;
		}
		
	}

	public void decline(Notification notification, HBox notificationBar) {
		
		try {
			deleteNotification(notification, notificationBar);
		} catch (SQLException e) {
			App.connectionErrorMessage();
			return;
		}
		
	}
	
	private void deleteNotification(Notification notification, HBox notificationBar) throws SQLException{
		
		NotificationDatabase.deleteNotification(notification.getId());
		notificationList.getChildren().remove(notificationBar);
		
	}
	
}
