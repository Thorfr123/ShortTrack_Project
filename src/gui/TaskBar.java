package gui;

import data.Task;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

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
		taskBox.setSpacing(10.0);
		
		taskCheckBox = new CheckBox();
		taskCheckBox.setMnemonicParsing(false);
		taskCheckBox.setFont(Font.font(14.0));
		
		VBox taskBox2 = new VBox();
		Label label1 = new Label(taskName);
		Label label2 = new Label(taskDeadlineDate);
		label1.setFont(Font.font(14.0));
		label2.getStyleClass().add("deadlineDate");
		taskBox2.getChildren().add(label1);
		taskBox2.getChildren().add(label2);	
		
		taskButton = new Button();
		taskButton.setMnemonicParsing(false);
		taskButton.setAlignment(Pos.TOP_LEFT);
		taskButton.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
		taskButton.setGraphic(taskBox2);
		taskButton.setFont(Font.font(14.0));
		
		HBox.setHgrow(taskButton,Priority.ALWAYS);
		
		taskBox.getChildren().add(taskCheckBox);
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
