package gui;

import database.*;
import data.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

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
	private Button loginButton;
	@FXML
	private Button signUpButton;
	@FXML
	private Button editListButton;
	@FXML
	private MenuButton sortByMenu;
	
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
	
	private Stage stage;
	private Scene scene;
	private Parent root;
	
	private static ArrayList<List> lists;
	private static List list;
	
	@FXML
    public void initialize() {
		
		notificationLabel = new Label();
		notificationLabel.setTextFill(Color.RED);
		
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
		
		if (list != null) {
			if (!lists.contains(list)) {
				list = null;
			}
		}
		
		if(list == null) {
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
		} catch (SQLException e1) {
			Pane newBox = (Pane)loginBox;
			String notification = "Error! Please, check your connection";
			showNotification(notification,newBox);
			
			return;
		}
		
		Account account = null;
		try {
			account = AccountsDatabase.getAccount(username, password);
		} catch (SQLException e1) {
			Pane newBox = (Pane)loginBox;
			String notification = "Error! Please, check your connection";
			showNotification(notification,newBox);
			
			return;
		}
		
		String name = account.getName();
		String email = account.getEmail();
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("LogoutScene.fxml"));
		root = loader.load();
		ControllerLogoutScene logoutController = loader.getController();
		logoutController.displayName(name);
		logoutController.displayEmail(email);
		
		stage = (Stage)((Node)e.getSource()).getScene().getWindow();
		loadScene();
		stage.show();
		
	}
	
	public void signUp(ActionEvent e) throws IOException {
		
		root = FXMLLoader.load(getClass().getResource("SignUpScene.fxml"));
		stage = (Stage)((Node)e.getSource()).getScene().getWindow();
		loadScene();
		stage.show();
		
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
		
		Task newTask = new Task(taskName);
		list.addTask(newTask);
		
		TaskBar task = new TaskBar(newTask);
		CheckBox taskCheckBox = task.getCheckBox();
		Button taskButton = task.getButton();
		
		taskCheckBox.setOnAction(event -> {
            checkTask(event);
        });
		taskButton.setOnAction(event -> {
            editTask(event);
        });
		
		tasksBox.getChildren().add(task.getHBox());
		
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
		
		Task newTask = new Task(taskName);
		list.addTask(newTask);
		
		TaskBar task = new TaskBar(newTask);
		CheckBox taskCheckBox = task.getCheckBox();
		Button taskButton = task.getButton();
		
		taskCheckBox.setOnAction(event -> {
            checkTask(event);
        });
		taskButton.setOnAction(event -> {
            editTask(event);
        });
		
		tasksBox.getChildren().add(task.getHBox());
		
		try {
			
			FXMLLoader loader = new FXMLLoader(getClass().getResource("EditTaskScene.fxml"));
			root = loader.load();
			stage = (Stage)((Node)e.getSource()).getScene().getWindow();
			loadScene();
			
			ControllerEditTaskScene controller = loader.getController();
			controller.initData(newTask, list);	

			stage.show();
			
		} catch (IOException exeption) {
			exeption.printStackTrace();
		}
		
	}
	
	public void changeList(ActionEvent e) {
		
		removeErrorNotifications();
		
		Button listButton = (Button)e.getSource();
		String listName = listButton.getText();
		
		for(List l : lists) {
			if(l.getName().equals(listName)) {
				list = l;
				break;
			}
		}
		
		tasksBox.getChildren().clear();
		loadTasks();
	}
	
	public void loadTasks() {
		editListButton.setVisible(true);
		sortByMenu.setVisible(true);
		
		listNameLabel.setText(list.getName());
		
		ArrayList<Task> tasks = list.getTaskList();
		for(Task t : tasks) {
			TaskBar task = new TaskBar(t);
			CheckBox taskCheckBox = task.getCheckBox();
			Button taskButton = task.getButton();
			
			if(t.chekCompleted())
				taskCheckBox.setSelected(true);
			
			taskCheckBox.setOnAction(event -> {
	            checkTask(event);
	        });
			taskButton.setOnAction(event -> {
	            editTask(event);
	        });
			
			tasksBox.getChildren().add(task.getHBox());
		}
		
		addTaskBox.setVisible(true);
		
	}
	
	public void editList(ActionEvent e) {
		
		String listName = listNameLabel.getText();

		List list = null;
		for (List l : lists) {
			if (l.getName().equals(listName)) {
				list = l;
				break;
			}
		}
		
		if (list == null) {											//Maybe useless
			return;
		}
		
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("EditListScene.fxml"));
			root = loader.load();
			stage = (Stage)((Node)e.getSource()).getScene().getWindow();
			loadScene();
			
			ControllerEditListScene controller = loader.getController();
			controller.initData(list, lists);	

			stage.show();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		
	}
	
	public void editTask(ActionEvent e) {
		
		Button taskButton = (Button)e.getSource();
		VBox taskLabels = (VBox)taskButton.getGraphic();
		Label taskNameLabel = (Label)taskLabels.getChildren().get(0);
		String taskName = taskNameLabel.getText();
		
		Task task = null;
		ArrayList<Task> tasks = list.getTaskList();
		for(Task t : tasks) {
			if(t.getName().equals(taskName)) {
				task = t;
				break;
			}
		}
		
		try {
			
			FXMLLoader loader = new FXMLLoader(getClass().getResource("EditTaskScene.fxml"));
			root = loader.load();
			stage = (Stage)((Node)e.getSource()).getScene().getWindow();
			loadScene();
			
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
		HBox taskBox = (HBox)taskCheckBox.getParent();
		Button taskButton = (Button)taskBox.getChildren().get(1);		
		VBox taskLabels = (VBox)taskButton.getGraphic();
		Label taskNameLabel = (Label)taskLabels.getChildren().get(0);
		String taskName = taskNameLabel.getText();

		Task task = null;
		ArrayList<Task> tasks = list.getTaskList();
		for(Task t : tasks) {
			if(t.getName().equals(taskName)) {
				task = t;
				break;
			}
		}
		
		task.setCompleted(taskCheckBox.isSelected());
	}
	
	public void loadScene() {
		
		scene = new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());
		String css = this.getClass().getResource("application.css").toExternalForm();
		scene.getStylesheets().add(css);
		stage.setScene(scene);
		
	}
	
	public void showNotification(String notification, Pane newBox) {
		
		newBox.getChildren().add(notificationLabel);
		notificationLabel.setText(notification);
		
	}
	
	public void removeErrorNotifications() {
		
		loginBox.getChildren().remove(notificationLabel);
		newTaskBox.getChildren().remove(notificationLabel);
		newListBox.getChildren().remove(notificationLabel);
		
		usernameField.getStyleClass().removeAll(Collections.singleton("error")); 
		passwordField.getStyleClass().removeAll(Collections.singleton("error")); 
		newListName.getStyleClass().removeAll(Collections.singleton("error")); 
		newTaskName.getStyleClass().removeAll(Collections.singleton("error")); 

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
}
