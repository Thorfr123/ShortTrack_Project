package com.psw.shortTrack.gui;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collections;

import com.psw.shortTrack.data.Account;
import com.psw.shortTrack.data.Group;
import com.psw.shortTrack.data.GroupTask;
import com.psw.shortTrack.data.Notification;
import com.psw.shortTrack.data.Notification.NotificationType;
import com.psw.shortTrack.data.SearchList;
import com.psw.shortTrack.data.TaskOrganizer;
import com.psw.shortTrack.data.User;
import com.psw.shortTrack.database.GroupTasksDatabase;
import com.psw.shortTrack.database.NotificationDatabase;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;

public class ControllerEditGroupTaskScene {
	
	@FXML
	private TextField taskNameField;
	@FXML
	private TextArea descriptionField;
	@FXML
	private CheckBox checkButton;
	@FXML 
	private DatePicker dueDateField;
	@FXML
	private ChoiceBox<Account> assignedToBox;
	@FXML
	private Label notificationLabel;
	@FXML
	private Button clearButton;
	@FXML
	private	HBox editGroupTaskBox;
	@FXML
	private Button deleteButton;
	@FXML
	private	HBox buttonsBox;

	private GroupTask task;
	private Group group;
	private TaskOrganizer loadList;
	
	public void initData(GroupTask task, TaskOrganizer loadList) {
		this.task = task;
		this.loadList = loadList;
		
		group = User.getGroup(task.getParentID());
		
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
		
		assignedToBox.getItems().add(GroupTask.nobody);
		assignedToBox.getItems().addAll(group.getMemberAccounts());
		assignedToBox.getItems().add(group.getManagerAccount());
		
		assignedToBox.setValue(task.getAssignedToAccount());
		
		if(!group.getManagerEmail().equals(User.getAccount().getEmail())) {
			taskNameField.setDisable(true);
			taskNameField.setOpacity(1);
			dueDateField.setDisable(true);
			assignedToBox.setDisable(true);

			editGroupTaskBox.getChildren().remove(clearButton);
			buttonsBox.getChildren().remove(deleteButton);
		}
		
    }
	
	//TODO: Show a message to the user so that he can see when the notification was sent
	//TODO: The button only makes sense to the manager if the task is his
	//		-> Remove the button for the manager
	//		Or
	//		-> Check if is one of is tasks before sending the notification
	public void askHelp(ActionEvent e) {
		
		try {
			
			NotificationDatabase.clearHelpRequests(task.getID());
			
			if (!group.getManagerEmail().equals(User.getAccount().getEmail())) {
				Notification ask = new Notification(NotificationType.askForHelp,
													User.getAccount(),
													group.getManagerAccount(),
													group,
													task);
				NotificationDatabase.createNotification(ask);
			}
			
			for (Account member : group.getMemberAccounts()) {
				if (member.getEmail().equals(User.getAccount().getEmail()))
					continue;
				
				Notification ask = new Notification(NotificationType.askForHelp,
													User.getAccount(),
													member,
													group,
													task);
				
				NotificationDatabase.createNotification(ask);
				
			}
		} catch (SQLException sqle) {
			App.connectionErrorMessage();
		}
		
	}
	
	public void delete(ActionEvent e) {
		
		removeErrorNotifications();
		
		try {
			GroupTasksDatabase.deleteTask(task.getID());
		} catch (SQLException exception) {
			App.connectionErrorMessage();
			return;
		}
		
		group.removeTask(task);
		
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
			
		if(!newTaskName.equals(task.getName()) && group.checkName(newTaskName)) {
			showNotification("Already exist a task with that name!");
			taskNameField.getStyleClass().add("error");
			return;
		}
		
		String newDescription = descriptionField.getText();
		LocalDate newDeadline = dueDateField.getValue();
		Account newAssignedTo = assignedToBox.getValue();
		
		try {
			GroupTasksDatabase.updateTask(task.getID(), newTaskName, newDescription, newDeadline, checkButton.isSelected());
			
			if (newAssignedTo.equals(GroupTask.nobody)) {
				GroupTasksDatabase.changeAssignedTo(task.getID(), null);
			}
			else if (!newAssignedTo.getEmail().equals(task.getAssignedToEmail())) {
				GroupTasksDatabase.changeAssignedTo(task.getID(), newAssignedTo.getEmail());
			}
			
		} catch (SQLException exception) {
			System.out.println(exception);
			App.connectionErrorMessage();
			return;
		}
		
		task.setName(newTaskName);
		task.setDescription(newDescription);
		task.setDeadline(newDeadline);
		task.setCompleted(checkButton.isSelected());
		task.setAssignedTo(newAssignedTo);
		
		App.loadMainScene();
		
	}
	
	public void cancel(ActionEvent e) {
		
		removeErrorNotifications();
		
		// Cancel the complete task creation
		if(task.getName().isBlank()) {
			try {
				GroupTasksDatabase.deleteTask(task.getID());
			} catch (SQLException exception) {
				App.connectionErrorMessage();
				return;
			}
			group.removeTask(task);
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
