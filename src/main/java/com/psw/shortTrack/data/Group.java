package com.psw.shortTrack.data;

import java.util.ArrayList;

public class Group extends TaskOrganizer{

	private static final long serialVersionUID = 8490848681679284579L;
	
	private ArrayList<String> members = new ArrayList<String>(0);
	private String manager;
	
	public Group(String name, String manager) {
		super(name);
		this.manager = manager;
	}
	
	public Group(String name, String manager, int id) {
		super(name,id);
		this.manager = manager;
	}
	
	public Group(String name, String manager, int id, ArrayList<Task> taskList) {
		super(name,id,taskList);
		this.manager = manager;
	}
	
	public Group(String name, String manager, int id, ArrayList<Task> taskList, ArrayList<String> members) {
		super(name,id,taskList);
		this.manager = manager;
		this.members = members;
	}
	
	public String getManager() {
		return manager;
	}
	
	public ArrayList<String> getMembers() {
		return members;
	}

	public void setMembers(ArrayList<String> members) {
		this.members = members;
	}
	
}
