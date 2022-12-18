package com.psw.shortTrack.gui;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import com.psw.shortTrack.data.*;
import com.psw.shortTrack.database.*;
import com.psw.shortTrack.gui_elements.ListButton;
import com.psw.shortTrack.gui_elements.TaskBar;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
	private Label notificationLabel = new Label();;
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
	
	private static ArrayList<List> lists;
	private static TaskOrganizer loadList;
	
	private String[] searchOptions = {"Name", "Created Date", "Deadline"};
	
	@FXML
    public void initialize() {
				
		notificationLabel.setTextFill(Color.RED);
		
		choiceBox.setValue("Search by");
		choiceBox.getItems().addAll(searchOptions);
		choiceBox.setOnAction(this::searchOption);
		
		// Loads sort by options
		for (String option : SortBy.options()) {
			MenuItem item = new MenuItem(option);
			item.setOnAction(this::sortTasks);
			sortByMenu.getItems().add(item);
		}
		
		lists = User.getLists();
		
		// Create all personal lists buttons
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

	public void login(ActionEvent e) {
		
		removeErrorNotifications();
		
		String email = emailField.getText();
		String password = passwordField.getText();
		
		if(email.isBlank() || password.isBlank()) {
			showNotification("Please complete both fields!",(Pane)loginBox);
			return;
		}
		
		try {
			if(!AccountsDatabase.checkLogin(email, password)) {
				showNotification("Invalid email or password!",(Pane)loginBox);
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
			lists = PersonalListsDatabase.getAllLists(account);
			
			User.setAccount(account);
			User.setLists(lists);
		} catch (SQLException exception) {
			App.connectionErrorMessage();
			return;
		}
		
		App.loadScene("LogoutScene.fxml");
		
	}
	
	public void signUp(ActionEvent e) {
		
		App.loadScene("SignUpScene.fxml");
		
	}
	
	public void addList(ActionEvent e) {
		
		removeErrorNotifications();
		
		String listName = newListName.getText();
		
		if(listName.isBlank())
			return;
		
		newListName.clear();
		
		if(listName.length() > 128) {
			showNotification("Group name exceeds maximum character length allowed!",(Pane)newListBox);
			return;
		}
		else if(User.checkListName(listName)) {
			showNotification("This List already exist!",(Pane)newListBox);
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

		if(taskName.length() > 128) {
			showNotification("Task name exceeds maximum character length allowed!",(Pane)newTaskBox);
			return;
		}
		else if(loadList.checkName(taskName)) {
			showNotification("This Task already exist!",(Pane)newTaskBox);
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
		
		if(taskName.length() > 128) {
			showNotification("Task name exceeds maximum character length allowed!",(Pane)newTaskBox);
			return;
		}
		else if(loadList.checkName(taskName)) {
			showNotification("This Task already exist!",(Pane)newTaskBox);
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
		
		FXMLLoader loader = App.loadScene("EditTaskScene.fxml");
		ControllerEditTaskScene controller = loader.getController();
		controller.initData(newTask, loadList);
		
	}
	
	public void changeList(ActionEvent e) {
		
		removeErrorNotifications();
		
		ListButton listButton = (ListButton)e.getSource();
		loadList = listButton.getList();
		
		loadTasks();
		
	}
	
	public void editList(ActionEvent e) {
		
		FXMLLoader loader = App.loadScene("EditListScene.fxml");
		ControllerEditListScene controller = loader.getController();
		controller.initData((List)loadList);
		
	}
	
	public void editTask(ActionEvent e) {
		
		Button taskButton = (Button)e.getSource();
		TaskBar taskBar = (TaskBar)taskButton.getParent();
		Task task = taskBar.getTask();

		FXMLLoader loader = App.loadScene("EditTaskScene.fxml");		
		ControllerEditTaskScene controller = loader.getController();
		controller.initData(task, loadList);	
	
	}
	
	public void checkTask(ActionEvent e) {
		
		removeErrorNotifications();
		
		CheckBox taskCheckBox = (CheckBox)e.getSource();
		TaskBar taskBar = (TaskBar)taskCheckBox.getParent();
		Task task = taskBar.getTask();
		
		task.setCompleted(taskCheckBox.isSelected());
		
		if(task.isCompleted()) {
			taskCheckBox.setSelected(true);
			taskBar.setOpacity(0.5);
		}
		else
			taskBar.setOpacity(1);
		
	}
	
	public void sortTasks(ActionEvent e) {
		
		String option = ((MenuItem)e.getSource()).getText();
		loadList.sort(SortBy.fromString(option));
		
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
		
		if(isDateType && (date = Task.checkValidDate(text)) == null) {
			showNotification("Invalid date format!",(Pane)searchVerticalBox);
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
				showNotification("Please select one option!",(Pane)searchVerticalBox);
				return;
		}
		
		if(searchList.getTaskList().isEmpty()) {
			showNotification("Nothing was found!",(Pane)searchVerticalBox);
			return;
		}
		
		loadList = searchList;
		loadTasks();
		
	}
	
	private void loadTasks() {
		
		tasksBox.getChildren().clear();
		
		listNameLabel.setText(loadList.getName());
		boolean searchMode = (loadList instanceof SearchList);
		
		setVisibleNodes(searchMode);
		
		ArrayList<Task> tasks = loadList.getTaskList();
		for(Task t : tasks) {
			TaskBar taskBar = new TaskBar(t, searchMode);
			CheckBox taskCheckBox = taskBar.getCheckBox();
			Button taskButton = taskBar.getButton();
			
			if(t.isCompleted()) {
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
	
	private void setVisibleNodes(boolean searchMode) {
		
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
		
	}
	
	private void showNotification(String notification, Pane newBox) {
		
		newBox.getChildren().add(notificationLabel);
		notificationLabel.setText(notification);
		System.out.println(notification);
		
		if(newBox.equals(loginBox)) {
			emailField.getStyleClass().add("error");
			passwordField.getStyleClass().add("error");
		}
		else if(newBox.equals(newListBox))
			newListName.getStyleClass().add("error");
		else if(newBox.equals(newTaskBox))
			newTaskName.getStyleClass().add("error");
		else if(newBox.equals(searchVerticalBox))
			searchBarField.getStyleClass().add("error");
			
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
