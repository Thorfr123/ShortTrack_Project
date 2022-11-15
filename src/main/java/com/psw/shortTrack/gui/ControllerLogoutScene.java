package com.psw.shortTrack.gui;

import java.io.IOException;
import java.sql.SQLException;

import com.psw.shortTrack.database.AccountsDatabase;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ControllerLogoutScene {
	
	@FXML 
	private Label printNameLabel;
	@FXML 
	private Label printEmailLabel;
	@FXML
	private AnchorPane scenePane;
	@FXML 
	private VBox logoutBox;
	
	private Stage stage;
	private Scene scene;
	private Parent root;
	
	public void logout(ActionEvent e) throws IOException {
		
		root = FXMLLoader.load(getClass().getResource("LoginScene.fxml"));
		stage = (Stage)((Node)e.getSource()).getScene().getWindow();
		loadScene();
		
	}
	
	public void delete(ActionEvent e) throws IOException {
		
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Delete account");
		alert.setHeaderText("You're about to delete your account and all your data!");
		alert.setContentText("Are you sure you really want to delete your account?");

		if(alert.showAndWait().get() == ButtonType.OK){
			
			try {
				if (!AccountsDatabase.deleteAccount(printEmailLabel.getText())) {
					System.out.println("Error deleting account!");  				// Just to debug
					return;
				}
			} catch (SQLException e1) {
				//Temporario
				System.out.println("Error! Please, check your conection");
				return;
			}	
			
			root = FXMLLoader.load(getClass().getResource("LoginScene.fxml"));
			stage = (Stage)((Node)e.getSource()).getScene().getWindow();
			loadScene();
			
			System.out.println("Your account was successfully deleted!");  	    // Just to debug
		}
		
	}
	
	public void displayName(String name) {
		printNameLabel.setText(name);
	}
	
	public void displayEmail(String email) {
		printEmailLabel.setText(email);
	}
	
	public void loadScene() {
		
		scene = new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());
		String css = this.getClass().getResource("application.css").toExternalForm();
		scene.getStylesheets().add(css);
		stage.setScene(scene);
		stage.show();
		
	}
}