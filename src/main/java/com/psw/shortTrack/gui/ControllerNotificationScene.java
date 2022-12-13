package com.psw.shortTrack.gui;

import java.sql.SQLException;

import com.psw.shortTrack.data.Notification;
import com.psw.shortTrack.data.Notification.NotificationType;
import com.psw.shortTrack.data.User;
import com.psw.shortTrack.database.GroupTasksDatabase;
import com.psw.shortTrack.database.GroupsDatabase;
import com.psw.shortTrack.database.NotificationDatabase;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ControllerNotificationScene {
	
	@FXML
	private VBox notificationList;
	
	@FXML
    public void initialize() {
		
		for(Notification notification : User.getNotifications()) {
			
			if(notification.getType() == NotificationType.invitateToGroup) {
				RequestNotificationBox notificationBar = new RequestNotificationBox(notification);
				
				notificationBar.getAcceptButton().setOnAction(event -> {
					acceptGroupInvite(notification, notificationBar);
		        });
				notificationBar.getRefuseButton().setOnAction(event -> {
					acknowledge(notification, notificationBar);
		        });
				
				notificationList.getChildren().add(notificationBar);
				
			}
			else if (notification.getType() == NotificationType.askForHelp) {
				RequestNotificationBox notificationBar = new RequestNotificationBox(notification);
				
				notificationBar.getAcceptButton().setOnAction(event -> {
					acceptHelpRequest(notification, notificationBar);
		        });
				notificationBar.getRefuseButton().setOnAction(event -> {
					acknowledge(notification, notificationBar);
		        });
				
				notificationList.getChildren().add(notificationBar);
			}
			else {
				SimpleNotificationBox notificationBar = new SimpleNotificationBox(notification);
				
				notificationBar.getOkButton().setOnAction(event -> {
					acknowledge(notification, notificationBar);
		        });
				
				notificationList.getChildren().add(notificationBar);
			}
		}
    }
	
	public void close() {
		
		App.loadMainScene();
		
	}
	
	public void acknowledge(Notification notification, HBox notificationBar) {
		
		try {
			deleteNotification(notification, notificationBar);
		} catch (SQLException e) {
			App.connectionErrorMessage();
			return;
		}
		
	}
	
	public void acceptGroupInvite(Notification notification, HBox notificationBar) {
		
		try {
			
			GroupsDatabase.addMember(notification.getRefGroup().getID(), User.getAccount());
			deleteNotification(notification, notificationBar);
			
			Notification response = new Notification(	notification.getResponseType(), 
														User.getAccount(), 
														notification.getSource(), 
														notification.getRefGroup(),
														notification.getRefTask());
			NotificationDatabase.createNotification(response);
			
		} catch (SQLException sqle) {
			App.connectionErrorMessage();
		}
		
	}
	
	public void acceptHelpRequest(Notification notification, HBox notificationBar) {
		
		try {
			
			// Verifica se o pedido ainda está ativo
			if (NotificationDatabase.checkHelpRequest(notification.getRefTask().getID())) {
				
				GroupTasksDatabase.changeAssignedTo(notification.getRefTask().getID(), User.getAccount().getEmail());
				NotificationDatabase.clearHelpRequests(notification.getRefTask().getID());
				
				Notification response = new Notification(	notification.getResponseType(), 
															User.getAccount(), 
															notification.getSource(), 
															notification.getRefGroup(),
															notification.getRefTask());
				NotificationDatabase.createNotification(response);
				
			}
			else {
				
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Help request error");
				alert.setHeaderText("It seems someone in your group has already accepted this help request or it was canceled.");
				//alert.setContentText("You will be logged out of your account!");
				alert.showAndWait();
				
			}
			
			deleteNotification(notification, notificationBar);
			
		} catch (SQLException sqle) {
			
			App.connectionErrorMessage();
		
		}
		
	}
	
	private void deleteNotification(Notification notification, HBox notificationBar) throws SQLException {
		
		NotificationDatabase.deleteNotification(notification.getId());
		notificationList.getChildren().remove(notificationBar);
		
	}
	
	public void refreshPage() {
		
		try {
			User.setNotifications(NotificationDatabase.getAllNotifications(User.getAccount()));
			initialize();
		} catch (SQLException exception) {
			App.connectionErrorMessage();			
		}
		
	}
	
}
