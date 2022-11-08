package gui;

import data.Task;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class TaskBar {
	
	private HBox taskBox;
	private Button taskButton;
	private CheckBox taskCheckBox;
	
	
	public TaskBar(Task task) {
		
		String taskName = task.getName();
		String taskDeadlineDate;
		
		if(task.getDeadlineDate() == null)
			taskDeadlineDate = "";
		else
			taskDeadlineDate = task.getDeadlineDate().toString();
		                    
        taskBox = new HBox();
		taskBox.setPrefHeight(38.0);
		taskBox.setPrefWidth(600.0);
		taskBox.setSpacing(10.0);
		
		taskCheckBox = new CheckBox();
		taskCheckBox.setMnemonicParsing(false);
		taskCheckBox.setPrefHeight(26.0);
		taskBox.getChildren().add(taskCheckBox);
		
		VBox taskBox2 = new VBox();
		taskBox2.setPrefHeight(200.0);
		taskBox2.setPrefWidth(100.0);
		
		Label Label1 = new Label(taskName);
		Label Label2 = new Label(taskDeadlineDate);
		Label2.getStyleClass().add("deadlineDate");
		taskBox2.getChildren().add(Label1);
		taskBox2.getChildren().add(Label2);	
		
		taskButton = new Button();
		taskButton.setMnemonicParsing(false);
		taskButton.setAlignment(Pos.TOP_LEFT);
		taskButton.setPrefHeight(26.0);
		taskButton.setPrefWidth(495.0);
		taskButton.setGraphic(taskBox2);
		
		taskBox.getChildren().add(taskButton);
		
	}
	
	public Button getButton() {
		return taskButton;
	}
	
	public CheckBox getCheckBox() {
		return taskCheckBox;
	}
	
	public HBox getHBox() {
		return taskBox;
	}
}
