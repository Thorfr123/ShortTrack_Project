package data;

import java.util.ArrayList;

import gui.ListButton;

public class List {
	private String name;
	private ArrayList<Task> taskList; 
	
	public List(String name) {
		this.name = name;
		
		taskList = new ArrayList<Task>();
	}
	
	public boolean checkName(String name) {
		
		for(Task t : taskList) {
			if(t.getName().equals(name))
				return true;
		}
		
		return false;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String newName) {
		name = newName;
	}
	
	public void addTask(Task newTask) {
		taskList.add(newTask);
	}
	
	public void removeTask(Task task) {
		taskList.remove(task);
	}
	
	public ArrayList<Task> getTaskList() {
		return taskList;
	}
}
