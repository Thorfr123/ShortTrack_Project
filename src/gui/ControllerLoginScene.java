package gui;

import database.*;
import data.*;
import java.io.IOException;
import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
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
	private VBox loginBox;
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
		
		if(lists == null) {
			lists = new ArrayList<List>();
			listNameLabel.setText("Choose one List!");
			return;
		}
		
		for(List l : lists) {
			ListButton list = new ListButton(l.getName());
			Button listButton = list.getButton();
			listButton.setOnAction(event -> {
		        changeList(event);
		    });
			listsBox.getChildren().add(listButton);
		}
		
		if(list == null) {
			listNameLabel.setText("Choose one List!");
			return;
		}	
		
		loadTasks();
    }

	public void login(ActionEvent e) throws IOException {
		
		String username = usernameField.getText();
		String password = passwordField.getText();
		
		if(!AccountsDatabase.checkLogin(username, password)) {
			
			if(notificationLabel == null) {
				notificationLabel = new Label("Invalid username or password!");
				notificationLabel.setTextFill(Color.RED);
				loginBox.getChildren().add(notificationLabel);
			}
			
			return;
		}
		
		loginBox.getChildren().remove(notificationLabel);
		
		Account account = AccountsDatabase.getAccount(username, password);
		String name = account.getName();
		String email = account.getEmail();
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("LogoutScene.fxml"));
		root = loader.load();
		ControllerLogoutScene logoutController = loader.getController();
		logoutController.displayName(name);
		logoutController.displayEmail(email);
		
		stage = (Stage)((Node)e.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
		
	}
	
	public void signUp(ActionEvent e) throws IOException {
		
		root = FXMLLoader.load(getClass().getResource("SignUpScene.fxml"));
		stage = (Stage)((Node)e.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.setMinWidth(290.0);
		stage.show();
		
	}
	
	public void addList(ActionEvent e) {
		
		String listName = newListName.getText();
		
		if(listName.isBlank())
			return;
		
		newListName.clear();
		
		for(List l : lists) {
			if(l.getName().equals(listName)) {
				System.out.println("Ja existe uma lista com esse nome!");
				return;
			}
		}
		
		List newList = new List(listName);
		lists.add(newList);
		
		ListButton list = new ListButton(listName);
		Button listButton = list.getButton();
		listButton.setOnAction(event -> {
	        changeList(event);
	    });
		listsBox.getChildren().add(listButton);
		
	}
	
	public void addTask(ActionEvent e) {
				
		String taskName = newTaskName.getText();
		
		if(taskName.isBlank())
			return;
		
		newTaskName.clear();
		
		if(list.checkName(taskName)) {
			System.out.println("Ja existe uma task com esse nome nesta lista!");
			return;
		}
		
		Task newTask = new Task(taskName);
		list.addTask(newTask);
		
		TaskBar task = new TaskBar(taskName);
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
	
	public void changeList(ActionEvent e) {
		
		String text = e.getSource().toString();
		String listName = text.substring(text.indexOf("'")+1, 
	               text.indexOf("'", text.indexOf("'")+1));
		
		for(List l : lists) {
			if(l.getName().equals(listName))
				list = l;
		}
		
		tasksBox.getChildren().clear();
		
		loadTasks();
	}
	
	public void loadTasks() {
		listNameLabel.setText(list.getName());
		
		ArrayList<Task> tasks = list.getTaskList();
		for(Task t : tasks) {
			TaskBar task = new TaskBar(t.getName());
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
		
		editListButton.setVisible(true);
		addTaskBox.setVisible(true);
	}
	
	public void editList(ActionEvent e) {
		
		System.out.println(list.getName());
		
	}
	
	public void editTask(ActionEvent e) {
		
		String text = e.getSource().toString();
		String taskName = text.substring(text.indexOf("'")+1, 
	               text.indexOf("'", text.indexOf("'")+1));
		
		Task task = null;
		ArrayList<Task> tasks = list.getTaskList();
		for(Task t : tasks) {
			if(t.getName().equals(taskName))
				task = t;
		}
		
		try {
			
			FXMLLoader loader = new FXMLLoader(getClass().getResource("EditTaskScene.fxml"));
			root = loader.load();
			stage = (Stage)((Node)e.getSource()).getScene().getWindow();
			scene = new Scene(root);
			stage.setScene(scene);
			stage.setMinWidth(320.0);
			
			ControllerEditTaskScene controller = loader.getController();
			controller.initData(task, list);	

			stage.show();
			
		} catch (IOException exeption) {
			exeption.printStackTrace();
		}
		
	}
	
	public void checkTask(ActionEvent e) {				// Falta Check task
		
		/*String text = e.getSource().toString();
		System.out.println(text);
		String value = text.substring(text.indexOf("'")+1, 
	               text.indexOf("'", text.indexOf("'")+1));
		System.out.println(value);*/
		
	}
}
