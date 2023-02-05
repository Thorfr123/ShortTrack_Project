package com.psw.shortTrack.gui_elements;

import com.psw.shortTrack.data.Group;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class GroupButton extends Button{
	
	private Group group;
	
	public GroupButton(Group group) {
		
		super();
		
		this.group = group;
		
		VBox groupBox = new VBox();
		Label label1 = new Label(group.getName());
		Label label2 = new Label("Manager: " + group.getManagerAccount().toString());
		label1.setFont(Font.font(14.0));
		label2.getStyleClass().add("manager");

		groupBox.getChildren().add(label1);
		groupBox.getChildren().add(label2);
		
		setMnemonicParsing(false);
		setAlignment(Pos.TOP_LEFT);
		setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
		setGraphic(groupBox);
		setFont(Font.font(14.0));
		
		HBox.setHgrow(this,Priority.ALWAYS);
		
		setFocusTraversable(false);
	}
	
	public Group getGroup() {
		return group;
	}
}

