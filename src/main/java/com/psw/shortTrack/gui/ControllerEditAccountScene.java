package com.psw.shortTrack.gui;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;

import com.psw.shortTrack.data.Account;
import com.psw.shortTrack.data.User;
import com.psw.shortTrack.database.AccountsDatabase;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.paint.Color;

public class ControllerEditAccountScene {

    @FXML
    private PasswordField confirmNewPasswordField;

    @FXML
    private TextField newEmailField;

    @FXML
    private TextField newFirstNameField;

    @FXML
    private TextField newLastNameField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField oldPasswordField;
    
    @FXML
    private Label notificationLabel;

    private Parent root;
    
    private Account account;
    
	public void initData() {

		account = User.getAccount();
		
		String fullName = account.getName();
		
		newFirstNameField.setText(fullName.substring(0, fullName.indexOf(' ')));
		newLastNameField.setText(fullName.substring(fullName.indexOf(' ') + 1, fullName.length()));
		newEmailField.setText(account.getEmail());		
		
    }
    
    
    @FXML
    void cancel(ActionEvent event) throws IOException {
    	
		removeErrorNotifications();
		App.loadMainScene();
    	
    }

    @FXML
    void delete(ActionEvent event) throws IOException {

    	removeErrorNotifications();
    	
    	Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Delete account");
		alert.setHeaderText("You're about to delete your account and all your data!");
		alert.setContentText("Are you sure you really want to delete your account?");

		if(alert.showAndWait().get() == ButtonType.OK){
			
			try {
				if (!AccountsDatabase.deleteAccount(newEmailField.getText())) {
					System.out.println("Error deleting account!");  				// Just to debug
					return;
				}
			} catch (SQLException e1) {
				System.out.println("Error! Please, check your conection");
				return;
			}
			
			User.setLogedIn(false);
			User.setGroups(null);
			User.setLists(null);
			User.setAccount(null);
			
			root = FXMLLoader.load(getClass().getResource("LoginScene.fxml"));
			App.loadScene(root);
			
			System.out.println("Your account was successfully deleted!");  	    // Just to debug
		}
		
    }

    @FXML
    void save(ActionEvent event) {
    	
    	removeErrorNotifications();
    	
    	String newFirstName = newFirstNameField.getText();
    	String newLastName	= newLastNameField.getText();
    	String newName = newFirstName + " " + newLastName;
    	String newEmail = newEmailField.getText();
    	String oldPassword = oldPasswordField.getText();
    	String newPassword = newPasswordField.getText();
    	String confirmNewPassword = confirmNewPasswordField.getText();
    	
    	if (newFirstName.isBlank()) {
    		showNotification("The account needs a first name!");
    		newFirstNameField.getStyleClass().add("error");
    		return;
    	}
    	else if (newLastName.isBlank()) {
    		showNotification("The account needs a last name!");
    		newLastNameField.getStyleClass().add("error");
    		return;
    	}
    	else if (newEmail.isBlank()) {
    		showNotification("The account needs a email!");
    		newEmailField.getStyleClass().add("error");
    		return;
    	}
    	else if ((!newPassword.isBlank() || !confirmNewPassword.isBlank()) && (oldPassword.isBlank())) {
    		showNotification("You need to type your password!");
    		oldPasswordField.getStyleClass().add("error");
    		newPasswordField.getStyleClass().add("error");
    		confirmNewPasswordField.getStyleClass().add("error");
    		return;
    	}
    	
    	if (!newName.equals(account.getName())) {
    		String notif;
    		if ((notif = Account.checkValidName(newFirstName)) != null) {
    			showNotification(notif);
    			return;
    		}
    		else if ((notif = Account.checkValidEmail(newLastName)) != null) {
    			showNotification(notif);
    			return;
    		}
    		
	    	try {
				if (!AccountsDatabase.changeName(account.getEmail(), newName)) {
					showNotification("Invalid inputs");
					return;
				}
			} catch (SQLException e) {
				showNotification("Please, verify your internet connection.");
				return;
				//e.printStackTrace();
			}
	    	account.setName(newName);
    	}
    	
    	if (!newEmail.equals(account.getEmail())) {
    		String notif = Account.checkValidEmail(newEmail);
    		
    		if (notif != null) {
    			showNotification(notif);
    			return;
    		}
    		
    		try {
    			if (!AccountsDatabase.changeEmail(account.getEmail(), newEmail)) {
    				showNotification("Invalid Email");
    				return;
    			}
    			
    			account.setEmail(newEmail);
    		}
    		catch (SQLException e) {
    			showNotification("Please, verify your internet connection.");
    			return;
    		}
    	}
    	
    	//TODO
    	
    	
    	
    }
    
	private void showNotification(String notification) {
		
		notificationLabel.setText(notification);
		notificationLabel.setTextFill(Color.RED);
		notificationLabel.setVisible(true);
		
	}
    
	private void removeErrorNotifications() {
		
		newFirstNameField.getStyleClass().removeAll(Collections.singleton("error")); 
		newLastNameField.getStyleClass().removeAll(Collections.singleton("error"));
		newEmailField.getStyleClass().removeAll(Collections.singleton("error"));
		oldPasswordField.getStyleClass().removeAll(Collections.singleton("error"));
		newPasswordField.getStyleClass().removeAll(Collections.singleton("error"));
		confirmNewPasswordField.getStyleClass().removeAll(Collections.singleton("error"));
		
		notificationLabel.setVisible(false);
		
	}
}