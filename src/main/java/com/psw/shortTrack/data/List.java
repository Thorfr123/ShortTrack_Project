package com.psw.shortTrack.data;

import java.util.ArrayList;
import com.psw.shortTrack.database.PersonalTasksDatabase;

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
	
	public PersonalTask addTask(String taskName) throws Exception {
		
		if(checkName(taskName))
			throw new IllegalArgumentException("This task already exist!");	
		
		PersonalTask newTask = new PersonalTask(taskName,id);
		
		if(User.isLogedIn())
			PersonalTasksDatabase.createTask(newTask);
		
		taskList.add(newTask);
		
		return newTask;
	}

}
