package com.psw.shortTrack.gui;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import com.psw.shortTrack.data.Account;
import com.psw.shortTrack.data.Group;
import com.psw.shortTrack.data.List;
import com.psw.shortTrack.data.User;
import com.psw.shortTrack.database.AccountsDatabase;
import com.psw.shortTrack.database.GroupsDatabase;
import com.psw.shortTrack.database.PersonalListsDatabase;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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
	
	private Parent root;

	public void create(ActionEvent e) throws IOException {
		
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
				showNotification("There was an unknown error");
				return;
			}
		} catch (SQLException e1) {
			App.connectionErrorMessage();
			return;
		}
		
		User.setAccount(account);
		
		App.writeLocalFiles();

		ArrayList<Group> groups;
		ArrayList<List> lists; 
		try {
			groups = GroupsDatabase.getAllGroups(account);
			lists = PersonalListsDatabase.getAllLists(account.getEmail());
		} catch (SQLException exeption) {
			App.connectionErrorMessage();
			return;
		}
		User.setGroups(groups);
		User.setLists(lists);
		
		
		root = FXMLLoader.load(getClass().getResource("LogoutScene.fxml"));
		App.loadScene(root);
	}
	
	public void cancel(ActionEvent e) throws IOException {
		
		removeErrorNotifications();
		
		root = FXMLLoader.load(getClass().getResource("LoginScene.fxml"));
		App.loadScene(root);
		
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
			App.connectionErrorMessage();
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
		else if(!checkDatabase(email)) {
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
	
	private boolean checkDatabase(String email) {
		
		try {
			if(!AccountsDatabase.checkEmail(email)) {
				showNotification("Email already in use!");
				emailField.getStyleClass().add("error");
				return false;
			}
		} catch (SQLException e) {
			App.connectionErrorMessage();
			return false;
		}
		
		return true;
	}
	
}
