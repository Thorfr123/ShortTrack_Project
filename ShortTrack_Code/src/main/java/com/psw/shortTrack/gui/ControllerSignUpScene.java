package com.psw.shortTrack.gui;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import com.psw.shortTrack.data.Account;
import com.psw.shortTrack.data.Group;
import com.psw.shortTrack.data.List;
import com.psw.shortTrack.data.User;
import com.psw.shortTrack.database.AccountsDatabase;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

public class ControllerSignUpScene {
	
	@FXML 
	private TextField firstNameField;
	@FXML 
	private TextField lastNameField;
	@FXML 
	private TextField emailField;
	@FXML 
	private PasswordField passwordField;
	@FXML 
	private PasswordField repeatPasswordField;
	@FXML 
	private Label notificationLabel;

	public void create(ActionEvent e) {
		
		removeErrorNotifications();

		String firstName = firstNameField.getText();
		String lastName =  lastNameField.getText();
		String email = emailField.getText();
		String password = passwordField.getText();
		String repeatPassword = repeatPasswordField.getText();
		
		if(!checkFields(firstName, lastName, email, password, repeatPassword))
			return;
		
		String name = firstName + " " +  lastName;
		Account account;
		try {
			
			account = new Account(email,name);
			if (!AccountsDatabase.createAccount(account, password)) {
				showNotification("Email already in use!");
				emailField.getStyleClass().add("error");
				return;
			}
			
		} catch (SQLException sqle) {
			App.connectionErrorMessage();
			return;
		}
		
		User.setAccount(account);
		
		App.writeLocalFiles();
		
		User.setGroups(new ArrayList<Group>(0));
		User.setLists(new ArrayList<List>(0));

		App.loadScene("LogoutScene.fxml");
		
	}
	
	public void cancel(ActionEvent e) {

		App.loadScene("LoginScene.fxml");
		
	}
	
	private boolean checkFields(String firstName, String lastName, String email,
								String password, String repeatPassword) {
		
		if(firstName.isBlank())
			firstNameField.getStyleClass().add("error");

		if(lastName.isBlank())
			lastNameField.getStyleClass().add("error");
		
		if(email.isBlank())
			emailField.getStyleClass().add("error");
		
		if(password.isBlank())
			passwordField.getStyleClass().add("error");
		
		if(repeatPassword.isBlank())
			repeatPasswordField.getStyleClass().add("error");
		
		if(firstName.isBlank() || lastName.isBlank() || email.isBlank() 
				|| password.isBlank() || repeatPassword.isBlank()) {
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
		else if((errorNotification = Account.checkValidPassword(password, repeatPassword)) != null) {
			showNotification(errorNotification);
			passwordField.getStyleClass().add("error");
			repeatPasswordField.getStyleClass().add("error");
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
		passwordField.getStyleClass().removeAll(Collections.singleton("error"));
		repeatPasswordField.getStyleClass().removeAll(Collections.singleton("error"));
		notificationLabel.setVisible(false);
		
	}
	
}
