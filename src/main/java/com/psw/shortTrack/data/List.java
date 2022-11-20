package com.psw.shortTrack.data;

import java.util.ArrayList;

public class List extends TaskOrganizer {
	
	private static final long serialVersionUID = -6310572270119855024L;
	
	public List(String name) {
		super(name);
	}
	
	public List(String name, int id) {
		super(name,id);
	}
	
	public List(String name, int id, ArrayList<Task> taskList) {
		super(name,id,taskList);
	}
	
	public PersonalTask addTask(String taskName) {
		
		if(checkName(taskName))
			return null;	
		
		PersonalTask newTask = new PersonalTask(taskName,id);
		taskList.add(newTask);
		
		return newTask;
	}

}
