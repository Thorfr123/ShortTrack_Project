package com.psw.shortTrack.gui;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;

import com.psw.shortTrack.data.Group;
import com.psw.shortTrack.data.User;
import com.psw.shortTrack.database.GroupsDatabase;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.control.Alert.AlertType;

public class ControllerEditGroupScene {
	
	@FXML
	private TextField groupNameField;
	@FXML
	private TextField memberTextField;
	@FXML
	private Label notificationLabel;
	
	private Group group;
	
	public void initData(Group group) {

		this.group = group;
		
		groupNameField.setText(group.getName());
		
    }
	
	public void delete(ActionEvent e) throws IOException {
		
		removeErrorNotifications();
		
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Delete Group");
		alert.setHeaderText("You're about to delete this group and lose this data!");
		alert.setContentText("Are you sure you really want to delete the group?");

		if(alert.showAndWait().get() == ButtonType.OK){
			
			/*try {
				GroupsDatabase.deleteGroup(group.getID());
			} catch (SQLException exception) {
				showNotification("Error! Please, check your connection");
				return;
			}*/
			
			User.getGroups().remove(group);
			group = null;
			
			App.loadMainScene();
			
		}
		
	}
	
	public void save(ActionEvent e) throws IOException {
		
		removeErrorNotifications();
		
		System.out.println("save - Not Working!");
		
		App.loadMainScene();
		
	}
	
	public void cancel(ActionEvent e) throws IOException {
		
		removeErrorNotifications();
		
		System.out.println("cancel - Not Working!");
		
		App.loadMainScene();
		
	}
	
	public void add(ActionEvent e) throws IOException {
		
		removeErrorNotifications();
		
		System.out.println("add - Not Working!");
		
	}
	
	public void remove(ActionEvent e) throws IOException {
		
		removeErrorNotifications();
		
		System.out.println("remove - Not Working!");
		
	}
	
	private void showNotification(String notification) {
		
		notificationLabel.setText(notification);
		notificationLabel.setTextFill(Color.RED);
		notificationLabel.setVisible(true);
		
	}
	
	private void removeErrorNotifications() {
		
		groupNameField.getStyleClass().removeAll(Collections.singleton("error")); 
		notificationLabel.setVisible(false);
		
	}
	
}
