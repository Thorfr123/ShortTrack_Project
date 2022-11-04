package gui;

import java.io.IOException;
import database.AccountsDatabase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ControllerSignUpScene {
	
	@FXML 
	private TextField firstNameField;
	@FXML 
	private TextField lastNameField;
	@FXML 
	private TextField emailField;
	@FXML 
	private TextField usernameField;
	@FXML 
	private PasswordField passwordField;
	@FXML 
	private Label notificationLabel;
	
	private Stage stage;
	private Scene scene;
	private Parent root;

	public void create(ActionEvent e) throws IOException {
		
		String firstName = firstNameField.getText();
		String lastName =  lastNameField.getText();
		String email = emailField.getText();
		String username = usernameField.getText();
		String password = passwordField.getText();
		
		if((firstName.isBlank()) || (lastName.isBlank()) || (email.isBlank()) 
				|| (username.isBlank()) || (password.isBlank())) {
			showNotification("Please complete all the fields!");
			return;
		}
		
		if(!checkName(firstName) || !checkName(lastName) || !checkEmail(email) 
				|| !checkUsername(username) || !checkPassword(password)) {
			return;
		}
		String name = firstName + " " +  lastName;
		
		int rt = AccountsDatabase.createAccount(username, password, email, firstName, lastName);
		
		switch (rt) {
			case -1:
				showNotification("Connection error");
				return;
			case -2:
				showNotification("Email already in use");
				return;
			case -3:
				showNotification("Username already in use");
				return;
		}
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("LogoutScene.fxml"));
		root = loader.load();
		ControllerLogoutScene logoutController = loader.getController();
		logoutController.displayName(name);
		logoutController.displayEmail(email);
		
		stage = (Stage)((Node)e.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.setMinWidth(820.0);
		stage.show();
		
	}
	
	public void cancel(ActionEvent e) throws IOException {
		
		root = FXMLLoader.load(getClass().getResource("LoginScene.fxml"));
		stage = (Stage)((Node)e.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.setMinWidth(820.0);
		stage.show();
		
	}
	
	public void showNotification(String notification) {
		
		notificationLabel.setText(notification);
		notificationLabel.setTextFill(Color.RED);
		
	}
	
	public boolean checkName(String name) {
		if(name.contains(" ")) {
			showNotification("Invalid name!");
			return false;
		}
			
		if(name.length() > 15 || name.length() < 1) {
			showNotification("Invalid name!");
			return false;
		}
		
		return true;
	}
	
	public boolean checkEmail(String email) { 				//Just a scratch
		if(!email.contains("@") || (email.length() < 1)) {
			showNotification("Invalid email!");
			return false;
		}
		
		if(!AccountsDatabase.checkEmail(email)) {
			showNotification("Email already in use!");
			return false;
		}

		return true;
	}
	
	public boolean checkUsername(String username) { 		//Just a scratch

		return true;
	}
	
	public boolean checkPassword(String password) { 
		if(password.length() < 5) {
			showNotification("Password to short!");
			return false;
		}

		return true;
	}
	
	
	
}
