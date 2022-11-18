package com.psw.shortTrack.data;

import java.time.LocalDate;

public class GroupTask extends Task {
	private static final long serialVersionUID = 8953497056843371921L;
	
	private String assignedTo;
	//private int groupId;

	public GroupTask(String name, int id, String description, LocalDate createdDate, LocalDate deadline, Boolean completed, int groupID, String assignedTo) {
		super(name, id, description, createdDate, deadline, completed, groupID);
		//this.groupId = groupID;
		this.assignedTo = assignedTo;
	}
	
	public GroupTask(String name, int groupID) {
		super(name, groupID);
	}

	public String getAssignedTo() {
		return assignedTo;
	}
	
	public void setAssignedTo(String assignedTo) {
		this.assignedTo = assignedTo;
	}

	/*public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}*/
	
}