package com.psw.shortTrack.gui;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;

import com.psw.shortTrack.data.Account;
import com.psw.shortTrack.data.Group;
import com.psw.shortTrack.data.GroupTask;
import com.psw.shortTrack.data.List;
import com.psw.shortTrack.data.Notification;
import com.psw.shortTrack.data.PersonalTask;
import com.psw.shortTrack.data.SearchList;
import com.psw.shortTrack.data.Task;
import com.psw.shortTrack.data.TaskOrganizer;
import com.psw.shortTrack.data.User;
import com.psw.shortTrack.data.TaskOrganizer.SortBy;
import com.psw.shortTrack.database.GroupTasksDatabase;
import com.psw.shortTrack.database.GroupsDatabase;
import com.psw.shortTrack.database.NotificationDatabase;
import com.psw.shortTrack.database.PersonalListsDatabase;
import com.psw.shortTrack.database.PersonalTasksDatabase;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
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
	private Label notificationNumber;
	
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
	private Button notificationButton;
	@FXML
	private MenuButton sortByMenu;
	@FXML
	private Button myTasksButton;
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
	@FXML
	private HBox listOptionsBox;

	private Parent root;
	
	private static ArrayList<List> lists;
	private static ArrayList<Group> groups;
	private static ArrayList<Notification> notifications;
	private static Account account;
	private static TaskOrganizer loadList;
	
	private static Boolean showAllTasks = true;
	
	private String[] searchOptions = {"Name", "Created Date", "Deadline"};
	
	@FXML
    public void initialize() {
		
		User.setLogedIn(true);
		
		account = User.getAccount();
		lists = User.getLists();
		
		int maxAttempts = 3;
        for (int count = 0; count < maxAttempts; count++) {
        	try {
				User.setGroups(GroupsDatabase.getAllGroups(User.getAccount()));
				groups = User.getGroups();
				User.setNotifications(NotificationDatabase.getAllNotifications(account));
				notifications = User.getNotifications();
				break;
			} catch (SQLException exception) {
				
				if (++count == maxAttempts) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Connection Error");
					alert.setHeaderText("Number of connection attempts exceeded!");
					alert.setContentText("Your section will be terminated");

					if(alert.showAndWait().get() == ButtonType.OK) {
						ActionEvent e = new ActionEvent();
						logout(e);
						return;	
					}	
				}
				else
					App.connectionErrorMessage();			
			}
        }

		printNameLabel.setText(account.getName());
		printEmailLabel.setText(account.getEmail());
		
		notificationLabel = new Label();
		notificationLabel.setTextFill(Color.RED);
		
		if(notifications.size() == 0)
			notificationNumber.setVisible(false);
		else {
			notificationNumber.setText(String.valueOf(notifications.size()));
			notificationNumber.setVisible(true);
		}
		
		choiceBox.setValue("Search by");
		choiceBox.getItems().addAll(searchOptions);
		choiceBox.setOnAction(this::searchOption);
		
		// Loads sort by options
		for (String option : SortBy.options()) {
			MenuItem item = new MenuItem(option);
			item.setOnAction(this::sortTasks);
			sortByMenu.getItems().add(item);
		}
		
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
			listOptionsBox.getChildren().remove(editListButton);
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
	
	public void logout(ActionEvent e) {
		
		User.setLogedIn(false);
		loadList = null;
		
		App.readLocalFiles();
		
		try {
			root = FXMLLoader.load(getClass().getResource("LoginScene.fxml"));
			App.loadScene(root);
		} catch (IOException exception) {
			exception.printStackTrace();
		}
		
	}
	
	public void editAccount(ActionEvent e) {
		
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("EditAccountScene.fxml"));
			root = loader.load();	
			ControllerEditAccountScene controller = loader.getController();
			controller.initData();
			App.loadScene(root);
		} catch (IOException exception) {
			exception.printStackTrace();
		}
		
	}
		
	public void addList(ActionEvent e) {
		
		removeErrorNotifications();
		
		String listName = newListName.getText();
		
		if(listName.isBlank())
			return;
		
		newListName.clear();
		
		Pane newBox = (Pane)newListBox;
		String notification;
		
		if(listName.length() > 128) {
			notification = "List name exceeds maximum character length allowed!";
			showNotification(notification,newBox);
			newListName.getStyleClass().add("error");
			return;
		}
		
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
			App.connectionErrorMessage();
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
		
		if(groupName.length() > 128) {
			notification = "Group name exceeds maximum character length allowed!";
			showNotification(notification,newBox);
			newGroupName.getStyleClass().add("error");
			return;
		}
		
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
			App.connectionErrorMessage();
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
		
		if(groupName.length() > 128) {
			notification = "Group name exceeds maximum character length allowed!";
			showNotification(notification,newBox);
			newGroupName.getStyleClass().add("error");
			return;
		}
		
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
			App.connectionErrorMessage();
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
		
		if(taskName.length() > 128) {
			String notification = "Task name exceeds maximum character length allowed!";
			showNotification(notification,(Pane)newTaskBox);
			newTaskName.getStyleClass().add("error");
			return;
		}
		
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
			App.connectionErrorMessage();
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
			App.connectionErrorMessage();
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

			if(showAllTasks)
				myTasksButton.setText("My Tasks");
			else
				myTasksButton.setText("All Tasks");
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

		try {
			PersonalTasksDatabase.updateTask(task.getID(), task.getName(), task.getDescription(), task.getDeadlineDate(), taskCheckBox.isSelected());
		} catch (SQLException exception) {
			App.connectionErrorMessage();
			return;
		}
		
		task.setCompleted(taskCheckBox.isSelected());
		
		if(taskCheckBox.isSelected())
			taskBar.setOpacity(0.5);
		else
			taskBar.setOpacity(1);
		
	}
	
	public void checkGroupTask(ActionEvent e) {
		
		removeErrorNotifications();
		
		CheckBox taskCheckBox = (CheckBox)e.getSource();
		GroupTaskBar taskBar = (GroupTaskBar)taskCheckBox.getParent();
		GroupTask task = taskBar.getTask();
		
		try {
			GroupTasksDatabase.updateTask(task.getID(), task.getName(), task.getDescription(), task.getDeadlineDate(), taskCheckBox.isSelected(), task.getAssignedToEmail());
		} catch (SQLException exception) {
			App.connectionErrorMessage();
			return;
		}
		
		task.setCompleted(taskCheckBox.isSelected());
		
		if(taskCheckBox.isSelected())
			taskBar.setOpacity(0.5);
		else
			taskBar.setOpacity(1);
		
	}
	
	public void sortTasks(ActionEvent e) {
		
		String option = ((MenuItem)e.getSource()).getText();
		loadList.sort(SortBy.fromString(option));
		
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
				for(Group g: groups)
					g.findTaskByName(text,searchList.getTaskList());			
				break;
			case "Created Date":
				for(List l: lists)
					l.findTaskByCreatedDate(date,searchList.getTaskList());			
				for(Group g: groups)
					g.findTaskByName(text,searchList.getTaskList());			
				break;
			case "Deadline":
				for(List l: lists)
					l.findTaskByDeadline(date,searchList.getTaskList());			
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
	
	public void showMyTasks(ActionEvent e) {
		
		showAllTasks = !showAllTasks;
		
		if(showAllTasks)
			myTasksButton.setText("My Tasks");
		else
			myTasksButton.setText("All Tasks");
		
		tasksBox.getChildren().clear();
		loadTasks();
	}
	
	public void notifications(ActionEvent e) {
		
		removeErrorNotifications();
		
		try {
			root = FXMLLoader.load(getClass().getResource("NotificationScene.fxml"));
			App.loadScene(root);
		} catch (IOException exception) {
			exception.printStackTrace();
		}
		
	}
	
	private void loadTasks() {
		
		boolean searchMode = (loadList instanceof SearchList);
		boolean isMemberOfTheGroup = (loadList instanceof Group) 
						&& !((Group)loadList).getManagerEmail().equals(User.getAccount().getEmail());
		boolean isMasterOfTheGroup = (loadList instanceof Group) 
				&& ((Group)loadList).getManagerEmail().equals(User.getAccount().getEmail());
		
		// Remove add tasks buttons and text fields for members, search lists and for the master with showMyTasks option
		if(searchMode || isMemberOfTheGroup || !showAllTasks)
			newTaskBox.getChildren().remove(addTaskBox);
		else if(!newTaskBox.getChildren().contains(addTaskBox))
			newTaskBox.getChildren().add(addTaskBox);
		
		addTaskBox.setVisible(true);
		
		// Remove edit list/group button for search lists
		if(searchMode)
			listOptionsBox.getChildren().remove(editListButton);
		else if(!listOptionsBox.getChildren().contains(editListButton))
			listOptionsBox.getChildren().add(editListButton);
		
		editListButton.setVisible(true);
		sortByMenu.setVisible(true);
		
		// Remove show my tasks / show all tasks for everyone besides master
		if(!isMasterOfTheGroup)
			listOptionsBox.getChildren().remove(myTasksButton);
		else if(!listOptionsBox.getChildren().contains(myTasksButton))
			listOptionsBox.getChildren().add(myTasksButton);
		
		myTasksButton.setVisible(true);
		
		if(showAllTasks)
			myTasksButton.setText("My Tasks");
		else
			myTasksButton.setText("All Tasks");
		
		listNameLabel.setText(loadList.getName());
		ArrayList<Task> tasks = loadList.getTaskList();
		for(Task t : tasks) {
			if (t instanceof PersonalTask) {
				TaskBar taskBar = new TaskBar(t,searchMode);
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
			else if(t instanceof GroupTask) {
				
				if(isMasterOfTheGroup && !showAllTasks) {
					String taskEmail = ((GroupTask)t).getAssignedToEmail();
					if(taskEmail == null)
						continue;
					if(!taskEmail.equals(User.getAccount().getEmail()))
						continue;		
				}
				
				GroupTaskBar taskBar = new GroupTaskBar((GroupTask)t,searchMode);
				CheckBox taskCheckBox = taskBar.getCheckBox();
				Button taskButton = taskBar.getButton();
				
				if(t.isCompleted()) {
					taskCheckBox.setSelected(true);
					taskBar.setOpacity(0.5);
				}
				else
					taskBar.setOpacity(1);
				
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