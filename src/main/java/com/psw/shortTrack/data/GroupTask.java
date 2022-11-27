package com.psw.shortTrack.data;

import java.time.LocalDate;

public class GroupTask extends Task {
	private static final long serialVersionUID = 8953497056843371921L;
	
	private Account assignedTo;

	public GroupTask(String name, int id, String description, LocalDate createdDate, LocalDate deadline, Boolean completed, int groupID, Account assignedTo) {
		super(name, id, description, createdDate, deadline, completed, groupID);
		
		if(assignedTo == null)
			this.assignedTo = new Account(null,"Nobody");
		else
			this.assignedTo = assignedTo;
	}
	
	public GroupTask(String name, int groupID) {
		super(name, groupID);
		this.assignedTo = new Account(null,"Nobody");
	}

	public Account getAssignedToAccount() {
		return assignedTo;
	}
	
	public String getAssignedToName() {
		return assignedTo.getName();
	}
	
	public String getAssignedToEmail() {
		return assignedTo.getEmail();
	}
	
	public void setAssignedTo(Account assignedTo) {
		this.assignedTo = assignedTo;
	}

}
