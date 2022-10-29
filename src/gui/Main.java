package gui;

import data.Account;
import database.AccountsDatabase;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;

public class Main extends Application {
	
	private AccountsDatabase dataBase;
	
	@Override
	public void start(Stage primaryStage) {
		
		dataBase = new AccountsDatabase();
		Account a = new Account("Valid","1234","Jorge Ribeiro","email1@gmail.com");
		dataBase.addAccount(a);
		
		try {
			Image icon = new Image("/teste_icon.png");
			primaryStage.getIcons().add(icon);
			primaryStage.setMinWidth(820.0);
			primaryStage.setMinHeight(380.0);
			primaryStage.setMaxWidth(820.0);
			
			
			Parent root = FXMLLoader.load(getClass().getResource("LoginScene.fxml"));
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.centerOnScreen();
			primaryStage.show();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
