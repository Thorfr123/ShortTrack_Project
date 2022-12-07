package com.psw.shortTrack.gui;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import com.psw.shortTrack.data.*;
import com.psw.shortTrack.database.*;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class ControllerLoginScene {
	
	@FXML 
	private Label notificationLabel;
	@FXML
	private Label listNameLabel;
	
	@FXML 
	private TextField emailField;
	@FXML 
	private PasswordField passwordField;
	@FXML 
	private TextField newListName;
	@FXML 
	private TextField newTaskName;
	@FXML
	private TextField searchBarField;
	
	@FXML
	private Button loginButton;
	@FXML
	private Button signUpButton;
	@FXML
	private Button editListButton;
	@FXML
	private MenuButton sortByMenu;
	@FXML
	private ChoiceBox<String> choiceBox;
	
	@FXML 
	private VBox loginBox;
	@FXML 
	private VBox newListBox;
	@FXML 
	private VBox newTaskBox;
	@FXML 
	private VBox listsBox;
	@FXML 
	private VBox tasksBox;
	@FXML 
	private HBox addTaskBox;
	@FXML
	private VBox searchVerticalBox;

	private Parent root;
	
	private static ArrayList<List> lists;
	private static TaskOrganizer loadList;
	
	private String[] searchOptions = {"Name", "Created Date", "Deadline"};
	
	@FXML
    public void initialize() {
				
		notificationLabel = new Label();
		notificationLabel.setTextFill(Color.RED);
		
		choiceBox.setValue("Search by");
		choiceBox.getItems().addAll(searchOptions);
		choiceBox.setOnAction(this::searchOption);
		
		lists = User.getLists();
		
		for(List l : lists) {
			ListButton listButton = new ListButton(l);
			listButton.setOnAction(event -> {
		        changeList(event);
		    });
			listsBox.getChildren().add(listButton);
		}
		
		if ((loadList instanceof List) && !lists.contains(loadList))
			loadList = null;
		
		if(loadList == null) {
			listNameLabel.setText("Choose one List!");
			return;
		}
		
		loadTasks();
    }

	public void login(ActionEvent e) throws IOException {
		
		removeErrorNotifications();
		
		String email = emailField.getText();
		String password = passwordField.getText();
		
		if(email.isBlank() || password.isBlank()) {
			Pane newBox = (Pane)loginBox;
			String notification = "Please complete both fields!";
			showNotification(notification,newBox);
			emailField.getStyleClass().add("error");
			passwordField.getStyleClass().add("error");
			return;
		}
		
		try {
			if(!AccountsDatabase.checkLogin(email, password)) {
				Pane newBox = (Pane)loginBox;
				String notification = "Invalid email or password!";
				showNotification(notification,newBox);
				emailField.getStyleClass().add("error");
				passwordField.getStyleClass().add("error");
				return;
			}
		} catch (SQLException exeption) {
			App.connectionErrorMessage();
			return;
		}
		
		App.writeLocalFiles();
		
		Account account = null;
		ArrayList<List> lists;
		try {
			account = AccountsDatabase.getAccount(email);
			lists = PersonalListsDatabase.getAllLists(account.getEmail());
			User.setAccount(account);	
			User.setLists(lists);
		} catch (SQLException exception) {
			App.connectionErrorMessage();
			return;
		}
		
		root = FXMLLoader.load(getClass().getResource("LogoutScene.fxml"));
		App.loadScene(root);
		
	}
	
	public void signUp(ActionEvent e) throws IOException {
		
		root = FXMLLoader.load(getClass().getResource("SignUpScene.fxml"));
		App.loadScene(root);
		
	}
	
	public void addList(ActionEvent e) {
		
		removeErrorNotifications();
		
		String listName = newListName.getText();
		
		if(listName.isBlank())
			return;
		
		newListName.clear();
		
		Pane newBox = (Pane)newListBox;
		String notification;
		
		if(User.checkListName(listName)) {
			notification = "This List already exist!";
			showNotification(notification,newBox);
			newListName.getStyleClass().add("error");
			return;
		}
		
		List newList = new List(listName);
		
		lists.add(newList);
		
		ListButton listButton = new ListButton(newList);
		listButton.setOnAction(event -> {
	        changeList(event);
	    });
		listsBox.getChildren().add(listButton);
		
	}
	
	public void addTask(ActionEvent e) {
		
		removeErrorNotifications();
				
		String taskName = newTaskName.getText();
		
		if(taskName.isBlank())
			return;
		
		newTaskName.clear();
		
		if(loadList.checkName(taskName)) {
			Pane newBox = (Pane)newTaskBox;
			String notification = "This Task already exist!";
			showNotification(notification,newBox);
			newTaskName.getStyleClass().add("error");
			return;
		}
		
		PersonalTask newTask = new PersonalTask(taskName,loadList.getID());
		
		((List)loadList).addTask(newTask);
		
		TaskBar taskBar = new TaskBar(newTask);
		CheckBox taskCheckBox = taskBar.getCheckBox();
		Button taskButton = taskBar.getButton();
		
		taskCheckBox.setOnAction(event -> {
            checkTask(event);
        });
		taskButton.setOnAction(event -> {
            editTask(event);
        });
		
		tasksBox.getChildren().add(taskBar);
		
	}
	
	public void addTaskComplete(ActionEvent e) {
		
		removeErrorNotifications();
		
		String taskName = newTaskName.getText();

		newTaskName.clear();
		
		if(loadList.checkName(taskName)) {
			Pane newBox = (Pane)newTaskBox;
			String notification = "This Task already exist!";
			showNotification(notification,newBox);
			newTaskName.getStyleClass().add("error");
			return;
		}
		
		PersonalTask newTask = new PersonalTask(taskName,loadList.getID());
		
		((List)loadList).addTask(newTask);
		
		TaskBar taskBar = new TaskBar(newTask);
		CheckBox taskCheckBox = taskBar.getCheckBox();
		Button taskButton = taskBar.getButton();
		
		taskCheckBox.setOnAction(event -> {
            checkTask(event);
        });
		taskButton.setOnAction(event -> {
            editTask(event);
        });
		
		tasksBox.getChildren().add(taskBar);
		
		try {
			
			FXMLLoader loader = new FXMLLoader(getClass().getResource("EditTaskScene.fxml"));
			root = loader.load();
			
			ControllerEditTaskScene controller = loader.getController();
			controller.initData(newTask, loadList);
			App.loadScene(root);
			
		} catch (IOException exeption) {
			exeption.printStackTrace();
		}
		
	}
	
	public void changeList(ActionEvent e) {
		
		removeErrorNotifications();
		
		ListButton listButton = (ListButton)e.getSource();
		loadList = listButton.getList();
		
		tasksBox.getChildren().clear();
		loadTasks();
		
	}
	
	public void editList(ActionEvent e) {
		
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("EditListScene.fxml"));
			root = loader.load();
			
			ControllerEditListScene controller = loader.getController();
			controller.initData((List)loadList);
			App.loadScene(root);

		} catch (IOException exeption) {
			exeption.printStackTrace();
		}	
		
	}
	
	public void editTask(ActionEvent e) {
		
		Button taskButton = (Button)e.getSource();
		TaskBar taskBar = (TaskBar)taskButton.getParent();
		Task task = taskBar.getTask();
		
		try {
			
			FXMLLoader loader = new FXMLLoader(getClass().getResource("EditTaskScene.fxml"));
			root = loader.load();
			
			ControllerEditTaskScene controller = loader.getController();
			controller.initData(task, loadList);	
			App.loadScene(root);
			
		} catch (IOException exeption) {
			exeption.printStackTrace();
		}
		
	}
	
	public void checkTask(ActionEvent e) {
		
		removeErrorNotifications();
		
		CheckBox taskCheckBox = (CheckBox)e.getSource();
		TaskBar taskBar = (TaskBar)taskCheckBox.getParent();
		Task task = taskBar.getTask();
		
		task.setCompleted(taskCheckBox.isSelected());
		
		if(task.chekCompleted()) {
			taskCheckBox.setSelected(true);
			taskBar.setOpacity(0.5);
		}
		else
			taskBar.setOpacity(1);
		
	}
	
	public void sortTasks(ActionEvent e) {
		
		String option = ((MenuItem)e.getSource()).getText();
		
		if(option.equals("Name"))
			loadList.sortByName();
		else if(option.equals("Created Date"))
			loadList.sortByCreatedDate();
		else if(option.equals("Deadline"))
			loadList.sortByDeadline();
		else if(option.equals("Completed"))
			loadList.sortByCompleted();
		
		tasksBox.getChildren().clear();
		loadTasks();
		
	}
	
	public void searchOption(ActionEvent e) {
		
		removeErrorNotifications();
		
		String text;
		
		switch(choiceBox.getValue()) {
		  case "Name":
			  text = "ex: Task 1";
			  break;
		  case "Created Date":
		  case "Deadline":
			  text = "ex: 2022-11-12";
			  break;
		  default:
			  text = "ex: Task 1";
		}

		searchBarField.setPromptText(text);
		
	}
	
	public void searchTask(ActionEvent e) {
		
		removeErrorNotifications();
		
		String text = searchBarField.getText();
		
		if(text.isBlank())
			return;
		
		SearchList searchList = new SearchList("Searched by: " + text);
		
		String searchOption = choiceBox.getValue();
		Boolean isDateType = searchOption.equals("Created Date") || searchOption.equals("Deadline");
		LocalDate date = null;
		Pane newBox = (Pane)searchVerticalBox;
		
		if(isDateType && (date = Task.checkValidDate(text)) == null) {
			showNotification("Invalid date format!",newBox);
			searchBarField.getStyleClass().add("error");
			return;
		}
		
		switch(searchOption) {
			case "Name":
				for(List l: lists)
					l.findTaskByName(text,searchList.getTaskList());			
				break;
			case "Created Date":
				for(List l: lists)
					l.findTaskByCreatedDate(date,searchList.getTaskList());			
				break;
			case "Deadline":
				for(List l: lists)
					l.findTaskByDeadline(date,searchList.getTaskList());			
				break;	
			default:
				String notification = "Please select one option!";
				showNotification(notification,newBox);
				searchBarField.getStyleClass().add("error");
				return;
		}
		
		if(searchList.getTaskList().isEmpty()) {
			String notification = "Nothing was found!";
			showNotification(notification,newBox);
			searchBarField.getStyleClass().add("error");
			return;
		}
		
		loadList = searchList;
		tasksBox.getChildren().clear();
		loadTasks();

	}
	
	private void loadTasks() {
		
		listNameLabel.setText(loadList.getName());
		boolean searchMode = (loadList instanceof SearchList);
		
		if(searchMode)
			newTaskBox.getChildren().remove(addTaskBox);
		else if(!newTaskBox.getChildren().contains(addTaskBox))
			newTaskBox.getChildren().add(addTaskBox);

		addTaskBox.setVisible(true);

		HBox searchBox = (HBox)sortByMenu.getParent();
		if(searchMode)
			searchBox.getChildren().remove(editListButton);
		else if(!searchBox.getChildren().contains(editListButton))
			searchBox.getChildren().add(editListButton);
		
		editListButton.setVisible(true);
		sortByMenu.setVisible(true);
		
		ArrayList<Task> tasks = loadList.getTaskList();
		for(Task t : tasks) {
			TaskBar taskBar = new TaskBar(t, searchMode);
			CheckBox taskCheckBox = taskBar.getCheckBox();
			Button taskButton = taskBar.getButton();
			
			if(t.chekCompleted()) {
				taskCheckBox.setSelected(true);
				taskBar.setOpacity(0.5);
			}
			else
				taskBar.setOpacity(1);
			
			taskCheckBox.setOnAction(event -> {
	            checkTask(event);
	        });
			taskButton.setOnAction(event -> {
	            editTask(event);
	        });
			
			tasksBox.getChildren().add(taskBar);
		}
		
	}
	
	private void showNotification(String notification, Pane newBox) {
		
		newBox.getChildren().add(notificationLabel);
		notificationLabel.setText(notification);
		System.out.println(notification);
		
	}
	
	private void removeErrorNotifications() {
		
		loginBox.getChildren().remove(notificationLabel);
		newTaskBox.getChildren().remove(notificationLabel);
		newListBox.getChildren().remove(notificationLabel);
		searchVerticalBox.getChildren().remove(notificationLabel);
		
		emailField.getStyleClass().removeAll(Collections.singleton("error")); 
		passwordField.getStyleClass().removeAll(Collections.singleton("error")); 
		newListName.getStyleClass().removeAll(Collections.singleton("error")); 
		newTaskName.getStyleClass().removeAll(Collections.singleton("error")); 
		searchBarField.getStyleClass().removeAll(Collections.singleton("error"));
		
	}
		
}
