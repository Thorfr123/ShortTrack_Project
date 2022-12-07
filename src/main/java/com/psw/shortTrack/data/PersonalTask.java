package com.psw.shortTrack.data;

import java.time.LocalDate;

public class PersonalTask extends Task {

	private static final long serialVersionUID = -8844284273122638778L;
	
	public PersonalTask(String name, int id, String description, LocalDate createdDate, LocalDate deadline, 
							Boolean completed, int listID) {
		super(name, id, description, createdDate, deadline, completed, listID);
	}
	
	public PersonalTask(String name, int listID) {
		super(name, listID);
	}
	
}
