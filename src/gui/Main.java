package gui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import data.List;
import data.Task;
import data.User;
import fileIO.FileIO;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;

public class Main extends Application {
	
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
		
		try {
			int[] idCounter = FileIO.readIdCountersFromFile();
			
			if (idCounter[0] < 1) {
				// Não existe registo logo é necessário descobrir um de alguma forma
				idCounter[0] = calculateTaskIdCounter();
			}

			if (idCounter[1] < 1) {
				// Não existe registo logo é necessário descobrir um de alguma forma
				idCounter[1] = calculateListIdCounter();
			}
			
			Task.idCount = idCounter[0];
			List.idCount = idCounter[1];
		} catch (IOException | ClassNotFoundException e){
			System.out.println("Erro a tentar ler o ficheiro de idCounters local!");
			Task.idCount = calculateTaskIdCounter();
			List.idCount = calculateListIdCounter();
		}
		
		try {
			Image icon = new Image("/teste_icon.png");
			primaryStage.setTitle("ShortTrack");
			primaryStage.getIcons().add(icon);
			primaryStage.setMinWidth(820.0);
			primaryStage.setMinHeight(540.0);
			
			Parent root = FXMLLoader.load(getClass().getResource("LoginScene.fxml"));
			Scene scene = new Scene(root);
			String css = this.getClass().getResource("application.css").toExternalForm();
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
}
