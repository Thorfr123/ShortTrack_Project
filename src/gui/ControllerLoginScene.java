package gui;

import database.*;
import data.*;
import java.io.IOException;
import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
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
	private TextField usernameField;
	@FXML 
	private PasswordField passwordField;
	@FXML
	private Button loginButton;
	@FXML
	private Button signUpButton;
	@FXML
	private Label usernameLabel;
	@FXML
	private Label passwordLabel;
	@FXML
	private Label listNameLabel;
	@FXML 
	private Label notificationLabel;
	@FXML 
	private TextField newListName;
	@FXML 
	private TextField newTaskName;
	@FXML 
	private VBox loginBox;
	@FXML 
	private VBox listsBox;
	@FXML 
	private VBox tasksBox;
	
	private Stage stage;
	private Scene scene;
	private Parent root;
	
	private static ArrayList<List> lists;
	private static ArrayList<ListButton> listButtons;
	private static ArrayList<TaskBar> taskBars;
	private List list;
	
	@FXML
    public void initialize() {
		if(lists == null) {
			lists = new ArrayList<List>();
			listButtons = new ArrayList<ListButton>();
			taskBars = new ArrayList<TaskBar>();
			return;
		}
		
		for(List l : lists) {
			ListButton list = new ListButton(l.getName());
			listButtons.add(list);
			listsBox.getChildren().add(list.getButton());
		}
		
		listNameLabel.setText("Choose one List!");
		
		/*ArrayList<Task> tasks = list.getTaskList();
		for(Task t : tasks) {
			TaskBar task = new TaskBar(t.getName());
			taskBars.add(task);
			tasksBox.getChildren().add(task.getHBox());
		}*/
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
			
			System.out.println("Invalid username or password!");
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
		listButtons.add(list);
		listsBox.getChildren().add(list.getButton());
		
	}
	
	public void addTask(ActionEvent e) {
				
		String taskName = newTaskName.getText();
		
		if(taskName.isBlank())
			return;
		
		newTaskName.clear();
		
		/*if(list.checkName(taskName)) {
			System.out.println("Ja existe uma task com esse nome nesta lista!");
			return;
		}*/
		
		TaskBar task = new TaskBar(taskName);
		taskBars.add(task);
		tasksBox.getChildren().add(task.getHBox());
		
	}
	
	
}
