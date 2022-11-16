package com.psw.shortTrack.data;

import java.time.LocalDate;

public class GroupTask extends Task {
	private static final long serialVersionUID = 8953497056843371921L;
	private static int idCount = 1;
	private int groupID;
	private String assignedTo;

	public GroupTask(String name, int id, String description, LocalDate createdDate, LocalDate deadline, Boolean completed, String assignedTo) {
		super(name, id, description, createdDate, deadline, completed);
		this.assignedTo = assignedTo;
	}
	
	public GroupTask(String name, int id, String description, LocalDate createdDate, LocalDate deadline, Boolean completed) {
		super(name, id, description, createdDate, deadline, completed);
		this.assignedTo = null;
	}
	
	public GroupTask(String name, int parentID) {
		super(name, parentID);
	}

	public GroupTask(String name) {
		super(name);
	}

	public String getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(String assignedTo) {
		this.assignedTo = assignedTo;
	}



}
