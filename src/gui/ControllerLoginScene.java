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
			lists = User.getLists();
		}
		
		/*
		 * Coloquei o array a inicializar na classe User, porque ele vai ser sempre preciso e assim já não é necessário
		 * o código abaixo
		 */
		/*
		if(User.getLists() == null) {
			System.out.println("A");
			lists = new ArrayList<List>();
			User.setLists(lists);
		}
		*/
		
		for(List l : lists) {
			ListButton lst = new ListButton(l.getName());
			Button listButton = lst.getButton();
			listButton.setOnAction(event -> {
		        changeList(event);
		    });
			listsBox.getChildren().add(listButton);
		}
		
		/*
		 * Verifica se existe a lista selecionada, porque quando se apagava a lista no scene de editList
		 * a lista não era apagada neste scene.
		 * Então o nome da lista e o botao continuavam a aparecer
		 */
		if (list != null) {
			List temp = null;
			for (List lst : lists) {
				if (lst.getName().equals(list.getName())) {
					temp = lst;
					break;
				}
			}
			if (temp == null) {
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
		
		String username = usernameField.getText();
		String password = passwordField.getText();
		
		if(username.isBlank() || password.isBlank()) {
			System.out.println("Necessario colocar username e password!");
			return;
		}
		
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
		
		Button listButton = (Button)e.getSource();
		String listName = listButton.getText();
		
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
		
		editListButton.setVisible(true);
		addTaskBox.setVisible(true);
	}
	
	public void editList(ActionEvent e) {
		
		String listName = listNameLabel.getText();

		List list = null;
		for (List l : lists) {
			if (l.getName().equals(listName)) {
				list = l;
			}
		}
		if (list == null) {
			return;
		}
		
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("EditListScene.fxml"));
			root = loader.load();
			stage = (Stage)((Node)e.getSource()).getScene().getWindow();
			scene = new Scene(root);
			stage.setScene(scene);
			stage.setMinHeight(480.0);
			stage.setMinWidth(350.0);
			
			ControllerEditListScene controller = loader.getController();
			controller.initData(list, lists);	

			stage.show();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		
	}
	
	public void editTask(ActionEvent e) {
		
		Button taskButton = (Button)e.getSource();
		String taskName = taskButton.getText();
		
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
			stage.setMinHeight(480.0);
			stage.setMinWidth(350.0);
			
			ControllerEditTaskScene controller = loader.getController();
			controller.initData(task, list);	

			stage.show();
			
		} catch (IOException exeption) {
			exeption.printStackTrace();
		}
		
	}
	
	public void checkTask(ActionEvent e) {
		
		CheckBox taskCheckBox = (CheckBox)e.getSource();
		HBox taskBox = (HBox)taskCheckBox.getParent();
		Button taskButton = (Button)taskBox.getChildren().get(1);
		String taskName = taskButton.getText();

		Task task = null;
		ArrayList<Task> tasks = list.getTaskList();
		for(Task t : tasks) {
			if(t.getName().equals(taskName))
				task = t;
		}
		
		task.setCompleted(taskCheckBox.isSelected());
	}
}
