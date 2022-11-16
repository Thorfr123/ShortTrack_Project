package com.psw.shortTrack.data;

import java.util.ArrayList;

public class Group {
	/**
	 * Variavel para a escrita/leitura offline dos dados
	 */
	private static final long serialVersionUID = -6310572270119855024L;
	public static int idCount = 1;
	private int id;
	private String name;
	private ArrayList<Task> taskList;
	private ArrayList<String> members;
	private String manager;
	
	public Group(String name, String manager) {
		this.name = name;
		this.manager = manager;
		this.id = idCount++;
		
		taskList = new ArrayList<Task>();
	}
	
	public Group(String name, String manager, int id) {
		this.name = name;
		this.manager = manager;
		this.id = id;
		
		taskList = new ArrayList<Task>();
	}
	
	public Group(String name, String manager, int id, ArrayList<Task> taskList) {
		this.name = name;
		this.manager = manager;
		this.id = id;
		this.taskList = taskList;
	}
	
	public Group(String name, String manager, int id, ArrayList<Task> taskList, ArrayList<String> members) {
		this.name = name;
		this.manager = manager;
		this.id = id;
		this.taskList = taskList;
		this.setMembers(members);
	}
	
	public boolean checkName(String name) {
		
		for(Task t : taskList) {
			if(t.getName().equals(name))
				return true;
		}
		
		return false;
	}
	
	public int getID() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getManager() {
		return manager;
	}
	
	public void setID(int id) {
		this.id = id;
	}
	
	public void setName(String newName) {
		name = newName;
	}
	
	public ArrayList<String> getMembers() {
		return members;
	}

	public void setMembers(ArrayList<String> members) {
		this.members = members;
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
