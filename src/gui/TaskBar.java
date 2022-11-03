package gui;

import javafx.event.ActionEvent;
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
		taskCheckBox.setOnAction(event -> {
            taskCheck(event);
        });
		taskBox.getChildren().add(taskCheckBox);
		
		taskButton = new Button(taskName);
		taskButton.setMnemonicParsing(false);
		taskButton.setAlignment(Pos.TOP_LEFT);
		taskButton.setPrefHeight(26.0);
		taskButton.setPrefWidth(495.0);
		taskButton.setOnAction(event -> {
            taskEdit(event);
        });
		taskBox.getChildren().add(taskButton);

	}
	
	public void taskEdit(ActionEvent e) {
		
		String text = e.getSource().toString();
		String value = text.substring(text.indexOf("'")+1, 
	               text.indexOf("'", text.indexOf("'")+1));
		System.out.println(value);
		
	}
	
	public void taskCheck(ActionEvent e) {
		
		/*String text = e.getSource().toString();
		System.out.println(text);
		String value = text.substring(text.indexOf("'")+1, 
	               text.indexOf("'", text.indexOf("'")+1));
		System.out.println(value);*/
		
		
	}
	
	public Button getButton() {												// maybe useless
		return taskButton;
	}
	
	public CheckBox getCheckBox() {											// maybe useless
		return taskCheckBox;
	}
	
	public HBox getHBox() {
		return taskBox;
	}
}
