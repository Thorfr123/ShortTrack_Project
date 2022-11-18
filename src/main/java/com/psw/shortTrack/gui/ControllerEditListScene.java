package com.psw.shortTrack.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import com.psw.shortTrack.data.List;
import com.psw.shortTrack.data.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.control.Alert.AlertType;

public class ControllerEditListScene {
	
	@FXML
	private TextField listNameField;
	@FXML
	private Label notificationLabel;
	
	private ArrayList<List> arrayList;
	private List list;
	
	public void initData(List list) {

		this.arrayList = User.getLists();
		this.list = list;
		
		listNameField.setText(list.getName());
		
    }
	
	public void delete(ActionEvent e) throws IOException {
		
		removeErrorNotifications();
		
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Delete List");
		alert.setHeaderText("You're about to delete this list and lose this data!");
		alert.setContentText("Are you sure you really want to delete the list?");

		if(alert.showAndWait().get() == ButtonType.OK){
			
			arrayList.remove(list);
			list = null;
			
			App.loadMainScene();
			
		}
		
	}
	
	public void save(ActionEvent e) throws IOException {
		
		removeErrorNotifications();
		
		String newListName = listNameField.getText();
		if(newListName.isBlank()) {
			showNotification("The List needs a name!");
			listNameField.getStyleClass().add("error");
			return;
		}
		
		if (!newListName.equals(list.getName())) {
			for (List l : arrayList) {
				if (l.getName().equals(newListName)) {
					showNotification("Already exist a task with that name!");
					return;
				}
			}
		}
				
		list.setName(newListName);
		
		App.loadMainScene();
		
	}
	
	public void cancel(ActionEvent e) throws IOException {
		
		removeErrorNotifications();
		
		App.loadMainScene();
		
	}
	
	
	private void showNotification(String notification) {
		
		notificationLabel.setText(notification);
		notificationLabel.setTextFill(Color.RED);
		notificationLabel.setVisible(true);
		
	}
	
	private void removeErrorNotifications() {
		
		listNameField.getStyleClass().removeAll(Collections.singleton("error")); 
		notificationLabel.setVisible(false);
		
	}
	
}
