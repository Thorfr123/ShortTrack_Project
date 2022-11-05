package gui;

import java.io.IOException;
import java.util.ArrayList;

import data.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class ControllerEditListScene {
	
	@FXML
	private TextField listNameField;
	@FXML
	private Label notificationLabel;
	
	private ArrayList<List> arrayList;
	private List list;
	
	private Stage stage;
	private Scene scene;
	private Parent root;
	
	public void initData(List list, ArrayList<List> arrayList) {
		this.arrayList = arrayList;
		this.list = list;
		
		listNameField.setText(list.getName());

    }
	
	public void delete(ActionEvent e) throws IOException {
		
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Delete List");
		alert.setHeaderText("You're about to delete this list and lose this data!");
		alert.setContentText("Are you sure you really want to delete the list?");

		if(alert.showAndWait().get() == ButtonType.OK){
			
			arrayList.remove(list);
			
			root = FXMLLoader.load(getClass().getResource("LoginScene.fxml"));
			stage = (Stage)((Node)e.getSource()).getScene().getWindow();
			loadScene();
			
		}
		
	}
	
	public void save(ActionEvent e) throws IOException {
		
		String newListName = listNameField.getText();
		if(newListName.isBlank()) {
			showNotification("The List needs a name!");
			return;
		}
		
		if (!newListName.equals(list.getName())) {
			for (List l : arrayList) {
				if (l.getName().equals(newListName)) {
					showNotification("Already exist a task with that name!");
					return;
				}
			}
		}
				
		list.setName(newListName);
		
		root = FXMLLoader.load(getClass().getResource("LoginScene.fxml"));
		stage = (Stage)((Node)e.getSource()).getScene().getWindow();
		loadScene();
		
	}
	
	public void cancel(ActionEvent e) throws IOException {
		
		root = FXMLLoader.load(getClass().getResource("LoginScene.fxml"));
		stage = (Stage)((Node)e.getSource()).getScene().getWindow();
		loadScene();
		
	}
	
	
	public void showNotification(String notification) {
		
		notificationLabel.setText(notification);
		notificationLabel.setTextFill(Color.RED);
		notificationLabel.setVisible(true);
		
	}
	
	public void loadScene() {
		
		scene = new Scene(root);
		String css = this.getClass().getResource("application.css").toExternalForm();
		scene.getStylesheets().add(css);
		stage.setScene(scene);
		stage.setMinWidth(820.0);
		stage.show();
		
	}
	
}
