package com.psw.shortTrack.gui;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import com.psw.shortTrack.data.List;
import com.psw.shortTrack.data.Task;
import com.psw.shortTrack.data.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.paint.Color;

public class ControllerEditTaskScene {
	
	@FXML
	private TextField taskNameField;
	@FXML
	private TextArea descriptionField;
	@FXML
	private CheckBox checkButton;
	@FXML 
	private DatePicker dueDateField;
	@FXML
	private Label notificationLabel;

	private Task task;
	private List list;
	private List search;
	
	public void initData(Task task, List search) {
		this.task = task;
		this.search = search;
		
		list = User.getList(task.getParentID());
		
		taskNameField.setText(task.getName());
		descriptionField.setText(task.getDescription());
		dueDateField.setValue(task.getDeadlineDate());
		
		if(task.chekCompleted()) {
			checkButton.setSelected(true);
			checkButton.setText("Completed");
		}
		else {
			checkButton.setSelected(false);
			checkButton.setText("To be started");
		}
		
    }
	
	public void delete(ActionEvent e) throws IOException {
		
		removeErrorNotifications();
		
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Delete Task");
		alert.setHeaderText("You're about to delete this task and lose this data!");
		alert.setContentText("Are you sure you really want to delete the task?");

		if(alert.showAndWait().get() == ButtonType.OK){
			
			list.removeTask(task);
			
			if(search != null)
				search.removeTask(task);
			
			task = null;
			
			App.loadMainScene();
			
		}
		
	}
	
	public void save(ActionEvent e) throws IOException {
		
		removeErrorNotifications();
		
		String newTaskName = taskNameField.getText();
		if(newTaskName.isBlank()) {
			showNotification("The Task needs a name!");
			taskNameField.getStyleClass().add("error");
			return;
		}
			
		if(!newTaskName.equals(task.getName()) && list.checkName(newTaskName)) {
			showNotification("Already exist a task with that name!");
			taskNameField.getStyleClass().add("error");
			return;
		}
		
		String newDescription = descriptionField.getText();
		LocalDate newDeadline = dueDateField.getValue();
				
		task.setName(newTaskName);
		task.setDescription(newDescription);
		task.setDeadline(newDeadline);
		task.setCompleted(checkButton.isSelected());
		
		App.loadMainScene();
		
	}
	
	public void cancel(ActionEvent e) throws IOException {
		
		removeErrorNotifications();
		
		// Cancel the complete task creation
		if(task.getName().isBlank())
			list.removeTask(task);
		
		App.loadMainScene();
		
	}
	
	public void changeState(ActionEvent e) {
		
		if(checkButton.isSelected())
			checkButton.setText("Completed");
		else
			checkButton.setText("To be started");
		
	}
	
	public void clearDeadline(ActionEvent e) {
		
		dueDateField.setValue(null);
		
	}
	
	private void removeErrorNotifications() {
		
		taskNameField.getStyleClass().removeAll(Collections.singleton("error")); 
		notificationLabel.setVisible(false);
		
	}
	
	private void showNotification(String notification) {
		
		notificationLabel.setText(notification);
		notificationLabel.setTextFill(Color.RED);
		notificationLabel.setVisible(true);
		
	}
	
}
