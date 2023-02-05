package com.psw.shortTrack.gui;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collections;
import com.psw.shortTrack.data.List;
import com.psw.shortTrack.data.SearchList;
import com.psw.shortTrack.data.Task;
import com.psw.shortTrack.data.TaskOrganizer;
import com.psw.shortTrack.data.User;
import com.psw.shortTrack.database.PersonalTasksDatabase;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
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
	private TaskOrganizer loadList;
	
	public void initData(Task task, TaskOrganizer loadList) {
		this.task = task;
		this.loadList = loadList;
		
		list = User.getList(task.getParentID());
		
		taskNameField.setText(task.getName());
		descriptionField.setText(task.getDescription());
		dueDateField.setValue(task.getDeadlineDate());
		
		if(task.isCompleted()) {
			checkButton.setSelected(true);
			checkButton.setText("Completed");
		}
		else {
			checkButton.setSelected(false);
			checkButton.setText("To be started");
		}
		
    }
	
	public void delete(ActionEvent e) {
		
		removeErrorNotifications();
		
		if(User.isLogedIn()) {
			try {
				PersonalTasksDatabase.deleteTask(task.getID());
			} catch (SQLException exception) {
				App.connectionErrorMessage();
				return;
			}
		}
		
		list.removeTask(task);
		
		if(loadList instanceof SearchList)
			loadList.removeTask(task);
		
		task = null;
		
		App.loadMainScene();
		
	}
	
	public void save(ActionEvent e) {
		
		removeErrorNotifications();
		
		String newTaskName = taskNameField.getText();
		if(newTaskName.isBlank()) {
			showNotification("The Task needs a name!");
			taskNameField.getStyleClass().add("error");
			return;
		}
		
		if(newTaskName.length() > 128) {
			showNotification("Task name exceeds maximum character length allowed!");
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
		
		if(User.isLogedIn()) {
			try {
				PersonalTasksDatabase.updateTask(task.getID(), newTaskName, newDescription, newDeadline, checkButton.isSelected());
			} catch (SQLException exception) {
				App.connectionErrorMessage();
				return;
			}
		}
				
		task.setName(newTaskName);
		task.setDescription(newDescription);
		task.setDeadline(newDeadline);
		task.setCompleted(checkButton.isSelected());
		
		App.loadMainScene();
		
	}
	
	public void cancel(ActionEvent e) {
		
		removeErrorNotifications();
		
		// Cancel the complete task creation		
		if(task.getName().isBlank()) {
			if(User.isLogedIn()) {
				try {
					PersonalTasksDatabase.deleteTask(task.getID());
				} catch (SQLException exception) {
					App.connectionErrorMessage();
					return;
				}
			}
			list.removeTask(task);
			task = null;
		}	
		
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
