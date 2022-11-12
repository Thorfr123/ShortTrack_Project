package gui;

import data.List;
import data.Task;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class TaskBar extends HBox{
	
	private Button taskButton;
	private CheckBox taskCheckBox;
	private Task task;
	private List list;
	
	public TaskBar(Task task,List list) {
		
		super();
		
		this.task = task;
		this.list = list;
		
		String taskName = task.getName();
		String taskDeadlineDate;
		
		if(task.getDeadlineDate() == null)
			taskDeadlineDate = "";
		else
			taskDeadlineDate = task.getDeadlineDate().toString();
		                    
		setSpacing(10.0);
		setAlignment(Pos.CENTER_LEFT);
		
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
		
		getChildren().add(taskCheckBox);
		getChildren().add(taskButton);
		
	}
	
	public Button getButton() {
		return taskButton;
	}
	
	public CheckBox getCheckBox() {
		return taskCheckBox;
	}
	
	public Task getTask() {
		return task;
	}
	
	public List getList() {
		return list;
	}
}
