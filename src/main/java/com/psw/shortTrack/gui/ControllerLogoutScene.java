package com.psw.shortTrack.gui;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

import com.psw.shortTrack.data.Account;
import com.psw.shortTrack.data.Group;
import com.psw.shortTrack.data.GroupTask;
import com.psw.shortTrack.data.List;
import com.psw.shortTrack.data.PersonalTask;
import com.psw.shortTrack.data.SearchList;
import com.psw.shortTrack.data.Task;
import com.psw.shortTrack.data.TaskOrganizer;
import com.psw.shortTrack.data.User;
import com.psw.shortTrack.database.GroupTasksDatabase;
import com.psw.shortTrack.database.GroupsDatabase;
import com.psw.shortTrack.database.PersonalListsDatabase;
import com.psw.shortTrack.database.PersonalTasksDatabase;

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
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

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

	private Parent root;
	
	private static ArrayList<List> lists;
	private static ArrayList<Group> groups;
	private static Account account;
	private static TaskOrganizer loadList;
	
	private String[] searchOptions = {"Name", "Created Date", "Deadline"};
	
	@FXML
    public void initialize() {
		
		User.setLogedIn(true);
		
		account = User.getAccount();
		lists = User.getLists();
		groups = User.getGroups();
		
		printNameLabel.setText(account.getName());
		printEmailLabel.setText(account.getEmail());
		
		notificationLabel = new Label();
		notificationLabel.setTextFill(Color.RED);
		
		choiceBox.setValue("Search by");
		choiceBox.getItems().addAll(searchOptions);
		choiceBox.setOnAction(this::searchOption);
		
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
		
		if ((loadList instanceof List) && !lists.contains(loadList))
			loadList = null;
		
		if ((loadList instanceof Group) && !groups.contains(loadList)) {
			for(Group g : groups) {
				if(g.getName().equals(loadList.getName())) {
					loadList = g;
					break;
				}
			}
			if(!groups.contains(loadList))
				loadList = null;
		}
		
		if(loadList == null) {
			listNameLabel.setText("Choose one List or Group!");
			return;
		}
		else if(loadList instanceof Group) {
			if(((Group)loadList).getManagerEmail().equals(User.getAccount().getEmail())){
				editListButton.setText("Edit Group");
			}
			else {
				editListButton.setText("Group Members");
			}
		}
		else
			editListButton.setText("Edit List");
		
		loadTasks();
    }
	
	public void logout(ActionEvent e) throws IOException {
		
		User.setLogedIn(false);
		User.setGroups(null);
		User.setLists(null);
		User.setAccount(null);
		loadList = null;
		
		App.readLocalFiles();
		
		root = FXMLLoader.load(getClass().getResource("LoginScene.fxml"));
		App.loadScene(root);
		
	}
	
	public void editAccount(ActionEvent e) throws IOException {
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("EditAccountScene.fxml"));
		root = loader.load();
		
		ControllerEditAccountScene controller = loader.getController();
		controller.initData();
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
		
		try {
			PersonalListsDatabase.createList(newList);
		}
		catch (SQLException exception) {
			notification = "Error! Please, check your connection";
			showNotification(notification,newBox);
			return;
		}
		
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
		
		Pane newBox = (Pane)newGroupBox;
		String notification;
		
		if(User.checkGroupName(groupName) != null) {
			notification = "This Group already exist!";
			showNotification(notification,newBox);
			newGroupName.getStyleClass().add("error");
			return;
		}
		
		Group newGroup = new Group(groupName,account);
		
		try {
			GroupsDatabase.createGroup(newGroup);
		}
		catch (SQLException exception) {
			notification = "Error! Please, check your connection";
			showNotification(notification,newBox);
			return;
		}
		
		groups.add(newGroup);
		
		GroupButton groupButton = new GroupButton(newGroup);
		groupButton.setOnAction(event -> {
	        changeGroup(event);
	    });
		groupsBox.getChildren().add(groupButton);
		
	}
	
	public void addGroupComplete(ActionEvent e) {
		
		removeErrorNotifications();
		
		String groupName = newGroupName.getText();
		
		newGroupName.clear();
		
		Pane newBox = (Pane)newGroupBox;
		String notification;
		
		if(User.checkGroupName(groupName) != null) {
			notification = "This Group already exist!";
			showNotification(notification,newBox);
			newGroupName.getStyleClass().add("error");
			return;
		}
		
		Group newGroup = new Group(groupName,account);
		
		try {
			GroupsDatabase.createGroup(newGroup);
		}
		catch (SQLException exception) {
			notification = "Error! Please, check your connection";
			showNotification(notification,newBox);
			return;
		}
		
		groups.add(newGroup);
		
		GroupButton groupButton = new GroupButton(newGroup);
		groupButton.setOnAction(event -> {
	        changeGroup(event);
	    });
		groupsBox.getChildren().add(groupButton);
		
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("EditGroupScene.fxml"));
			root = loader.load();
			
			ControllerEditGroupScene controller = loader.getController();
			controller.initData(newGroup);
			App.loadScene(root);
			
		} catch (IOException exeption) {
			exeption.printStackTrace();
		}
		
	}
	
	public void addTask(ActionEvent e) {
		
		removeErrorNotifications();
		
		String taskName = newTaskName.getText();
		
		if(taskName.isBlank())
			return;
		
		newTaskName.clear();
		
		if(loadList instanceof List)
			addTaskToList(taskName);
		else if(loadList instanceof Group)
			addTaskToGroup(taskName);
		
	}
	
	private PersonalTask addTaskToList(String taskName) {
		
		Pane newBox = (Pane)newTaskBox;
		String notification;


		if(loadList.checkName(taskName)) {
			notification = "This Task already exist!";
			showNotification(notification,newBox);
			newTaskName.getStyleClass().add("error");
			return null;
		}
		
		PersonalTask newTask = new PersonalTask(taskName,loadList.getID());

		try {
			PersonalTasksDatabase.createTask(newTask);
		} catch (SQLException exception) {
			notification = "Error! Please, check your connection";
			showNotification(notification,newBox);
			return null;
		}	

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
		
		return newTask;
	}
	
	private GroupTask addTaskToGroup(String taskName) {
		
		Pane newBox = (Pane)newTaskBox;
		String notification;

		if(loadList.checkName(taskName)) {
			notification = "This Task already exist!";
			showNotification(notification,newBox);
			newTaskName.getStyleClass().add("error");
			return null;
		}
		
		GroupTask newTask = new GroupTask(taskName,loadList.getID());

		try {
			GroupTasksDatabase.createTask(newTask);
		} catch (SQLException exception) {
			notification = "Error! Please, check your connection";
			showNotification(notification,newBox);
			return null;
		}
		
		((Group)loadList).addTask(newTask);
		
		GroupTaskBar taskBar = new GroupTaskBar(newTask);
		CheckBox taskCheckBox = taskBar.getCheckBox();
		Button taskButton = taskBar.getButton();
		
		taskCheckBox.setOnAction(event -> {
			checkGroupTask(event);
        });
		taskButton.setOnAction(event -> {
            editGroupTask(event);
        });
		
		tasksBox.getChildren().add(taskBar);
		
		return newTask;
	}
	
	public void addTaskComplete(ActionEvent e) {
		
		removeErrorNotifications();
		
		String taskName = newTaskName.getText();
		
		newTaskName.clear();
		
		Task newTask = null;
		if(loadList instanceof List)
			newTask = addTaskToList(taskName);
		else if(loadList instanceof Group)
			newTask = addTaskToGroup(taskName);
		
		if(newTask == null)
			return;
		
		if(loadList instanceof List) {
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
		else if(loadList instanceof Group) {
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("EditGroupTaskScene.fxml"));
				root = loader.load();
				
				ControllerEditGroupTaskScene controller = loader.getController();
				controller.initData((GroupTask)newTask, loadList);	
				App.loadScene(root);
				
			} catch (IOException exeption) {
				exeption.printStackTrace();
			}
		}
		
	}
	
	public void changeList(ActionEvent e) {
		
		removeErrorNotifications();
		
		editListButton.setText("Edit List");
		ListButton listButton = (ListButton)e.getSource();
		loadList = listButton.getList();
		
		tasksBox.getChildren().clear();
		loadTasks();
		
	}
	
	public void changeGroup(ActionEvent e) {
		
		removeErrorNotifications();
		
		GroupButton groupButton = (GroupButton)e.getSource();
		loadList = groupButton.getGroup();
		
		if(((Group)loadList).getManagerEmail().equals(User.getAccount().getEmail())) {
			editListButton.setText("Edit Group");
		}
		else {
			editListButton.setText("Group Members");
		}
		
		tasksBox.getChildren().clear();
		loadTasks();
		
	}
	
	public void editListOrGroup(ActionEvent e) {
		
		if(loadList instanceof List) {
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
		else if(loadList instanceof Group) {
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("EditGroupScene.fxml"));
				root = loader.load();
				
				ControllerEditGroupScene controller = loader.getController();
				controller.initData((Group)loadList);	
				App.loadScene(root);
				
			} catch (IOException exeption) {
				exeption.printStackTrace();
			}	
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
			controller.initData(task,loadList);	
			App.loadScene(root);
				
		} catch (IOException exeption) {
			exeption.printStackTrace();
		}
		
	}
	
	public void editGroupTask(ActionEvent e) {
		
		Button taskButton = (Button)e.getSource();
		GroupTaskBar taskBar = (GroupTaskBar)taskButton.getParent();
		GroupTask task = taskBar.getTask();
		
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("EditGroupTaskScene.fxml"));
			root = loader.load();
			
			ControllerEditGroupTaskScene controller = loader.getController();
			controller.initData(task,loadList);	
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
	
	public void checkGroupTask(ActionEvent e) {
		
		removeErrorNotifications();
		
		CheckBox taskCheckBox = (CheckBox)e.getSource();
		GroupTaskBar taskBar = (GroupTaskBar)taskCheckBox.getParent();
		Task task = taskBar.getTask();
		
		task.setCompleted(taskCheckBox.isSelected());
		
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
		String errorNotification;
		Pane newBox = (Pane)searchVerticalBox;
		
		if(isDateType && (errorNotification = Task.checkValidDate(text)) != null) {
			showNotification(errorNotification,newBox);
			searchBarField.getStyleClass().add("error");
			return;
		}
		
		switch(searchOption) {
			case "Name":
				for(List l: lists)
					l.findTaskByName(text,searchList.getTaskList());			
				for(Group g: groups)
					g.findTaskByName(text,searchList.getTaskList());			
				break;
			case "Created Date":
				for(List l: lists)
					l.findTaskByCreatedDate(text,searchList.getTaskList());			
				for(Group g: groups)
					g.findTaskByName(text,searchList.getTaskList());			
				break;
			case "Deadline":
				for(List l: lists)
					l.findTaskByDeadline(text,searchList.getTaskList());			
				for(Group g: groups)
					g.findTaskByName(text,searchList.getTaskList());			
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
		
		boolean searchMode = (loadList instanceof SearchList);
		boolean isMemberOfTheGroup = (loadList instanceof Group) 
						&& !((Group)loadList).getManagerEmail().equals(User.getAccount().getEmail());
		
		if(searchMode || isMemberOfTheGroup)
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
		
		listNameLabel.setText(loadList.getName());
		ArrayList<Task> tasks = loadList.getTaskList();
		for(Task t : tasks) {
			if (t instanceof PersonalTask) {
				TaskBar taskBar = new TaskBar(t,searchMode);
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
			else if(t instanceof GroupTask) {
				GroupTaskBar taskBar = new GroupTaskBar((GroupTask)t,searchMode);
				CheckBox taskCheckBox = taskBar.getCheckBox();
				Button taskButton = taskBar.getButton();
				
				if(t.chekCompleted())
					taskCheckBox.setSelected(true);
				
				taskCheckBox.setOnAction(event -> {
					checkGroupTask(event);
		        });
				taskButton.setOnAction(event -> {
		            editGroupTask(event);
		        });
				
				tasksBox.getChildren().add(taskBar);
			}

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