package com.psw.shortTrack.data;

import java.time.LocalDate;

public class PersonalTask extends Task {

	private static final long serialVersionUID = -8844284273122638778L;
	
	//private int listId;
	
	public PersonalTask(String name, int id, String description, LocalDate createdDate, LocalDate deadline, Boolean completed, int listID) {
		super(name, id, description, createdDate, deadline, completed, listID);
		//this.listId = listID;
	}
	
	public PersonalTask(String name, int listID) {
		super(name, listID);
	}

	/*public int getListId() {
		return listId;
	}

	public void setListId(int listId) {
		this.listId = listId;
	}*/

}
