package com.psw.shortTrack.gui;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;

import com.psw.shortTrack.data.Account;
import com.psw.shortTrack.database.AccountsDatabase;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
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
		App.loadScene(root,stage);
		stage.show();
		
	}
	
	public void cancel(ActionEvent e) throws IOException {
		
		removeErrorNotifications();
		
		root = FXMLLoader.load(getClass().getResource("LoginScene.fxml"));
		stage = (Stage)((Node)e.getSource()).getScene().getWindow();
		App.loadScene(root,stage);
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
		
		String errorNotification;
		if((errorNotification = Account.checkValidName(firstName)) != null) {
			showNotification(errorNotification);
			firstNameField.getStyleClass().add("error");
			return false;
		}
		else if((errorNotification = Account.checkValidName(lastName)) != null) {
			showNotification(errorNotification);
			lastNameField.getStyleClass().add("error");
			return false;
		}
		else if((errorNotification = Account.checkValidEmail(email)) != null) {
			showNotification(errorNotification);
			emailField.getStyleClass().add("error");
			return false;
		}
		else if((errorNotification = Account.checkValidName(username)) != null) {
			showNotification(errorNotification);
			usernameField.getStyleClass().add("error");
			return false;
		}
		else if((errorNotification = Account.checkValidPassword(password, repeatPassword)) != null) {
			showNotification(errorNotification);
			passwordField.getStyleClass().add("error");
			repeatPasswordField.getStyleClass().add("error");
			return false;
		}
		else if(!checkDatabase(email,username)) {
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
	
}
