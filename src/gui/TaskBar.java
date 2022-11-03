package gui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;

public class TaskBar {
	
	private HBox taskBox;
	private Button taskButton;
	private CheckBox taskCheckBox;
	
	
	public TaskBar(String taskName) {
		
		taskBox = new HBox();
		taskBox.setPrefHeight(26.0);
		taskBox.setPrefWidth(600.0);
		taskBox.setSpacing(10.0);
		
		taskCheckBox = new CheckBox();
		taskCheckBox.setMnemonicParsing(false);
		taskCheckBox.setPrefHeight(26.0);
		taskBox.getChildren().add(taskCheckBox);
		
		taskButton = new Button(taskName);
		taskButton.setMnemonicParsing(false);
		taskButton.setAlignment(Pos.TOP_LEFT);
		taskButton.setPrefHeight(26.0);
		taskButton.setPrefWidth(495.0);
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
