package com.psw.shortTrack.gui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class App extends Application {
	
	private static String css;
	private static Stage stage;
	
	private static Instant time1 = Instant.now();
	
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
	
	/**
	 * Calculates the actual task id counter
	 * 
	 * @return Task id counter
	 */
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
	
	/**
	 * Calculates the actual list id counter
	 * 
	 * @return List id counter
	 */
	private static int calculateListIdCounter() {
		
		int idCount = 1;
		for (List lst : User.getLists()) {
			if (lst.getID() >= idCount) {
				idCount = lst.getID() + 1;
			}
		}
		return idCount;
		
	}
	
	/**
	 * Reads all the data stored in the local files. It restores the offline personal lists and tasks and the id counters.
	 */
	public static void readLocalFiles() {
		
		try {
			
			User.setLists(FileIO.readPersonalListsFromFile());
			
		} catch (FileNotFoundException fnfe) {
			System.out.println("Couldn't find local backup file");
		} catch (ClassNotFoundException | IOException e) {
			System.out.println("Erro a tentar ler o ficheiro de backup local!");
		}
		
		int [] idCounter = {-1, -1};
		try {
			
			idCounter = FileIO.readIdCountersFromFile();
			if (idCounter[0] < 1 || idCounter[1] < 1)
				throw new IOException();
			
		} catch (IOException | ClassNotFoundException e){
			if (idCounter[0] < 1) {
				idCounter[0] = calculateTaskIdCounter();
			}
			if (idCounter[1] < 1) {
				idCounter[1] = calculateListIdCounter();
			}
		}
		PersonalTask.idCount = idCounter[0];
		List.idCount = idCounter[1];
		
	}
	
	/**
	 * Writes the offline data to local files. It stores the offline personal lists and tasks and id counters
	 */
	public static void writeLocalFiles() {
		
		try {
			
			FileIO.writePersonalListsToFile(User.getLists());
			FileIO.writeIdCountersToFile(PersonalTask.idCount, List.idCount);
			
		} catch (IOException e) {
			System.out.println("Erro a tentar escrever o ficheiro de backup local!");
		}
		
	}
	
	/**
	 * Loads a new scene with the fxml name
	 */
	public static FXMLLoader loadScene(String fxml) {
		
		try {
			
			FXMLLoader loader = new FXMLLoader(App.class.getResource(fxml));
			Parent root = loader.load();
			
			Scene scene = new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());
			scene.getStylesheets().add(css);
			
			// Allow the user to refresh the logout scene every 10 seconds
			if(fxml.equals("LogoutScene.fxml")) {
				scene.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
			        if (event.getCode() == KeyCode.F5) {
			        	Instant time2 = Instant.now();
			        	Duration timeElapsed = Duration.between(time1, time2);
			        	if(timeElapsed.toMillis() > 10000) {
				        	ControllerLogoutScene controller = loader.getController();
				        	
				        	controller.initialize();
							
			        	}
			        	time1 = time2;
			        }
				});
			}
			// Allow the user to refresh the notification scene every 10 seconds
			else if(fxml.equals("NotificationScene.fxml")) {
				scene.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
			        if (event.getCode() == KeyCode.F5) {
			        	Instant time2 = Instant.now();
			        	Duration timeElapsed = Duration.between(time1, time2);
			        	if(timeElapsed.toMillis() > 10000) {
				        	ControllerNotificationScene controller = loader.getController();
				        	controller.refreshPage();
			        	}
			        	time1 = time2;
			        }
				});
			}
			
			if (fxml.equals("LogoutScene.fxml") && !User.isLogedIn()) {
				return null;
			}
			
			stage.setScene(scene);
			stage.show();

			Platform.runLater(() -> scene.getRoot().requestFocus());

			return loader;
		}
		catch (IOException exception) {
			exception.printStackTrace();
			return null;
		}

	}
	
	/**
	 * Loads the main window scene
	 */
	public static void loadMainScene() {

		String fxml = User.isLogedIn() ? "LogoutScene.fxml" : "LoginScene.fxml";
		loadScene(fxml);
		
	}
	
	/**
	 * Shows the connection error alert
	 */
	public static void connectionErrorMessage() {
		
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Connection Error");
		alert.setHeaderText("The connection to the database was lost!");
		alert.setContentText("Error! Please, check your connection");
		alert.showAndWait();
		
	}
	
	/**
	 * Shows the account deleted error alert
	 */
	public static void accountDeletedMessage() {
		
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Account deleted");
		alert.setHeaderText("It seems your account was deleted in another session!");
		alert.setContentText("You will be logged out of your account!");
		alert.showAndWait();
		
	}
	
	/**
	 * Shows the group deleted error alert
	 */
	public static void groupDeletedMessage() {
		
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Group deleted");
		alert.setHeaderText("It seems this group was deleted in another session!");
		alert.setContentText("You will return to the main window!");
		alert.showAndWait();
		
	}
	
	/**
	 * Shows the task deleted error alert
	 */
	public static void taskDeletedMessage() {
		
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error editing task");
		alert.setHeaderText("It seems this task no longer exists!");
		alert.setContentText("This can be caused because the manager deleted this task!");
		alert.showAndWait();
		
	}
	
	/**
	 * Shows the user has no privileges error alert
	 */
	public static void taskNoPrivilegesMessage() {
		
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error editing task");
		alert.setHeaderText("It seems you haven't enough privileges to edit this task!");
		alert.setContentText("This can be caused because this task is no longer assigned to you");
		alert.showAndWait();
		
	}
	
}
