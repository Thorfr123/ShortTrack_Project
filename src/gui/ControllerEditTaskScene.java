package gui;

import java.io.IOException;
import data.List;
import data.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ControllerEditTaskScene {
	
	@FXML
	private TextField taskNameField;
	@FXML
	private TextArea descriptionField;
	@FXML
	private CheckBox checkButton;
	@FXML 
	private DatePicker dueDateField;
	@FXML
	private Label notificationLabel;
	
	private Task task;
	private List list;
	
	private Stage stage;
	private Scene scene;
	private Parent root;
	
	public void initData(Task task, List list) {
		this.task = task;
		this.list = list;
		
		taskNameField.setText(task.getName());
		descriptionField.setText(task.getDescription());
    }
	
	public void delete(ActionEvent e) throws IOException {
		
		
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Delete Task");
		alert.setHeaderText("You're about to delete this task and lose this data!");
		alert.setContentText("Are you sure you really want to delete the task?");

		if(alert.showAndWait().get() == ButtonType.OK){
			
			list.removeTask(task);
			
			root = FXMLLoader.load(getClass().getResource("LoginScene.fxml"));
			stage = (Stage)((Node)e.getSource()).getScene().getWindow();
			scene = new Scene(root);
			stage.setScene(scene);
			stage.setMinWidth(820.0);
			stage.show();
			
		}
		
	}
	
	public void save(ActionEvent e) throws IOException {			// falta Check e DueDate
		
		String newTaskName = taskNameField.getText();
		if(newTaskName.isBlank()) {
			showNotification("The Task needs a name!");
			return;
		}
			
		if(!newTaskName.equals(task.getName()) && list.checkName(newTaskName)) {
			showNotification("Already exist a task with that name!");
			return;
		}
		
		String newDescription = descriptionField.getText();
		task.setName(newTaskName);
		task.setDescription(newDescription);
		
		root = FXMLLoader.load(getClass().getResource("LoginScene.fxml"));
		stage = (Stage)((Node)e.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.setMinWidth(820.0);
		stage.show();
		
	}
	
	public void showNotification(String notification) {
		
		notificationLabel.setText(notification);
		notificationLabel.setTextFill(Color.RED);
		notificationLabel.setVisible(true);
		
	}
	
}
