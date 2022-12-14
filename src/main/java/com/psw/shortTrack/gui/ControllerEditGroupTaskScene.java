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
import com.psw.shortTrack.database.NotFoundException;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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
	@FXML
	private Button askHelpButton;

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
		
		// Ask for help
		setHelpButton();
		
    }
	
	public void askHelp(ActionEvent e) {
		
		try {
			
			NotificationDatabase.clearHelpRequests(task.getID());
			
			if (!group.getManagerEmail().equals(User.getAccount().getEmail())) {
				Notification ask = new Notification(NotificationType.askForHelp,
													User.getAccount(),
													group.getManagerAccount(),
													group,
													task);
				if (!NotificationDatabase.createNotification(ask)) {
					showNotification("There was an error trying to ask for help! Please, try again later.", true);
					return;
				}
			}
			
			for (Account member : group.getMemberAccounts()) {
				if (member.getEmail().equals(User.getAccount().getEmail()))
					continue;
				
				Notification ask = new Notification(NotificationType.askForHelp,
													User.getAccount(),
													member,
													group,
													task);
				
				if (!NotificationDatabase.createNotification(ask)) {
					showNotification("There was an error trying to ask for help! Please, try again later.", true);
					return;
				}
			}
			
			setHelpButton();
			showNotification("You have successfully request for help in this task!", false);
			
		} catch (SQLException sqle) {
			
			App.connectionErrorMessage();
		
		}
		
	}
	
	public void cancelHelpRequest() {
		
		try {
			
			if (!GroupTasksDatabase.hasPrivilege(task.getID(), User.getAccount().getEmail())) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Help cancel request error");
				alert.setHeaderText("It seems this task is no longer assigned to you, so you cannot manage it.");
				//alert.setContentText("You will be logged out of your account!");
				alert.showAndWait();
				App.loadMainScene();
				return;
			}
			else {
				NotificationDatabase.clearHelpRequests(task.getID());
				setHelpButton();
				showNotification("You have successfully canceled your request for help in this task!", false);
			}
		}
		catch (NotFoundException nfe) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Help cancel request error");
			alert.setHeaderText("It seems this task was deleted by the manager.");
			alert.showAndWait();
			App.loadMainScene();
			return;
		}
		catch (SQLException e) {
			App.connectionErrorMessage();
			return;
		}
		
	}
	
	public void delete(ActionEvent e) {
		
		removeErrorNotifications();
		
		try {
			
			if (!GroupTasksDatabase.deleteTask(task.getID())) {
				showNotification("There was an error trying to delete your task! Please, try again later.", true);
				return;
			}
			
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
			showNotification("The Task needs a name!", true);
			taskNameField.getStyleClass().add("error");
			return;
		}
		else if(newTaskName.length() > 128) {
			showNotification("Task name exceeds maximum character length allowed!", true);
			taskNameField.getStyleClass().add("error");
			return;
		}
		else if(!newTaskName.equals(task.getName()) && group.checkName(newTaskName)) {
			showNotification("Already exist a task with that name!", true);
			taskNameField.getStyleClass().add("error");
			return;
		}
		
		String newDescription = descriptionField.getText();
		LocalDate newDeadline = dueDateField.getValue();
		Account newAssignedTo = assignedToBox.getValue();
		
		try {
			
			if (!GroupTasksDatabase.updateTask(task.getID(), User.getAccount().getEmail(), newTaskName, newDescription, newDeadline)) {
				App.taskNoPrivilegesMessage();
				return;
			}
			
			GroupTasksDatabase.changeState(task.getID(), checkButton.isSelected());
			
			if (newAssignedTo.getEmail() == null || !newAssignedTo.getEmail().equals(task.getAssignedToEmail())) {
				
				if (!GroupTasksDatabase.changeAssignedTo(task.getID(), newAssignedTo.getEmail())) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Error editing task");
					alert.setHeaderText("There was an error when trying to assign this task to " + newAssignedTo.toString() + "!");
					alert.setContentText("This can be caused, for example, if this member no longer belongs to the group.");
					alert.showAndWait();
					return;
				}
				
			}
			
			task.setName(newTaskName);
			task.setDescription(newDescription);
			task.setDeadline(newDeadline);
			task.setCompleted(checkButton.isSelected());
			task.setAssignedTo(newAssignedTo);
			
		} 
		catch (NotFoundException nfe) {
			App.taskDeletedMessage();
			return;
		}
		catch (SQLException exception) {
			exception.printStackTrace();
			System.out.println(exception.getMessage());
			System.out.println(exception.getErrorCode());
			System.out.println(exception.getSQLState());
			System.out.println(exception.getStackTrace());
			App.connectionErrorMessage();
			return;
		}
		finally {
			App.loadMainScene();
		}
		
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
		
		if(checkButton.isSelected()) {
			checkButton.setText("Completed");
		}
		else {
			checkButton.setText("To be started");
		}
		setHelpButton();
		
	}
	
	public void clearDeadline(ActionEvent e) {
		
		dueDateField.setValue(null);
		
	}
	
	private void removeErrorNotifications() {
		
		taskNameField.getStyleClass().removeAll(Collections.singleton("error")); 
		notificationLabel.setVisible(false);
		
	}
	
	private void showNotification(String notification, boolean isError) {
		
		notificationLabel.setText(notification);
		notificationLabel.setTextFill(isError ? Color.RED : Color.GREEN);
		notificationLabel.setVisible(true);
		
	}
	
	private void setHelpButton() {
		
		if ( 	checkButton.isSelected() ||
				!User.getAccount().getEmail().equals(task.getAssignedToEmail()) ||
				User.getAccount().getEmail().equals(group.getManagerEmail())) {
			
			askHelpButton.setVisible(false);
		}
		else {
			try {
				
				if (NotificationDatabase.checkHelpRequest(task.getID())) {
					askHelpButton.setText("Cancel help request");
					askHelpButton.setOnAction(event -> cancelHelpRequest());
				}
				else {
					askHelpButton.setText("Ask for help");
					askHelpButton.setOnAction(event -> askHelp(event));
				}
				askHelpButton.setVisible(true);
				
			} catch (SQLException e) {
				
				App.connectionErrorMessage();
				
			}
		}
		
	}
	
}
