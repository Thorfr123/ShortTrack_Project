package com.psw.shortTrack.gui;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

import com.psw.shortTrack.data.Account;
import com.psw.shortTrack.data.Group;
import com.psw.shortTrack.data.List;
import com.psw.shortTrack.data.Task;
import com.psw.shortTrack.data.User;
import com.psw.shortTrack.database.AccountsDatabase;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ControllerLogoutScene {
		
	@FXML 
	private Label printNameLabel;
	@FXML 
	private Label printEmailLabel;
	@FXML 
	private Label notificationLabel;
	@FXML
	private Label listNameLabel;
	
	@FXML 
	private TextField newListName;
	@FXML 
	private TextField newGroupName;
	@FXML 
	private TextField newTaskName;
	@FXML
	private TextField searchBarField;
	
	@FXML
	private Button editListButton;
	@FXML
	private MenuButton sortByMenu;
	@FXML
	private ChoiceBox<String> choiceBox;
	
	@FXML 
	private VBox logoutBox;
	@FXML 
	private VBox newListBox;
	@FXML 
	private VBox newGroupBox;
	@FXML 
	private VBox newTaskBox;
	@FXML 
	private VBox listsBox;
	@FXML 
	private VBox groupsBox;
	@FXML 
	private VBox tasksBox;
	@FXML 
	private HBox addTaskBox;
	@FXML
	private VBox searchVerticalBox;

	private Stage stage;
	private Parent root;
	
	private static ArrayList<List> lists;
	private static ArrayList<Group> groups;
	private static Account account;
	private static List list;
	private static Group group;
	private static List search;
	
	private String[] searchOptions = {"Name", "Created Date", "Deadline"};
	
	//private int SEARCH_LIST_ID = 0;
	
	@FXML
    public void initialize() {
		
		User.setLogedIn(true);
		
		if(account == null)
			account = User.getAccount();
		
		printNameLabel.setText(account.getName());
		printEmailLabel.setText(account.getEmail());
		
		notificationLabel = new Label();
		notificationLabel.setTextFill(Color.RED);
		
		choiceBox.setValue("Search by");
		choiceBox.getItems().addAll(searchOptions);
		choiceBox.setOnAction(this::searchOption);
		
		if(lists == null)
			lists = User.getLists();
		
		if(groups == null)
			groups = User.getGroups();
		
		for(List l : lists) {
			ListButton listButton = new ListButton(l);
			listButton.setOnAction(event -> {
		        changeList(event);
		    });
			listsBox.getChildren().add(listButton);
		}
		
		for(Group g : groups) {
			GroupButton groupButton = new GroupButton(g);
			groupButton.setOnAction(event -> {
		        changeGroup(event);
		    });
			groupsBox.getChildren().add(groupButton);
		}
		
		if((list != null) && !lists.contains(list))
			list = null;
		
		if((group != null) && !groups.contains(group))
			list = null;
		
		if((list == null) && (group == null) && (search == null)) {
			listNameLabel.setText("Choose one List or Group!");
			return;
		}
		
		loadTasks();
    }
	
	public void logout(ActionEvent e) throws IOException {
		
		User.setLogedIn(false);
		
		root = FXMLLoader.load(getClass().getResource("LoginScene.fxml"));
		stage = (Stage)((Node)e.getSource()).getScene().getWindow();
		App.loadScene(root,stage);
		stage.show();
		
	}
	
	public void delete(ActionEvent e) throws IOException {
		
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Delete account");
		alert.setHeaderText("You're about to delete your account and all your data!");
		alert.setContentText("Are you sure you really want to delete your account?");

		if(alert.showAndWait().get() == ButtonType.OK){
			
			try {
				if (!AccountsDatabase.deleteAccount(printEmailLabel.getText())) {
					System.out.println("Error deleting account!");  				// Just to debug
					return;
				}
			} catch (SQLException e1) {
				//Temporario
				System.out.println("Error! Please, check your conection");
				return;
			}	
			
			User.setLogedIn(false);
			
			root = FXMLLoader.load(getClass().getResource("LoginScene.fxml"));
			stage = (Stage)((Node)e.getSource()).getScene().getWindow();
			App.loadScene(root,stage);
			stage.show();
			
			System.out.println("Your account was successfully deleted!");  	    // Just to debug
		}
		
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
	
	public void addGroup(ActionEvent e) {
		
		removeErrorNotifications();
		
		String groupName = newGroupName.getText();
		
		if(groupName.isBlank())
			return;
		
		newGroupName.clear();
		
		for(Group g : groups) {
			if(g.getName().equals(groupName) && g.getManager().equals(account.getEmail())) {
				Pane newBox = (Pane)newListBox;
				String notification = "This group already exist!";
				showNotification(notification,newBox);
				newGroupName.getStyleClass().add("error");
				return;
			}
		}
		
		Group newGroup = new Group(groupName,account.getEmail());
		groups.add(newGroup);
		
		GroupButton groupButton = new GroupButton(newGroup);
		groupButton.setOnAction(event -> {
	        changeGroup(event);
	    });
		groupsBox.getChildren().add(groupButton);
		
	}
	
	public void addTask(ActionEvent e) {
		
		removeErrorNotifications();
		
		String taskName = newTaskName.getText();
		
		if(taskName.isBlank())
			return;
		
		newTaskName.clear();
		
		if(list != null)
			addTaskToList(taskName);
		else if(group != null)
			addTaskToGroup(taskName);
		else
			System.out.println("Algum erro aconteceu v1!");
		
	}
	
	public void addTaskToList(String taskName) {
	
		if(list.checkName(taskName)) {
			Pane newBox = (Pane)newTaskBox;
			String notification = "This task already exist!";
			showNotification(notification,newBox);
			newTaskName.getStyleClass().add("error");
			return;
		}
		
		Task newTask = new Task(taskName,list.getID());
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
	
	public void addTaskToGroup(String taskName) {
		
		if(group.checkName(taskName)) {
			Pane newBox = (Pane)newTaskBox;
			String notification = "This task already exist!";
			showNotification(notification,newBox);
			newTaskName.getStyleClass().add("error");
			return;
		}
		
		Task newTask = new Task(taskName,group.getID());
		group.addTask(newTask);
		
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
		
		Task newTask = new Task(taskName,list.getID());
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
			stage = (Stage)((Node)e.getSource()).getScene().getWindow();
			App.loadScene(root,stage);
			
			ControllerEditTaskScene controller = loader.getController();
			controller.initData(newTask, list);	

			stage.show();
			
		} catch (IOException exeption) {
			exeption.printStackTrace();
		}
		
	}
	
	public void changeList(ActionEvent e) {
		
		removeErrorNotifications();
		group = null;
		search = null;
		
		ListButton listButton = (ListButton)e.getSource();
		list = listButton.getList();
		
		tasksBox.getChildren().clear();
		loadTasks();
		
	}
	
	public void changeGroup(ActionEvent e) {
		
		removeErrorNotifications();
		list = null;
		search = null;
		
		GroupButton groupButton = (GroupButton)e.getSource();
		group = groupButton.getGroup();
		
		tasksBox.getChildren().clear();
		loadTasks();
		
	}
	
	public void editList(ActionEvent e) {
		
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("EditListScene.fxml"));
			root = loader.load();
			stage = (Stage)((Node)e.getSource()).getScene().getWindow();
			App.loadScene(root,stage);
			
			ControllerEditListScene controller = loader.getController();
			controller.initData(list);	

			stage.show();
		} catch (IOException exeption) {
			exeption.printStackTrace();
		}	
		
	}
	
	public void editGroup(ActionEvent e) {
		
		System.out.println("addGroup!");
		
	}
	
	public void editTask(ActionEvent e) {
		
		Button taskButton = (Button)e.getSource();
		TaskBar taskBar = (TaskBar)taskButton.getParent();
		Task task = taskBar.getTask();
		
		try {
			
			FXMLLoader loader = new FXMLLoader(getClass().getResource("EditTaskScene.fxml"));
			root = loader.load();
			stage = (Stage)((Node)e.getSource()).getScene().getWindow();
			App.loadScene(root,stage);
			
			ControllerEditTaskScene controller = loader.getController();
			controller.initData(task, list);	

			stage.show();
			
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
		
		if(option.equals("Name"))
			list.sortByName();
		else if(option.equals("Created Date"))
			list.sortByCreatedDate();
		else if(option.equals("Deadline"))
			list.sortByDeadline();
		else if(option.equals("Completed"))
			list.sortByCompleted();
		
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
		
		list = searchList;
		tasksBox.getChildren().clear();
		loadTasks();

	}
	
	private void loadTasks() {

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
		
		if(list != null)
			loadListTasks();
		else if(group != null)
			loadGroupTasks();
		else
			System.out.println("Algum erro aconteceu v2!");
	}
	
	private void loadListTasks() {
		
		listNameLabel.setText(list.getName());
		ArrayList<Task> tasks = list.getTaskList();
		for(Task t : tasks) {
			TaskBar taskBar = new TaskBar(t);
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
	
	private void loadGroupTasks() {
		
		listNameLabel.setText(group.getName());
		ArrayList<Task> tasks = group.getTaskList();
		for(Task t : tasks) {
			TaskBar taskBar = new TaskBar(t);
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
		
		logoutBox.getChildren().remove(notificationLabel);
		newTaskBox.getChildren().remove(notificationLabel);
		newListBox.getChildren().remove(notificationLabel);
		newGroupBox.getChildren().remove(notificationLabel);
		searchVerticalBox.getChildren().remove(notificationLabel);
		
		newListName.getStyleClass().removeAll(Collections.singleton("error")); 
		newTaskName.getStyleClass().removeAll(Collections.singleton("error")); 
		newGroupName.getStyleClass().removeAll(Collections.singleton("error")); 
		searchBarField.getStyleClass().removeAll(Collections.singleton("error"));
		
	}
}