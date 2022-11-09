package gui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.text.Font;

public class ListButton {
	
	private Button listButton;
	
	public ListButton(String listName) {
		
		listButton = new Button(listName);
		listButton.setFont(Font.font(14.0));
		listButton.setMnemonicParsing(false);
		listButton.setAlignment(Pos.TOP_LEFT);
		listButton.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
		
	}
	
	public Button getButton() {
		return listButton;
	}
}
