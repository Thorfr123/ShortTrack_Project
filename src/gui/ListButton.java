package gui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;

public class ListButton {
	
	private Button listButton;
	
	public ListButton(String listName) {
		
		listButton = new Button(listName);
		listButton.setMnemonicParsing(false);
		listButton.setAlignment(Pos.TOP_LEFT);
		listButton.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
		
	}
	
	public Button getButton() {
		return listButton;
	}
}
