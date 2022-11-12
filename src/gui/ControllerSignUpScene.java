package gui;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	private PasswordField repeatPasswordField;
	@FXML 
	private Label notificationLabel;
	
	private Stage stage;
	private Scene scene;
	private Parent root;

	public void create(ActionEvent e) throws IOException {
		
		removeErrorNotifications();
		

		String firstName = firstNameField.getText();
		String lastName =  lastNameField.getText();
		String email = emailField.getText();
		String username = usernameField.getText();
		String password = passwordField.getText();
		String repeatPassword = repeatPasswordField.getText();
		
		if(!checkFields(firstName, lastName, email, username, password, repeatPassword))
			return;
		
		String name = firstName + " " +  lastName;
		
		try {
			if (AccountsDatabase.createAccount(username, password, email, firstName, lastName) < 0) {
				showNotification("There was an unknown error");
				return;
			}
		} catch (SQLException e1) {
			showNotification("Error! Please, check your conection");
			return;
		}
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("LogoutScene.fxml"));
		root = loader.load();
		ControllerLogoutScene logoutController = loader.getController();
		logoutController.displayName(name);
		logoutController.displayEmail(email);
		
		stage = (Stage)((Node)e.getSource()).getScene().getWindow();
		loadScene();
		stage.show();
		
	}
	
	public void cancel(ActionEvent e) throws IOException {
		
		removeErrorNotifications();
		
		root = FXMLLoader.load(getClass().getResource("LoginScene.fxml"));
		stage = (Stage)((Node)e.getSource()).getScene().getWindow();
		loadScene();
		stage.show();
		
	}
	
	private boolean checkFields(String firstName, String lastName, String email, String username,
								String password, String repeatPassword) {
		
		if(firstName.isBlank())
			firstNameField.getStyleClass().add("error");

		if(lastName.isBlank())
			lastNameField.getStyleClass().add("error");
		
		if(email.isBlank())
			emailField.getStyleClass().add("error");
		
		if(username.isBlank())
			usernameField.getStyleClass().add("error");
		
		if(password.isBlank())
			passwordField.getStyleClass().add("error");
		
		if(repeatPassword.isBlank())
			repeatPasswordField.getStyleClass().add("error");
		
		if(firstName.isBlank() || lastName.isBlank() || email.isBlank() 
				|| username.isBlank() || password.isBlank() || repeatPassword.isBlank()) {
			showNotification("Please complete all the fields!");
			return false;
		}
		
		if(!checkName(firstName) || !checkName(lastName) || !checkEmail(email) || !checkUsername(username) 
				|| !checkPassword(password,repeatPassword) || !checkDatabase(email,username) ) {
			return false;
		}
		
		return true;
	}
	
	private void showNotification(String notification) {
		
		notificationLabel.setText(notification);
		notificationLabel.setTextFill(Color.RED);
		notificationLabel.setVisible(true);
		
	}
	
	private void removeErrorNotifications() {
		
		firstNameField.getStyleClass().removeAll(Collections.singleton("error"));
		lastNameField.getStyleClass().removeAll(Collections.singleton("error"));
		emailField.getStyleClass().removeAll(Collections.singleton("error"));
		usernameField.getStyleClass().removeAll(Collections.singleton("error"));
		passwordField.getStyleClass().removeAll(Collections.singleton("error"));
		repeatPasswordField.getStyleClass().removeAll(Collections.singleton("error"));
		notificationLabel.setVisible(false);
		
	}
	
	private boolean checkName(String name) {				//Just Scratch
		if(name.contains(" ")) {
			showNotification("Invalid name!");
			firstNameField.getStyleClass().add("error");
			lastNameField.getStyleClass().add("error");
			return false;
		}
			
		if(name.length() > 15 || name.length() < 1) {
			showNotification("Invalid name!");
			firstNameField.getStyleClass().add("error");
			lastNameField.getStyleClass().add("error");
			return false;
		}
		
		return true;
	}
	
	private boolean checkEmail(String email) {
		//Regular Expression   
        String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}";  
        //Compile regular expression to get the pattern  
        Pattern pattern = Pattern.compile(regex);
        
        Matcher matcher = pattern.matcher(email);
        
        if(!matcher.matches()) {
			showNotification("Invalid email!");
			emailField.getStyleClass().add("error");
			return false;
		}
        	
		return true;
	}
	
	private boolean checkUsername(String username) {
		
		if(username.length() < 5) {
			showNotification("Username to short!");
			usernameField.getStyleClass().add("error");
			return false;
		}
		
		return true;
	}
	
	private boolean checkPassword(String password, String repeatPassword) { 
		if(password.length() < 5) {
			showNotification("Password to short!");
			passwordField.getStyleClass().add("error");
			return false;
		}
		
		if(!password.equals(repeatPassword)) {
			showNotification("The passwords don't match!");
			passwordField.getStyleClass().add("error");
			repeatPasswordField.getStyleClass().add("error");
			return false;
		}
		
		return true;
	}
	
	private boolean checkDatabase(String email, String username) {
		
		try {
			if(!AccountsDatabase.checkEmail(email)) {
				showNotification("Email already in use!");
				emailField.getStyleClass().add("error");
				return false;
			}
			if(!AccountsDatabase.checkUsername(username)) {
				showNotification("Username already in use!");
				usernameField.getStyleClass().add("error");
				return false;
			}
		} catch (SQLException e) {
			showNotification("Error! Please, check your conection");
			return false;
		}
		
		return true;
	}
	
	private void loadScene() {
		
		scene = new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());
		String css = this.getClass().getResource("application.css").toExternalForm();
		scene.getStylesheets().add(css);
		stage.setScene(scene);
		stage.show();
		
	}
	
}
