package com.psw.shortTrack.gui;

import com.psw.shortTrack.data.Notification;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class SimpleNotificationBox extends HBox {
	
	private Button okButton;
	private Notification notification;
	
	public SimpleNotificationBox(Notification notification) {
		
		super();
		
		this.notification = notification;
     
		setSpacing(10.0);
		
		// TODO: change label text
		Label notificationText = new Label("Text");
		HBox.setHgrow(notificationText,Priority.ALWAYS);
		notificationText.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
		
		HBox buttons = new HBox();
		buttons.setAlignment(Pos.CENTER);
		buttons.setPrefSize(150.0, 26.0);
		buttons.setSpacing(10.0);
		
		okButton = new Button("Ok");
		okButton.setMnemonicParsing(false);
		okButton.setPrefSize(60.0, 26.0);
		okButton.setFocusTraversable(false);
		
		this.setPadding(new Insets(0, 5, 0, 5));
	}

	public Button getOkButton() {
		return okButton;
	}
	
	public Notification getNotification() {
		return notification;
	}

	
}
