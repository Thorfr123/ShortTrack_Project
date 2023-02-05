package com.psw.shortTrack.gui;

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
import com.psw.shortTrack.data.SortBy;
import com.psw.shortTrack.data.Task;
import com.psw.shortTrack.data.TaskOrganizer;
import com.psw.shortTrack.data.User;
import com.psw.shortTrack.database.GroupTasksDatabase;
import com.psw.shortTrack.database.GroupsDatabase;
import com.psw.shortTrack.database.NotFoundException;
import com.psw.shortTrack.database.NotificationDatabase;
import com.psw.shortTrack.database.PersonalListsDatabase;
import com.psw.shortTrack.database.PersonalTasksDatabase;
import com.psw.shortTrack.gui_elements.GroupButton;
import com.psw.shortTrack.gui_elements.GroupTaskBar;
import com.psw.shortTrack.gui_elements.ListButton;
import com.psw.shortTrack.gui_elements.TaskBar;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
	private Label notificationLabel = new Label();
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
		
		if(!updateDataFromDatabase()) {
			User.setLogedIn(false);
			return;
		}

		printNameLabel.setText(account.getName());
		printEmailLabel.setText(account.getEmail());
		
		notificationLabel.setTextFill(Color.RED);
		
		if(notifications.size() == 0)
			notificationNumber.setVisible(false);
		else {
			notificationNumber.setText(String.valueOf(notifications.size()));
			notificationNumber.setVisible(true);
		}
		
		// Loads Search by options
		choiceBox.getItems().clear();
		choiceBox.setValue("Search by");
		choiceBox.getItems().addAll(searchOptions);
		choiceBox.setOnAction(this::searchOption);
		
		// Loads sort by options
		sortByMenu.getItems().clear();
		for (String option : SortBy.options()) {
			MenuItem item = new MenuItem(option);
			item.setOnAction(this::sortTasks);
			sortByMenu.getItems().add(item);
		}
		
		// Loads personal list buttons
		listsBox.getChildren().clear();
		for(List l : lists) {
			ListButton listButton = new ListButton(l);
			listButton.setOnAction(event -> {
		        changeList(event);
		    });
			listsBox.getChildren().add(listButton);
		}
		
		// Loads group buttons
		groupsBox.getChildren().clear();
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
			listOptionsBox.getChildren().remove(myTasksButton);
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

		App.loadScene("LoginScene.fxml");
		
	}
	
	public void editAccount(ActionEvent e) {
		
		FXMLLoader loader = App.loadScene("EditAccountScene.fxml");
		ControllerEditAccountScene controller = loader.getController();
		controller.initData();
		
	}
	
	public void notifications(ActionEvent e) {
		
		removeErrorNotifications();

		App.loadScene("NotificationScene.fxml");
		
	}
	
	
	public void editListOrGroup(ActionEvent e) {
		
		if(loadList instanceof List) {
			FXMLLoader loader = App.loadScene("EditListScene.fxml");
			ControllerEditListScene controller = loader.getController();
			controller.initData((List)loadList);
		}
		else if(loadList instanceof Group) {
			FXMLLoader loader = App.loadScene("EditGroupScene.fxml");
			ControllerEditGroupScene controller = loader.getController();
			controller.initData((Group)loadList);
		}
			
	}
	
	public void editTask(ActionEvent e) {
		
		Button taskButton = (Button)e.getSource();
		TaskBar taskBar = (TaskBar)taskButton.getParent();
		Task task = taskBar.getTask();

		FXMLLoader loader = App.loadScene("EditTaskScene.fxml");
		ControllerEditTaskScene controller = loader.getController();
		controller.initData(task,loadList);			
		
	}
	
	public void editGroupTask(ActionEvent e) {
		
		Button taskButton = (Button)e.getSource();
		GroupTaskBar taskBar = (GroupTaskBar)taskButton.getParent();
		GroupTask task = taskBar.getTask();

		FXMLLoader loader = App.loadScene("EditGroupTaskScene.fxml");
		ControllerEditGroupTaskScene controller = loader.getController();
		controller.initData(task,loadList);	

	}
		
	public void addList(ActionEvent e) {
		
		removeErrorNotifications();
		
		String listName = newListName.getText();
		
		if(listName.isBlank())
			return;
		
		newListName.clear();
		
		if(listName.length() > 128) {
			showNotification("List name exceeds maximum character length allowed!",(Pane)newListBox);
			return;
		}
		else if(User.checkListName(listName)) {
			showNotification("This List already exist!",(Pane)newListBox);
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
		
		if(groupName.length() > 128) {
			showNotification("Group name exceeds maximum character length allowed!",(Pane)newGroupBox);
			return;
		}
		else if(User.checkGroupName(groupName) != null) {
			showNotification("This Group already exist!",(Pane)newGroupBox);
			return;
		}
		
		Group newGroup = new Group(groupName,account);
		
		try {
			if (!GroupsDatabase.createGroup(newGroup)) {
				App.accountDeletedMessage();
				App.loadMainScene();
				return;
			}
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
		
		if(groupName.length() > 128) {
			showNotification("Group name exceeds maximum character length allowed!",(Pane)newGroupBox);
			return;
		}
		else if(User.checkGroupName(groupName) != null) {
			showNotification("This Group already exist!",(Pane)newGroupBox);
			return;
		}
		
		Group newGroup = new Group(groupName,account);
		
		try {
			if (!GroupsDatabase.createGroup(newGroup)) {
				App.accountDeletedMessage();
				App.loadMainScene();
				return;
			}
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

		FXMLLoader loader = App.loadScene("EditGroupScene.fxml");
		ControllerEditGroupScene controller = loader.getController();
		controller.initData(newGroup);
		
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
		
		if(loadList instanceof List)
			addTaskToList(taskName);
		else if(loadList instanceof Group)
			addTaskToGroup(taskName);
		
	}
	
	private PersonalTask addTaskToList(String taskName) {

		if(loadList.checkName(taskName)) {
			showNotification("This Task already exist!",(Pane)newTaskBox);
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
		
		createPersonalTaskButton(newTask,false);
		
		return newTask;
	}
	
	private GroupTask addTaskToGroup(String taskName) {

		if(loadList.checkName(taskName)) {
			showNotification("This Task already exist!",(Pane)newTaskBox);
			return null;
		}
		
		GroupTask newTask = new GroupTask(taskName,loadList.getID());

		try {
			GroupTasksDatabase.createTask(newTask);
		} 
		catch (NotFoundException nfe) {
			App.groupDeletedMessage();
			return null;
		}
		catch (SQLException exception) {
			App.connectionErrorMessage();
			return null;
		}
		
		((Group)loadList).addTask(newTask);
		
		createGroupTaskButton(newTask,false);
		
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
			FXMLLoader loader = App.loadScene("EditTaskScene.fxml");	
			ControllerEditTaskScene controller = loader.getController();
			controller.initData(newTask, loadList);	
		}
		else if(loadList instanceof Group) {
			FXMLLoader loader = App.loadScene("EditGroupTaskScene.fxml");
			ControllerEditGroupTaskScene controller = loader.getController();
			controller.initData((GroupTask)newTask, loadList);		
		}
		
	}
	
	public void changeList(ActionEvent e) {
		
		removeErrorNotifications();
		
		editListButton.setText("Edit List");
		ListButton listButton = (ListButton)e.getSource();
		loadList = listButton.getList();
		
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

		loadTasks();
		
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
			
			if (GroupTasksDatabase.hasPrivilege(task.getID(), User.getAccount().getEmail())) {
				GroupTasksDatabase.changeState(task.getID(), taskCheckBox.isSelected());
			}
			else {
				App.taskNoPrivilegesMessage();
				App.loadMainScene();
				return;
			}
			
		}
		catch (NotFoundException nfe) {
			App.taskDeletedMessage();
			App.loadMainScene();
			return;
		}
		catch (SQLException exception) {
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
	
	public void showMyTasks(ActionEvent e) {
		
		showAllTasks = !showAllTasks;
		
		if(showAllTasks)
			myTasksButton.setText("My Tasks");
		else
			myTasksButton.setText("All Tasks");
		
		loadTasks();
	}
	
	private void loadTasks() {
		
		tasksBox.getChildren().clear();
		
		boolean searchMode = (loadList instanceof SearchList);
		boolean isMasterOfTheGroup = (loadList instanceof Group) 
				&& ((Group)loadList).getManagerEmail().equals(User.getAccount().getEmail());
		
		setVisibleNodes(searchMode,isMasterOfTheGroup);
		
		listNameLabel.setText(loadList.getName());
		ArrayList<Task> tasks = loadList.getTaskList();
		for(Task t : tasks) {
			
			if (t instanceof PersonalTask)
				createPersonalTaskButton(t,searchMode);
			else if(t instanceof GroupTask) {
				
				if(isMasterOfTheGroup && !showAllTasks) {
					String taskEmail = ((GroupTask)t).getAssignedToEmail();
					if(taskEmail == null)
						continue;
					else if(!taskEmail.equals(User.getAccount().getEmail()))
						continue;		
				}
				
				createGroupTaskButton(t, searchMode);
				
			}

		}
	}
	
	private void createPersonalTaskButton(Task t, boolean searchMode) {
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
	
	private void createGroupTaskButton(Task t, boolean searchMode) {
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
	
	private void setVisibleNodes(boolean searchMode, boolean isMasterOfTheGroup) {
		
		boolean isMemberOfTheGroup = (loadList instanceof Group) 
				&& !((Group)loadList).getManagerEmail().equals(User.getAccount().getEmail());
		
		// Remove add tasks buttons and text fields for members, search lists and for the master with showMyTasks option
		if(searchMode || isMemberOfTheGroup || (!showAllTasks && (loadList instanceof Group)))
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
		if(!isMasterOfTheGroup) {
			listOptionsBox.getChildren().remove(myTasksButton);
		}
		else if(!listOptionsBox.getChildren().contains(myTasksButton)) {
			listOptionsBox.getChildren().add(myTasksButton);
		}
			
		myTasksButton.setVisible(true);
		
		if(showAllTasks)
			myTasksButton.setText("My Tasks");
		else
			myTasksButton.setText("All Tasks");
		
	}
	
	private boolean updateDataFromDatabase() {
		
		int maxAttempts = 3;
        for (int count = 1; count <= maxAttempts; count++) {
        	
        	try {	
        		
        		User.setGroups(GroupsDatabase.getAllGroups(User.getAccount()));
				groups = User.getGroups();
				
				User.setNotifications(NotificationDatabase.getAllNotifications(account));
				notifications = User.getNotifications();				
				break;
			
        	}
        	catch (NotFoundException nfe) {
        		
        		App.accountDeletedMessage();
        		logout(null);
        		return false;
        		
			} catch (SQLException exception) {
				
				if (count == maxAttempts) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Connection Error");
					alert.setHeaderText("Number of connection attempts exceeded!");
					alert.setContentText("Your section will be terminated");

					if(alert.showAndWait().get() == ButtonType.OK) {
						//ActionEvent e = new ActionEvent();
						System.out.println("Faz");
						
						Platform.runLater(() -> logout(null));
						
						//logout(e);
						//System.out.println("logout");
						return false;	
					}
				}
				else
					App.connectionErrorMessage();			
			}
        }
        
        return true;
	}

	private void showNotification(String notification, Pane newBox) {
		
		newBox.getChildren().add(notificationLabel);
		notificationLabel.setText(notification);
		System.out.println(notification);
		
		if(newBox.equals(newTaskBox))
			newTaskName.getStyleClass().add("error");
		else if(newBox.equals(newListBox))
			newListName.getStyleClass().add("error");
		else if(newBox.equals(newGroupBox))
			newGroupName.getStyleClass().add("error");
		else if(newBox.equals(searchVerticalBox))
			searchBarField.getStyleClass().add("error");
		
	}
	
	private void removeErrorNotifications() {
		
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