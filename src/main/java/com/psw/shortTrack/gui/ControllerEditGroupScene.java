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
import javafx.scene.paint.Color;
import javafx.scene.control.Alert.AlertType;

public class ControllerEditGroupScene {
	
	@FXML
	private TextField groupNameField;
	@FXML
	private TextField memberTextField;
	@FXML
	private ListView<String> memberList;
	@FXML
	private Label notificationLabel;
	
	private Group group;
	private ArrayList<Account> newMembers = new ArrayList<Account>(0);
	
	public void initData(Group group) {

		this.group = group;
		
		for(Account a : group.getMemberAccounts()) {
			newMembers.add(a);
			memberList.getItems().add(a.getName() + " (" + a.getEmail() + ")");
		}
		
		groupNameField.setText(group.getName());
		
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
		//ArrayList<String> newMembers = group.getMembers();
		
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
		
		for(String s : group.getMemberEmails()) {
			if(s.equals(newMember)) {
				showNotification("This member already belongs to this group!");
				memberTextField.getStyleClass().add("error");
				return;
			}
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
		
		memberList.getItems().add(newMemberAccount.getName() + " (" + newMemberAccount.getEmail() + ")");
		newMembers.add(newMemberAccount);
		
	}
	
	public void removeMember(ActionEvent e) throws IOException {
		
		removeErrorNotifications();
		String memberToRemove = memberList.getSelectionModel().getSelectedItem();
		
		if(memberToRemove == null)
			return;
		
		memberList.getItems().remove(memberToRemove);
		memberToRemove = memberToRemove.substring(memberToRemove.indexOf("(")+1, 
												  memberToRemove.indexOf(")"));
		
		for(Account a : newMembers) {
			String oldmember = a.getEmail();
			if(oldmember.equals(memberToRemove)) {
				newMembers.remove(a);
				break;
			}
		}
		
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
