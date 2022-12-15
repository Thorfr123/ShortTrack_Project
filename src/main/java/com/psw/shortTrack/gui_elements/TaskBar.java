package com.psw.shortTrack.gui_elements;

import com.psw.shortTrack.data.List;
import com.psw.shortTrack.data.Task;
import com.psw.shortTrack.data.User;

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
	
	public TaskBar(Task task, Boolean searchMode) {
		
		super();
		
		this.task = task;
		
		String taskName = task.getName();
		String taskDeadlineDate = "";
		
		if (task.getDeadlineDate() != null) {
			taskDeadlineDate = task.getDeadlineDate().toString();
		}
		                    
		setSpacing(10.0);
		setAlignment(Pos.CENTER_LEFT);
		
		taskCheckBox = new CheckBox();
		taskCheckBox.setMnemonicParsing(false);
		taskCheckBox.setFont(Font.font(14.0));
		taskCheckBox.setFocusTraversable(false);
		
		VBox taskBox2 = new VBox();
		HBox taskBox3 = new HBox();
		Label label1 = new Label(taskName);
		Label label2 = new Label(taskDeadlineDate);
		label1.setFont(Font.font(14.0));
		label2.getStyleClass().add("taskFooter");
		taskBox3.getChildren().add(label2);
		HBox.setHgrow(label2,Priority.ALWAYS);
		label2.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
		
		if(searchMode) {
			List list = User.getList(task.getParentID());		
			Label label3 = new Label("List: " + list.getName());
			label3.getStyleClass().add("taskFooter");
			taskBox3.getChildren().add(label3);
			label3.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
		}

		taskBox2.getChildren().add(label1);
		taskBox2.getChildren().add(taskBox3);
		
		taskButton = new Button();
		taskButton.setMnemonicParsing(false);
		taskButton.setAlignment(Pos.TOP_LEFT);
		taskButton.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
		taskButton.setGraphic(taskBox2);
		taskButton.setFont(Font.font(14.0));
		taskButton.setFocusTraversable(false);
		
		HBox.setHgrow(taskButton,Priority.ALWAYS);
		
		getChildren().add(taskCheckBox);
		getChildren().add(taskButton);
		
	}
	
	public TaskBar(Task task) {
		this(task,false);
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

}
