package com.psw.shortTrack.gui;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

import com.psw.shortTrack.data.Account;
import com.psw.shortTrack.data.Group;
import com.psw.shortTrack.data.User;
import com.psw.shortTrack.database.AccountsDatabase;
import com.psw.shortTrack.database.GroupsDatabase;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.control.Alert.AlertType;

public class ControllerEditGroupScene {
	
	@FXML
	private TextField groupNameField;
	@FXML
	private TextField memberTextField;
	@FXML
	private ListView<Account> memberList;
	@FXML
	private Label notificationLabel;
	@FXML
	private VBox editGroupBox;
	@FXML
	private HBox addMembersBox;
	@FXML
	private HBox managerButtonsBox;
	@FXML
	private HBox memberButtonsBox;
	
	private Group group;
	private ArrayList<Account> newMembers = new ArrayList<Account>(0);
	
	public void initData(Group group) {

		this.group = group;
		
		groupNameField.setText(group.getName());
		
		newMembers.addAll(group.getMemberAccounts());
		memberList.getItems().addAll(group.getMemberAccounts());
		
		if(!group.getManagerEmail().equals(User.getAccount().getEmail())) {
			groupNameField.setDisable(true);
			groupNameField.setOpacity(1);
			memberList.setDisable(true);
			memberList.setOpacity(1);
			editGroupBox.getChildren().remove(addMembersBox);
			editGroupBox.getChildren().remove(managerButtonsBox);
		}
		else {
			editGroupBox.getChildren().remove(memberButtonsBox);
		}
    }
	
	public void delete(ActionEvent e) throws IOException {
		
		removeErrorNotifications();
		
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Delete Group");
		alert.setHeaderText("You're about to delete this group and lose this data!");
		alert.setContentText("Are you sure you really want to delete the group?");

		if(alert.showAndWait().get() == ButtonType.OK){
			
			try {
				GroupsDatabase.deleteGroup(group.getID());
			} catch (SQLException exception) {
				showNotification("Error! Please, check your connection");
				return;
			}
			
			User.getGroups().remove(group);
			group = null;
			
			App.loadMainScene();
			
		}
		
	}
	
	public void save(ActionEvent e) throws IOException {
		
		removeErrorNotifications();
		
		String newGroupName = groupNameField.getText();
		
		if(newGroupName.isBlank()) {
			showNotification("The Group needs a name!");
			groupNameField.getStyleClass().add("error");
			return;
		}
		
		Group g = User.checkGroupName(newGroupName);
		if ((g != null) && (g != group)) {
			showNotification("Already exist a group with that name!");
			return;
		}
		
		if(User.isLogedIn()) {
			try {
				GroupsDatabase.updateGroup(group.getID(),newGroupName,newMembers);
			} catch (SQLException exception) {
				showNotification("Error! Please, check your connection");
				return;
			}
		}

		group.setName(newGroupName);
		group.setMembers(newMembers);
		
		App.loadMainScene();
		
	}
	
	public void cancel(ActionEvent e) throws IOException {
		
		removeErrorNotifications();
		
		App.loadMainScene();
		
	}
	
	// TODO: Implement this method
	public void leave(ActionEvent e) throws IOException {
		
		removeErrorNotifications();
		
		System.out.println("leave - Not Working!");
		
	}
	
	public void addMember(ActionEvent e) throws IOException {
		
		removeErrorNotifications();
		String newMember = memberTextField.getText();
		
		if(newMember.isBlank())
			return;
	
		memberTextField.clear();
		
		String errorNotification;
		if((errorNotification = Account.checkValidEmail(newMember)) != null) {
			showNotification(errorNotification);
			memberTextField.getStyleClass().add("error");
			return;
		}
		
		if(group.getMemberEmails().contains(newMember)) {
			showNotification("This member already belongs to this group!");
			memberTextField.getStyleClass().add("error");
			return;
		}
		
		Account newMemberAccount;
		try {
			newMemberAccount = AccountsDatabase.getAccount(newMember);
		} catch (SQLException exception) {
			showNotification("Error! Please, check your connection");
			return;
		}
		
		if(newMemberAccount == null) {
			showNotification("There is no account with this email!");
			memberTextField.getStyleClass().add("error");
			return;
		}

		memberList.getItems().add(newMemberAccount);
		newMembers.add(newMemberAccount);
		
	}
	
	public void removeMember(ActionEvent e) throws IOException {
		
		removeErrorNotifications();
		Account memberToRemove = memberList.getSelectionModel().getSelectedItem();
		
		if(memberToRemove == null)
			return;
		
		memberList.getItems().remove(memberToRemove);
		newMembers.remove(memberToRemove);
		
	}
	
	private void showNotification(String notification) {
		
		notificationLabel.setText(notification);
		notificationLabel.setTextFill(Color.RED);
		notificationLabel.setVisible(true);
		
	}
	
	private void removeErrorNotifications() {
		
		groupNameField.getStyleClass().removeAll(Collections.singleton("error")); 
		memberTextField.getStyleClass().removeAll(Collections.singleton("error")); 
		notificationLabel.setVisible(false);
		
	}
	
}
