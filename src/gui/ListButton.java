package gui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;

public class ListButton {
	
	private Button listButton;
	
	public ListButton(String listName) {
		
		listButton = new Button(listName);
		listButton.setMnemonicParsing(false);
		listButton.setAlignment(Pos.TOP_LEFT);
		listButton.setPrefHeight(26.0);
		listButton.setPrefWidth(165.0);
	}
	
	public Button getButton() {
		return listButton;
	}
}
