package gui;

import database.*;
import data.*;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ControllerLoginScene {
	
	@FXML 
	TextField usernameField;
	@FXML 
	PasswordField passwordField;
	@FXML
	Button loginButton;
	@FXML
	Button signUpButton;
	@FXML
	Label usernameLabel;
	@FXML
	Label passwordLabel;
	
	private Stage stage;
	private Scene scene;
	private Parent root;

	public void login(ActionEvent e) throws IOException {
		
		String username = usernameField.getText();
		String password = passwordField.getText();
		
		if(!AccountsDatabase.checkLogin(username, password)) {
			System.out.println("Invalid username or password!");
			return;
		}
		
		Account account = AccountsDatabase.getAccount(username, password);
		String name = account.getName();
		String email = account.getEmail();
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("LogoutScene.fxml"));
		root = loader.load();
		ControllerLogoutScene logoutController = loader.getController();
		logoutController.displayName(name);
		logoutController.displayEmail(email);
		
		stage = (Stage)((Node)e.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
		
	}
	
	public void signUp(ActionEvent e) throws IOException {
		
		root = FXMLLoader.load(getClass().getResource("SignUpScene.fxml"));
		stage = (Stage)((Node)e.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.setMinWidth(290.0);
		stage.show();
		
	}
	
}
