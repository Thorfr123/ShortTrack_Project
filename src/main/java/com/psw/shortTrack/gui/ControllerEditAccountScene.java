package com.psw.shortTrack.gui;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;

import com.psw.shortTrack.data.Account;
import com.psw.shortTrack.data.User;
import com.psw.shortTrack.database.AccountsDatabase;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class ControllerEditAccountScene {

    @FXML
    private TextField newFirstNameField;
    @FXML
    private TextField newLastNameField;

    @FXML
    private TextField newEmailField;
    @FXML
    private Button changeEmailButton;
    @FXML
    private HBox currentPasswordBox;
    @FXML
    private HBox emailChangePasswordLabel;
    @FXML
    private HBox mailChangeBox;
    
    @FXML
    private HBox changePasswordBox;
    @FXML
    private Button changePasswordButton;
    @FXML
    private VBox changePasswordFill;
    @FXML
    private VBox confirmNewPasswordBox;
    @FXML
    private VBox newPasswordBox;
    @FXML
    private Label passwordInstructionsLabel;
    @FXML
    private Label changePasswordLabel;
    @FXML
    private HBox savePasswordBox;
    
    // Label "Current Password" - usado no changeEmail e changePassword
    @FXML
    private Label passwordLabel;
    // Field "Current Password" - usado no changeEmail e changePassword
    @FXML
    private PasswordField currentPasswordField;
    @FXML
    private Button saveEmail;
    @FXML
    private Button emailCancel;
    @FXML
    private Button passwordCancel;
    @FXML
    private Label  newPasswordLabel;
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private Label confirmNewPasswordLabel;
    @FXML
    private PasswordField confirmNewPasswordField;
    @FXML
    private Button savePassword;
    
    @FXML
    private Label notificationLabel;
    
    private Account account;
    private String firstName, lastName;
    
    public void initData() {

		account = User.getAccount();
		
		String name = account.getName();
		firstName = name.substring(0, name.indexOf(' '));
		lastName = name.substring(name.indexOf(' ') + 1);
		
		newFirstNameField.setText(firstName);
		newFirstNameField.setFocusTraversable(false);
		
		newLastNameField.setText(lastName);
		newLastNameField.setFocusTraversable(false);
		
		newEmailField.setText(account.getEmail());
		newEmailField.setFocusTraversable(false);
		
	    passwordLabel = createLabel("Current password");
    	
	    currentPasswordField = createPasswordField();
    	
	    saveEmail = createButton("Save");
	    saveEmail.setOnAction(event -> {
	    	saveEmail(event);
		});
	
		emailCancel = createButton("Cancel");
		emailCancel.setOnAction(event -> {
			resetEmailLayout();
	    });
    	
		passwordCancel = createButton("Cancel");
		passwordCancel.setOnAction(event -> {
			resetPasswordLayout();
	    });
		
		newPasswordLabel = createLabel("New password");
		
		newPasswordField = createPasswordField();
    	
		confirmNewPasswordLabel = createLabel("Confirm new password");
		
		confirmNewPasswordField = createPasswordField();
    	
		savePassword = createButton("Save");
		savePassword.setOnAction(event -> {
			savePassword();
	    });	
    }
    
	@FXML
    void close(ActionEvent event) throws IOException {
    	
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
				if (!AccountsDatabase.deleteAccount(account.getEmail())) {
					showNotification("Unknown error");
					return;
				}
			} catch (SQLException sqle) {
				showNotification("Error! Please, check your connection");
				return;
			}
			
			User.setLogedIn(false);
			User.setGroups(null);
			User.setLists(null);
			User.setAccount(null);
			
			App.readLocalFiles();
			App.loadMainScene();
		}
    }

    @FXML
    void changeName(ActionEvent event) {
    	    	
    	removeErrorNotifications();
    	
    	String newFirstName = newFirstNameField.getText();
    	String newLastName = newLastNameField.getText();
    	String notif = null;
    	
    	if (newFirstName.equals(firstName) && newLastName.equals(lastName)) {
    		showNotification("Name didn't change!");
    		return;
    	}
    	else if (newFirstName.isBlank()) {
    		showNotification("First name cannot be empty!");
    		newFirstNameField.getStyleClass().add("error");
    		return;
    	}
    	else if (newLastName.isBlank()) {
    		showNotification("Last name cannot be empty!");
    		newLastNameField.getStyleClass().add("error");
    		return;
    	}
    	else if ((notif = Account.checkValidName(newFirstName)) != null) {
    		showNotification(notif);
    		newFirstNameField.getStyleClass().add("error");
    		return;
    	}
    	else if ((notif = Account.checkValidName(newLastName)) != null) {
    		showNotification(notif);
    		newLastNameField.getStyleClass().add("error");
    		return;
    	}
    	
    	String newName = newFirstName + " " + newLastName;
    	
    	try {
    		if (!AccountsDatabase.changeName(account.getEmail(), newName)) {
    			showNotification("Unknown error!");
    			return;
    		}
    	}
    	catch (SQLException sqle) {
    		showNotification("Error! Please, check your connection.");
    		return;
    	}
    	
    	account.setName(newName);
    }
    
    @FXML
    void changeEmail(ActionEvent e) {
    	
    	resetPasswordLayout();
    	
    	mailChangeBox.getChildren().remove(changeEmailButton);
    	mailChangeBox.getChildren().add(saveEmail);
    	
    	emailChangePasswordLabel.getChildren().add(passwordLabel);
    	
    	currentPasswordBox.getChildren().add(currentPasswordField);
    	currentPasswordBox.getChildren().add(emailCancel);
    	
    	newEmailField.setDisable(false);	
    }
    
    
    void saveEmail(ActionEvent e) {
    	
    	removeErrorNotifications();
    	
    	String newEmail = newEmailField.getText();
    	String currentPassword = currentPasswordField.getText();
    	String notif = null;
    	
    	if (newEmail.equals(account.getEmail())) {
    		showNotification("Email didn't change!");
    		return;
    	}
    	else if (newEmail.isBlank()) {
    		newEmailField.getStyleClass().add("error");
    		showNotification("New email cannot be empty!");
    		return;
    	}
    	else if (currentPassword.isBlank()) {
    		currentPasswordField.getStyleClass().add("error");
    		showNotification("You need to insert your current password!");
    		return;
    	}
    	else if ((notif = Account.checkValidEmail(newEmail)) != null) {
    		showNotification(notif);
    		newEmailField.getStyleClass().add("error");
    		return;
    	}
    	
    	try {
			if (AccountsDatabase.checkLogin(account.getEmail(), currentPassword)) {
				if (AccountsDatabase.checkEmail(newEmail)) {
					if (!AccountsDatabase.changeEmail(account.getEmail(), newEmail)) {
						showNotification("Unknown error!");
						return;
					}
				}
				else {
					showNotification("This email is already in use!");
					newEmailField.getStyleClass().add("error");
					return;
				}
			}
			else {
				showNotification("Wrong password! Please, try again.");
				currentPasswordField.getStyleClass().add("error");
				return;
			}
		} catch (SQLException e1) {
			showNotification("Error! Please, check your connection");
			return;
		}
    	
    	account.setEmail(newEmail);
    	resetEmailLayout();
    	
    }
       
    @FXML
    void changePassword(ActionEvent event) {
    	
    	resetEmailLayout();
    	
    	changePasswordLabel.setText("Current password");
    	
    	changePasswordBox.getChildren().removeAll(passwordInstructionsLabel, changePasswordFill,changePasswordButton);
    	changePasswordBox.getChildren().add(currentPasswordField);
    	changePasswordBox.getChildren().add(passwordCancel);
    	
    	newPasswordBox.getChildren().add(newPasswordLabel);
    	newPasswordBox.getChildren().add(newPasswordField);
    	
    	confirmNewPasswordBox.getChildren().add(confirmNewPasswordLabel);
    	confirmNewPasswordBox.getChildren().add(confirmNewPasswordField);
    	
    	savePasswordBox.getChildren().add(savePassword);
    }
    
	private void savePassword() {
		
		removeErrorNotifications();
		
		String currentPassword = currentPasswordField.getText();
		String newPassword = newPasswordField.getText();
		String confirmNewPassword = confirmNewPasswordField.getText();
		String notif = null;
		
		if (currentPassword.isBlank()) {
			currentPasswordField.getStyleClass().add("error");
			showNotification("Please, insert your current password!");
			return;
		}
		else if (newPassword.isBlank()) {
			newPasswordField.getStyleClass().add("error");
			showNotification("Please, insert a new password!");
			return;
		}
		else if (confirmNewPassword.isBlank()) {
			confirmNewPasswordField.getStyleClass().add("error");
			showNotification("Please, confirm your new password!");
			return;
		}
		else if (newPassword.equals(currentPassword)) {
			newPasswordField.getStyleClass().add("error");
			showNotification("New password cannot be the same as your current one.");
			return;
		}
		else if ((notif = Account.checkValidPassword(newPassword, confirmNewPassword)) != null) {
			newPasswordField.getStyleClass().add("error");
			confirmNewPasswordField.getStyleClass().add("error");
			showNotification(notif);
			return;
		}
		
		try {
			if (AccountsDatabase.checkLogin(account.getEmail(), currentPassword)) {
				if (!AccountsDatabase.changePassword(account.getEmail(), currentPassword, newPassword)) {
					showNotification("Unknown error!");
					return;
				}			
			}
			else {
				currentPasswordField.getStyleClass().add("error");
				showNotification("Wrong current password! Please, try again.");
				return;
			}
		}
		catch (SQLException sqle) {
			System.out.println(sqle);
			showNotification("Error! Please, check your connection");
			return;
		}
		
		resetPasswordLayout();
	}

    private void removeErrorNotifications() {
		
		newFirstNameField.getStyleClass().removeAll(Collections.singleton("error")); 
		newLastNameField.getStyleClass().removeAll(Collections.singleton("error"));
		newEmailField.getStyleClass().removeAll(Collections.singleton("error"));
		currentPasswordField.getStyleClass().removeAll(Collections.singleton("error"));
		newPasswordField.getStyleClass().removeAll(Collections.singleton("error"));
		confirmNewPasswordField.getStyleClass().removeAll(Collections.singleton("error"));
		
		notificationLabel.setVisible(false);
	}
      
	private void showNotification(String notification) {
		
		notificationLabel.setText(notification);
		notificationLabel.setVisible(true);
	}
	
	private void resetEmailLayout() {
		
		removeErrorNotifications();
		
       	if (!mailChangeBox.getChildren().contains(changeEmailButton))
    		mailChangeBox.getChildren().add(changeEmailButton);
    	
       	if (mailChangeBox.getChildren().contains(saveEmail))
    		mailChangeBox.getChildren().remove(saveEmail);
       	
    	if (emailChangePasswordLabel.getChildren().contains(passwordLabel))
    		emailChangePasswordLabel.getChildren().remove(passwordLabel);
    	
    	if (currentPasswordBox.getChildren().contains(currentPasswordField))
    		currentPasswordBox.getChildren().remove(currentPasswordField);
    	
    	if (currentPasswordBox.getChildren().contains(emailCancel))
    		currentPasswordBox.getChildren().remove(emailCancel);
		
    	newEmailField.setText(account.getEmail());
    	newEmailField.setDisable(true);
    	currentPasswordField.setText("");
	}
	
    private void resetPasswordLayout() {
    	
    	removeErrorNotifications();
    	
    	changePasswordLabel.setText("Change password");
    	
    	if (!changePasswordBox.getChildren().contains(passwordInstructionsLabel))
    		changePasswordBox.getChildren().add(passwordInstructionsLabel);
    	
    	if (!changePasswordBox.getChildren().contains(changePasswordFill))
    		changePasswordBox.getChildren().add(changePasswordFill);
    	
    	if (!changePasswordBox.getChildren().contains(changePasswordButton))
    		changePasswordBox.getChildren().add(changePasswordButton);
    	
    	if (changePasswordBox.getChildren().contains(currentPasswordField))
    		changePasswordBox.getChildren().remove(currentPasswordField);
    	
    	if (changePasswordBox.getChildren().contains(passwordCancel))
    		changePasswordBox.getChildren().remove(passwordCancel);
    	
    	if (newPasswordBox.getChildren().contains(newPasswordLabel))
    		newPasswordBox.getChildren().remove(newPasswordLabel);

    	if (newPasswordBox.getChildren().contains(newPasswordField))
    		newPasswordBox.getChildren().remove(newPasswordField);
    	
    	if (confirmNewPasswordBox.getChildren().contains(confirmNewPasswordLabel))
    		confirmNewPasswordBox.getChildren().remove(confirmNewPasswordLabel);
    	
    	if (confirmNewPasswordBox.getChildren().contains(confirmNewPasswordField))
    		confirmNewPasswordBox.getChildren().remove(confirmNewPasswordField);
    	
    	if (savePasswordBox.getChildren().contains(savePassword))
    		savePasswordBox.getChildren().remove(savePassword);
		
    	currentPasswordField.setText("");
    	newPasswordField.setText("");
    	confirmNewPasswordField.setText("");
    	
	}
    
    private Label createLabel(String text) {
    	Label label = new Label(text);
    	label.setFont(Font.font(18));
    	return label;
    }
    
    private Button createButton(String name) {
    	Button newButton = new Button(name);
	    newButton.setFont(Font.font(14));
	    newButton.setPrefSize(70, 30);
	    newButton.setAlignment(Pos.CENTER);
	    newButton.setFocusTraversable(false);
    	return newButton;
    }
    
    private PasswordField createPasswordField() {
    	PasswordField field = new PasswordField();
		field.setFont(Font.font(14));
		field.setFocusTraversable(false);
    	HBox.setHgrow(field, Priority.ALWAYS);
    	return field;
    }
}
