package gui;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ControllerLogoutScene {
	
	@FXML 
	Label printNameLabel;
	@FXML 
	Label printEmailLabel;
	
	private Stage stage;
	private Scene scene;
	private Parent root;
	
	public void logout(ActionEvent e) throws IOException {
		
		root = FXMLLoader.load(getClass().getResource("LoginScene.fxml"));
		stage = (Stage)((Node)e.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
		
	}
	
	public void delete(ActionEvent e) {
		System.out.println("Delete");
	}
	
	public void displayName(String name) {
		printNameLabel.setText(name);
	}
	
	public void displayEmail(String email) {
		printEmailLabel.setText(email);
	}
}