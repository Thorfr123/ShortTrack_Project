package gui;

import java.io.IOException;
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
		
		String firstName = firstNameField.getText();
		String lastName =  lastNameField.getText();
		String email = emailField.getText();
		String username = usernameField.getText();
		String password = passwordField.getText();
		String repeatPassword = repeatPasswordField.getText();
		
		if((firstName.isBlank()) || (lastName.isBlank()) || (email.isBlank()) 
				|| (username.isBlank()) || (password.isBlank()) || (repeatPassword.isBlank())) {
			showNotification("Please complete all the fields!");
			return;
		}
		
		if(!checkName(firstName) || !checkName(lastName) || !checkPassword(password,repeatPassword)
				|| !checkEmail(email) || !checkUsername(username) ) {
			return;
		}
		String name = firstName + " " +  lastName;
		
		if (AccountsDatabase.createAccount(username, password, email, firstName, lastName) < 0) {
			showNotification("There was an unknown error");
			return;
		}
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("LogoutScene.fxml"));
		root = loader.load();
		ControllerLogoutScene logoutController = loader.getController();
		logoutController.displayName(name);
		logoutController.displayEmail(email);
		
		stage = (Stage)((Node)e.getSource()).getScene().getWindow();
		scene = new Scene(root);
		String css = this.getClass().getResource("application.css").toExternalForm();
		scene.getStylesheets().add(css);
		stage.setScene(scene);
		stage.setMinWidth(820.0);
		stage.show();
		
	}
	
	public void cancel(ActionEvent e) throws IOException {
		
		root = FXMLLoader.load(getClass().getResource("LoginScene.fxml"));
		stage = (Stage)((Node)e.getSource()).getScene().getWindow();
		scene = new Scene(root);
		String css = this.getClass().getResource("application.css").toExternalForm();
		scene.getStylesheets().add(css);
		stage.setScene(scene);
		stage.setMinWidth(820.0);
		stage.show();
		
	}
	
	public void showNotification(String notification) {
		
		notificationLabel.setText(notification);
		notificationLabel.setTextFill(Color.RED);
		
	}
	
	public boolean checkName(String name) {				//Just Scratch
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
	
	public boolean checkEmail(String email) {
		//Regular Expression   
        String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}";  
        //Compile regular expression to get the pattern  
        Pattern pattern = Pattern.compile(regex);
        
        Matcher matcher = pattern.matcher(email);
        
        if(!matcher.matches()) {
			showNotification("Invalid email!");
			return false;
		}
        	
		
		if(!AccountsDatabase.checkEmail(email)) {
			showNotification("Email already in use!");
			return false;
		}

		return true;
	}
	
	public boolean checkUsername(String username) {
		
		if(username.length() < 5) {
			showNotification("Username to short!");
			return false;
		}
		
		if(!AccountsDatabase.checkUsername(username)) {
			showNotification("Username already in use!");
			return false;
		}
		
		return true;
	}
	
	public boolean checkPassword(String password, String repeatPassword) { 
		if(password.length() < 5) {
			showNotification("Password to short!");
			return false;
		}
		
		if(!password.equals(repeatPassword)) {
			showNotification("The passwords don't match!");
			return false;
		}
		
		return true;
	}
	
	
	
}
