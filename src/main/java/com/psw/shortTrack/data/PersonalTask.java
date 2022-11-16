package com.psw.shortTrack.data;

import java.time.LocalDate;

public class PersonalTask extends Task {

	private static final long serialVersionUID = -8844284273122638778L;
	public static int idCount = 1;
	private int listId;
	
	public PersonalTask(String name, int id, String description, LocalDate createdDate, LocalDate deadline, Boolean completed) {
		super(name, id, description, createdDate, deadline, completed);
	}
	
	public PersonalTask(String name, int parentID) {
		super(name, parentID);
		super.setID(idCount++);
	}

	public PersonalTask(String name) {
		super(name);
		super.setID(0);
	}

	public int getListId() {
		return listId;
	}

	public void setListId(int listId) {
		this.listId = listId;
	}

}
