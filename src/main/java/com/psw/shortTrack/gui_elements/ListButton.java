package com.psw.shortTrack.gui_elements;

import com.psw.shortTrack.data.List;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.text.Font;

public class ListButton extends Button{
	
	private List list;
	
	public ListButton(List list) {
		
		super();
		
		setText(list.getName());
		setFont(Font.font(14.0));
		setMnemonicParsing(false);
		setAlignment(Pos.TOP_LEFT);
		setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
		setFocusTraversable(false);
		
		this.list = list;
		
	}
	
	public List getList() {
		return list;
	}
}

