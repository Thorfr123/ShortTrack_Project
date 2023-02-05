package com.psw.shortTrack.gui_elements;

import com.psw.shortTrack.data.Account;
import com.psw.shortTrack.data.Group;
import com.psw.shortTrack.data.GroupTask;
import com.psw.shortTrack.data.User;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class GroupTaskBar extends HBox{
	
	private Button taskButton;
	private CheckBox taskCheckBox;
	private GroupTask task;
	
	public GroupTaskBar(GroupTask task, Boolean searchMode) {
		
		super();
		
		this.task = task;
		Group group = User.getGroup(task.getParentID());
		
		String taskName = task.getName();
		String taskDeadlineDate = "";
		Account assignedTo = task.getAssignedToAccount();
		
		if(task.getDeadlineDate() != null)
			taskDeadlineDate = task.getDeadlineDate().toString();
		                    
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
		label2.setMinSize(100.0,Double.MIN_VALUE);
		
		Label label3 = new Label();
		if(assignedTo != null) {
			label3.setText("Assign to: " + assignedTo.toString());
			label3.getStyleClass().add("taskFooter");
			taskBox3.getChildren().add(label3);
			label3.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
		}
		
		if(searchMode) {
			HBox.setHgrow(label3,Priority.ALWAYS);
			Label label4 = new Label("Group: " + group.getName());
			label4.getStyleClass().add("taskFooter");
			taskBox3.getChildren().add(label4);
			label4.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
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
	
	public GroupTaskBar(GroupTask task) {
		this(task,false);
	}
	
	public Button getButton() {
		return taskButton;
	}
	
	public CheckBox getCheckBox() {
		return taskCheckBox;
	}
	
	public GroupTask getTask() {
		return task;
	}

}

