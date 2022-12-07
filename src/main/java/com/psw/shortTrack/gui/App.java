package com.psw.shortTrack.gui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import com.psw.shortTrack.data.List;
import com.psw.shortTrack.data.PersonalTask;
import com.psw.shortTrack.data.Task;
import com.psw.shortTrack.data.User;
import com.psw.shortTrack.fileIO.FileIO;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;

public class App extends Application {
	
	private static String css;
	private static Stage stage;
	
	@Override
	public void start(Stage primaryStage) {
		
		stage = primaryStage;

		readLocalFiles();
		
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			String icon_str = classLoader.getResource("teste_icon.png").toExternalForm();
			Image icon = new Image(icon_str);
			primaryStage.setTitle("ShortTrack");
			primaryStage.getIcons().add(icon);
			primaryStage.setMinWidth(820.0);
			primaryStage.setMinHeight(540.0);
			
			Parent root = FXMLLoader.load(getClass().getResource("LoginScene.fxml"));
			Scene scene = new Scene(root);
			css = this.getClass().getClassLoader().getResource("application.css").toExternalForm();
			scene.getStylesheets().add(css);

			primaryStage.setScene(scene);
			primaryStage.centerOnScreen();
			
			primaryStage.setOnCloseRequest(event -> {
				if(!User.isLogedIn())
					writeLocalFiles();
			});
			
			primaryStage.show();
			Platform.runLater(() -> scene.getFocusOwner().getParent().requestFocus());
			
		} catch(Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	private static int calculateTaskIdCounter() {
		int idCount = 1;
		for (List lst : User.getLists()) {
			for (Task tsk : lst.getTaskList()) {
				if (tsk.getID() >= idCount) {
					idCount = tsk.getID() + 1;
				}
			}
		}
		return idCount;
	}
	
	private static int calculateListIdCounter() {
		int idCount = 1;
		for (List lst : User.getLists()) {
			if (lst.getID() >= idCount) {
				idCount = lst.getID() + 1;
			}
		}
		return idCount;
	}
	
	public static void readLocalFiles() {
		
		try {
			ArrayList<List> lists = FileIO.readPersonalListsFromFile();
			User.setLists(lists);			
		} catch (FileNotFoundException fnfe) {
			System.out.println("Couldn't find local backup file");
		} catch (ClassNotFoundException | IOException e) {
			System.out.println("Erro a tentar ler o ficheiro de backup local!");
		}
		
		int [] idCounter = {-1, -1};
		try {
			idCounter = FileIO.readIdCountersFromFile();
			if (idCounter[0] < 1) {
				idCounter[0] = calculateTaskIdCounter();
			}
			if (idCounter[1] < 1) {
				idCounter[1] = calculateListIdCounter();
			}
		} catch (IOException | ClassNotFoundException e){
			System.out.println("Erro a tentar ler o ficheiro de idCounters local!");
			idCounter[0] = calculateTaskIdCounter();
			idCounter[1] = calculateListIdCounter();
		}
		PersonalTask.idCount = idCounter[0];
		List.idCount = idCounter[1];
		
	}
	
	public static void writeLocalFiles() {
		
		try {
			FileIO.writePersonalListsToFile(User.getLists());
			FileIO.writeIdCountersToFile(PersonalTask.idCount, List.idCount);
		} catch (IOException e) {
			System.out.println("Erro a tentar escrever o ficheiro de backup local!");
		}
		
	}
	
	public static void connectionErrorMessage() {
		
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Connection Error");
		alert.setHeaderText("The connection to the database was lost!");
		alert.setContentText("Error! Please, check your connection");

		if(alert.showAndWait().get() == ButtonType.OK)
			return;
		
	}
	
	public static void loadScene(Parent root) {
		
		Scene scene = new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());
		scene.getStylesheets().add(css);
		stage.setScene(scene);
		stage.show();
		
		Platform.runLater(() -> scene.getRoot().requestFocus());
	}
	
	
	public static void loadMainScene() throws IOException {
		
		Parent root;
		
		if(User.isLogedIn())
			root = FXMLLoader.load(App.class.getResource("LogoutScene.fxml"));
		else
			root = FXMLLoader.load(App.class.getResource("LoginScene.fxml"));
		
		loadScene(root);
	}
	
}
