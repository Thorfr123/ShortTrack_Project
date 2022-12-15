package com.psw.shortTrack.gui_elements;

import com.psw.shortTrack.data.Notification;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;

public class SimpleNotificationBox extends HBox {
	
	private Button okButton;
	private Notification notification;
	
	public SimpleNotificationBox(Notification notification) {
		
		super();
		
		this.notification = notification;
     
		setSpacing(10.0);
		
		Label notificationText = new Label(notification.getMessage());
		notificationText.setFont(Font.font(14.0));
		getChildren().add(notificationText);
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
		
		buttons.getChildren().add(okButton);
		getChildren().add(buttons);
		
		this.setPadding(new Insets(0, 5, 0, 5));
	}

	public Button getOkButton() {
		return okButton;
	}
	
	public Notification getNotification() {
		return notification;
	}

	
}
