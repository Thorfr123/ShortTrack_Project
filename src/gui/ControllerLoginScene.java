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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ControllerLoginScene {
	
	@FXML 
	private TextField usernameField;
	@FXML 
	private PasswordField passwordField;
	@FXML
	private Button loginButton;
	@FXML
	private Button signUpButton;
	@FXML
	private Label usernameLabel;
	@FXML
	private Label passwordLabel;
	@FXML 
	private Label notificationLabel;
	@FXML 
	private VBox loginBox;
	
	private Stage stage;
	private Scene scene;
	private Parent root;

	public void login(ActionEvent e) throws IOException {
		
		String username = usernameField.getText();
		String password = passwordField.getText();
		
		if(!AccountsDatabase.checkLogin(username, password)) {
			
			if(notificationLabel == null) {
				notificationLabel = new Label("Invalid username or password!");
				notificationLabel.setTextFill(Color.RED);
				loginBox.getChildren().add(notificationLabel);
			}
			
			System.out.println("Invalid username or password!");
			return;
		}
		
		loginBox.getChildren().remove(notificationLabel);
		
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
