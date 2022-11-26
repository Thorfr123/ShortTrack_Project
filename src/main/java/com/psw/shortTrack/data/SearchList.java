package com.psw.shortTrack.data;

public class SearchList extends TaskOrganizer {
	
	private static final long serialVersionUID = 54963574661204278L;

	public SearchList(String name) {
		super(name,0);
	}
	
	public void addTask(PersonalTask task) {
		taskList.add(task);
	}
	
	public void addTask(GroupTask task) {
		taskList.add(task);
	}

}
