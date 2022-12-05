package com.psw.shortTrack.gui;

import java.io.IOException;
import java.util.ArrayList;

import com.psw.shortTrack.data.Notification;
import com.psw.shortTrack.data.User;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class ControllerNotificationScene {
	
	@FXML
	VBox notificationList;
	
	ArrayList<Notification> notifications;
	
	@FXML
    public void initialize() {
		
		notifications = User.getNotifications();
		
		for(Notification n : notifications) {
			if(n.getType() == 1) {
				InviteNotificationBox notificationBar = new InviteNotificationBox(n);
				Button acceptButton = notificationBar.getAcceptButton();
				Button refuseButton = notificationBar.getRefuseButton();
				
				acceptButton.setOnAction(event -> {
					accept(event);
		        });
				refuseButton.setOnAction(event -> {
					refuse(event);
		        });
				
				notificationList.getChildren().add(notificationBar);
			}
			else if(n.getType() == 2) {
				SimpleNotificationBox notificationBar = new SimpleNotificationBox(n);
				Button okButton = notificationBar.getOkButton();
				
				okButton.setOnAction(event -> {
					ok(event);
		        });
				
				notificationList.getChildren().add(notificationBar);
			}
		}
    }
	
	public void close(ActionEvent e) throws IOException {
		
		App.loadMainScene();
		
	}
	
	public void ok(ActionEvent e) {
		
		System.out.println("ok - Not Working!");
		
	}
	
	public void accept(ActionEvent e) {
		
		System.out.println("accept - Not Working!");
		
	}

	public void refuse(ActionEvent e) {
		
		System.out.println("refuse - Not Working!");
		
	}
	
}
