package com.psw.shortTrack.data;

import java.time.LocalDate;

@SuppressWarnings("serial")
public class GroupTask extends Task {
	
	private Account assignedTo = nobody;
	
	public static Account nobody = new Account(null, "Nobody");

	public GroupTask(String name, int id, String description, LocalDate createdDate, LocalDate deadline, Boolean completed, 
						int groupID, Account assignedTo) {
		
		super(name, id, description, createdDate, deadline, completed, groupID);
		if (assignedTo != null)
			this.assignedTo = assignedTo;		
	}
	
	public GroupTask(String name, int groupID) {
		super(name, groupID);
	}
	
	public GroupTask(int id, String name) {
		super(id, name);
	}

	public Account getAssignedToAccount() { return assignedTo; }
	
	public String getAssignedToEmail() { return assignedTo.getEmail(); }
	
	public void setAssignedTo(Account assignedTo) { this.assignedTo = assignedTo; }

}
