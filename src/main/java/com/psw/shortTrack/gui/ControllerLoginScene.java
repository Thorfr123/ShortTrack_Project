package com.psw.shortTrack.gui;

import java.io.IOException;
import java.sql.SQLException;
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
	private Label usernameLabel;
	@FXML
	private Label passwordLabel;
	@FXML 
	private Label notificationLabel;
	@FXML
	private Label listNameLabel;
	
	@FXML 
	private TextField usernameField;
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
	private static List list;
	private static List search;
	
	private String[] searchOptions = {"Name", "Created Date", "Deadline"};
	
	@FXML
    public void initialize() {
		
		notificationLabel = new Label();
		notificationLabel.setTextFill(Color.RED);
		
		choiceBox.setValue("Search by");
		choiceBox.getItems().addAll(searchOptions);
		choiceBox.setOnAction(this::searchOption);
		
		if(lists == null) {
			lists = User.getLists();
		}
		
		for(List l : lists) {
			ListButton listButton = new ListButton(l);
			listButton.setOnAction(event -> {
		        changeList(event);
		    });
			listsBox.getChildren().add(listButton);
		}
		
		if((list != null) && !lists.contains(list))
			list = null;
		
		if((list == null) && (search == null)) {
			listNameLabel.setText("Choose one List!");
			return;
		}
		
		loadTasks();
    }

	public void login(ActionEvent e) throws IOException {
		
		removeErrorNotifications();
		
		String username = usernameField.getText();
		String password = passwordField.getText();
		
		if(username.isBlank() || password.isBlank()) {
			
			Pane newBox = (Pane)loginBox;
			String notification = "Please complete both fields!";
			showNotification(notification,newBox);
			usernameField.getStyleClass().add("error");
			passwordField.getStyleClass().add("error");
			
			return;
		}
		
		try {
			if(!AccountsDatabase.checkLogin(username, password)) {
				
				Pane newBox = (Pane)loginBox;
				String notification = "Invalid username or password!";
				showNotification(notification,newBox);
				usernameField.getStyleClass().add("error");
				passwordField.getStyleClass().add("error");
				
				return;
			}
		} catch (SQLException exeption) {
			Pane newBox = (Pane)loginBox;
			String notification = "Error! Please, check your connection";
			showNotification(notification,newBox);
			
			return;
		}
		
		Account account = null;
		try {
			account = AccountsDatabase.getAccount(username, password);
		} catch (SQLException exeption) {
			Pane newBox = (Pane)loginBox;
			String notification = "Error! Please, check your connection";
			showNotification(notification,newBox);
			
			return;
		}
		
		User.setAccount(account);
		
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
		
		for(List l : lists) {
			if(l.getName().equals(listName)) {
				Pane newBox = (Pane)newListBox;
				String notification = "This list already exist!";
				showNotification(notification,newBox);
				newListName.getStyleClass().add("error");
				return;
			}
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
		
		if(list.checkName(taskName)) {
			Pane newBox = (Pane)newTaskBox;
			String notification = "This task already exist!";
			showNotification(notification,newBox);
			newTaskName.getStyleClass().add("error");
			return;
		}
		
		Task newTask = new PersonalTask(taskName,list.getID());
		list.addTask(newTask);
		
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
		
		if(list.checkName(taskName)) {
			Pane newBox = (Pane)newTaskBox;
			String notification = "This task already exist!";
			showNotification(notification,newBox);
			newTaskName.getStyleClass().add("error");
			return;
		}
		
		Task newTask = new PersonalTask(taskName,list.getID());
		list.addTask(newTask);
		
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
			controller.initData(newTask, search);
			App.loadScene(root);
			
		} catch (IOException exeption) {
			exeption.printStackTrace();
		}
		
	}
	
	public void changeList(ActionEvent e) {
		
		removeErrorNotifications();
		
		search = null;
		
		ListButton listButton = (ListButton)e.getSource();
		list = listButton.getList();
		
		tasksBox.getChildren().clear();
		loadTasks();
		
	}
	
	public void editList(ActionEvent e) {
		
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("EditListScene.fxml"));
			root = loader.load();
			
			ControllerEditListScene controller = loader.getController();
			controller.initData(list);
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
			controller.initData(task, search);	
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
		
	}
	
	public void sortTasks(ActionEvent e) {
		
		String option = ((MenuItem)e.getSource()).getText();
		
		List sortlist = null;
		if(list != null)
			sortlist = list;
		else if(search != null)
			sortlist = search;
		
		if(option.equals("Name"))
			sortlist.sortByName();
		else if(option.equals("Created Date"))
			sortlist.sortByCreatedDate();
		else if(option.equals("Deadline"))
			sortlist.sortByDeadline();
		else if(option.equals("Completed"))
			sortlist.sortByCompleted();
		
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
		
		List searchList = new List("Searched by: " + text, 0);
		
		String errorNotification;
		Pane newBox = (Pane)searchVerticalBox;
		switch(choiceBox.getValue()) {
			case "Name":
				for(List l: lists) {
					l.findTaskByName(text,searchList.getTaskList());			
				}
				break;
			case "Created Date":
				if((errorNotification = Task.checkValidDate(text)) != null) {
					showNotification(errorNotification,newBox);
					searchBarField.getStyleClass().add("error");
					return;
				}
				for(List l: lists) {
					l.findTaskByCreatedDate(text,searchList.getTaskList());			
				}
				break;
			case "Deadline":
				if((errorNotification = Task.checkValidDate(text)) != null) {
					showNotification(errorNotification,newBox);
					searchBarField.getStyleClass().add("error");
					return;
				}
				for(List l: lists) {
					l.findTaskByDeadline(text,searchList.getTaskList());			
				}
				break;	
			default:
				String notification = "Please select one option!";
				showNotification(notification,newBox);
				searchBarField.getStyleClass().add("error");
				return;
		}
		
		list = null;
		search = searchList;
		tasksBox.getChildren().clear();
		loadTasks();

	}
	
	private void loadTasks() {
		
		List loadList;
		if(list != null)
			loadList = list;
		else if(search != null)
			loadList = search;
		else
			loadList = null;
		
		listNameLabel.setText(loadList.getName());
		boolean searchMode = (search != null);
		
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
			
			if(t.chekCompleted())
				taskCheckBox.setSelected(true);
			
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
		
		usernameField.getStyleClass().removeAll(Collections.singleton("error")); 
		passwordField.getStyleClass().removeAll(Collections.singleton("error")); 
		newListName.getStyleClass().removeAll(Collections.singleton("error")); 
		newTaskName.getStyleClass().removeAll(Collections.singleton("error")); 
		searchBarField.getStyleClass().removeAll(Collections.singleton("error"));
		
	}
		
}
