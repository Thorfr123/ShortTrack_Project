package com.psw.shortTrack.data;

public class SearchList extends TaskOrganizer {
	
	private static final long serialVersionUID = 54963574661204278L;

	public SearchList(String name) {
		super(name,0);
	}
	
	public PersonalTask addPersonalTask(String taskName) {
		
		if(checkName(taskName))
			return null;	
		
		PersonalTask newTask = new PersonalTask(taskName,id);
		taskList.add(newTask);
		
		return newTask;
	}
	
	public GroupTask addGroupTask(String taskName) {
		
		if(checkName(taskName))
			return null;	
		
		GroupTask newTask = new GroupTask(taskName,id);
		taskList.add(newTask);
		
		return newTask;
	}

}
