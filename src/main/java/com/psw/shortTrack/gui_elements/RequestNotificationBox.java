package com.psw.shortTrack.gui_elements;

import com.psw.shortTrack.data.Notification;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;

public class RequestNotificationBox extends HBox {
	
	private Button acceptButton;
	private Button refuseButton;
	private Notification notification;
	
	public RequestNotificationBox(Notification notification) {
		
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
		
		acceptButton = new Button("Accept");
		acceptButton.setMnemonicParsing(false);
		acceptButton.setPrefSize(60.0, 26.0);
		acceptButton.setFocusTraversable(false);
		
		refuseButton = new Button("Decline");
		refuseButton.setMnemonicParsing(false);
		refuseButton.setPrefSize(60.0, 26.0);
		refuseButton.setFocusTraversable(false);
		
		buttons.getChildren().addAll(acceptButton, refuseButton);
		
		getChildren().add(buttons);
		this.setPadding(new Insets(0, 5, 0, 5));
	}

	public Button getAcceptButton() {
		return acceptButton;
	}
	
	public Button getRefuseButton() {
		return refuseButton;
	}
	
	public Notification getNotification() {
		return notification;
	}

}
