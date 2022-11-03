package gui;

import javafx.event.ActionEvent;
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
		listButton.setOnAction(event -> {
	        listEdit(event);
	    });

	}
	
	public void listEdit(ActionEvent e) {
		
		String text = e.getSource().toString();
		String value = text.substring(text.indexOf("'")+1, 
	               text.indexOf("'", text.indexOf("'")+1));
		System.out.println(value);
		
	}
	
	public Button getButton() {
		return listButton;
	}
}
