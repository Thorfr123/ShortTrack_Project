package com.psw.shortTrack.gui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import com.psw.shortTrack.data.List;
import com.psw.shortTrack.data.Task;
import com.psw.shortTrack.data.User;
import com.psw.shortTrack.fileIO.FileIO;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;

public class App extends Application {
	
	private static String css;
	
	@Override
	public void start(Stage primaryStage) {
		
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
		Task.idCount = idCounter[0];
		List.idCount = idCounter[1];
		
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			String icon_str = classLoader.getResource("icon.png").toExternalForm();
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
				try {
					FileIO.writePersonalListsToFile(User.getLists());
					FileIO.writeIdCountersToFile(Task.idCount, List.idCount);
				} catch (IOException e) {
					System.out.println("Erro a tentar escrever o ficheiro de backup local!");
				}
				
			});
			
			primaryStage.show();
			
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
	
	public static void loadScene(Parent root, Stage stage) {
		
		Scene scene = new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());
		scene.getStylesheets().add(css);
		stage.setScene(scene);
		
	}
	
	public static Parent getMainScene() throws IOException {
		
		Parent root;
		
		if(User.isLogedIn())
			root = FXMLLoader.load(App.class.getResource("LogoutScene.fxml"));
		else
			root = FXMLLoader.load(App.class.getResource("LoginScene.fxml"));
		
		return root;
	}
}
