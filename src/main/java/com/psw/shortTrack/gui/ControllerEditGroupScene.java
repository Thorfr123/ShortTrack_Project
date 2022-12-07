package com.psw.shortTrack.gui;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;

import com.psw.shortTrack.data.Account;
import com.psw.shortTrack.data.Group;
import com.psw.shortTrack.data.Notification;
import com.psw.shortTrack.data.User;
import com.psw.shortTrack.data.Notification.NotificationType;
import com.psw.shortTrack.database.AccountsDatabase;
import com.psw.shortTrack.database.GroupsDatabase;
import com.psw.shortTrack.database.NotificationDatabase;

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
	
	public void initData(Group group) {

		this.group = group;
		
		groupNameField.setText(group.getName());
		
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
				App.connectionErrorMessage();
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
			showNotification("The Group needs a name!", true);
			groupNameField.getStyleClass().add("error");
			return;
		}
		else if (newGroupName.equals(group.getName())) {
			App.loadMainScene();
		}
		
		Group g = User.checkGroupName(newGroupName);
		if ((g != null) && (g != group)) {
			showNotification("Already exist a group with that name!", true);
			return;
		}
		
		if(User.isLogedIn()) {
			try {
				
				GroupsDatabase.changeName(group.getID(),newGroupName);

			} catch (SQLException exception) {
				App.connectionErrorMessage();
				return;
			}
		}

		group.setName(newGroupName);
		
		App.loadMainScene();
		
	}
	
	public void cancel(ActionEvent e) throws IOException {
		
		removeErrorNotifications();
		
		// Cancel the complete task creation
		if(group.getName().isBlank()) {
			try {
				GroupsDatabase.deleteGroup(group.getID());
			} catch (SQLException exception) {
				App.connectionErrorMessage();
				return;
			}
			User.getGroups().remove(group);
			group = null;
		}
		
		App.loadMainScene();
		
	}
	
	public void leave(ActionEvent e) throws IOException {
		
		removeErrorNotifications();
		try {
			
			Notification leave = new Notification(NotificationType.leftGroup, User.getAccount(), group.getManagerAccount(), group);
			NotificationDatabase.createNotification(leave);
			GroupsDatabase.removeMember(group.getID(), User.getAccount());
			
		} catch (SQLException exception) {
			App.connectionErrorMessage();
			return;
		}
		
		App.loadMainScene();
		
	}
	
	public void addMember(ActionEvent e) throws IOException {
		
		removeErrorNotifications();
		String newMember = memberTextField.getText();
		
		if(newMember.isBlank())
			return;
	
		memberTextField.clear();
		
		String errorNotification;
		if ((errorNotification = Account.checkValidEmail(newMember)) != null) {
			showNotification(errorNotification, true);
			memberTextField.getStyleClass().add("error");
			return;
		}
		else if (newMember.equals(group.getManagerEmail())) {
			showNotification("This account is the manager of this group!", true);
			memberTextField.getStyleClass().add("error");
			return;
		}
		else if (group.getMemberEmails().contains(newMember)) {
			showNotification("This account is already in the group!", true);
			memberTextField.getStyleClass().add("error");
			return;
		}
		
		try {			
			Account newMemberAccount = AccountsDatabase.getAccount(newMember);
			if (newMemberAccount == null) {
				showNotification("There is no account with this email!", true);
				memberTextField.getStyleClass().add("error");
				return;
			}
			
			if (NotificationDatabase.checkInvitation(newMember, group.getID())) {
				showNotification("You have already sent an invitation to " + newMemberAccount.toString() + "!", true);
				memberTextField.getStyleClass().add("error");
				return;
			}
			
			Notification invite = new Notification(NotificationType.invitateToGroup, User.getAccount(), newMemberAccount, group);
			NotificationDatabase.createNotification(invite);
			
			showNotification("Invitation sent to " + newMemberAccount.toString(), false);
			
		} catch (SQLException sqle) {
			App.connectionErrorMessage();
			return;
		}
		
	}
	
	public void removeMember(ActionEvent e) throws IOException {
		
		removeErrorNotifications();
		Account memberToRemove = memberList.getSelectionModel().getSelectedItem();
		
		if(memberToRemove == null)
			return;
		
		try {
			
			GroupsDatabase.removeMember(group.getID(), memberToRemove);
			Notification remove = new Notification(NotificationType.removedFromGroup, User.getAccount(), memberToRemove, group);
			NotificationDatabase.createNotification(remove);
			
		} catch (SQLException sqle) {
			App.connectionErrorMessage();
			return;
		}
		
		//group.removeMember(memberToRemove);
		
		memberList.getItems().remove(memberToRemove);
		
		showNotification("You have successfully removed " + memberToRemove.toString(), false);
		
	}
	
	private void showNotification(String notification, boolean error) {
		
		notificationLabel.setText(notification);
		notificationLabel.setTextFill(error ? Color.RED : Color.GREEN);
		notificationLabel.setVisible(true);		
		
	}
	
	private void removeErrorNotifications() {
		
		groupNameField.getStyleClass().removeAll(Collections.singleton("error")); 
		memberTextField.getStyleClass().removeAll(Collections.singleton("error")); 
		notificationLabel.setVisible(false);
		
	}
	
}
