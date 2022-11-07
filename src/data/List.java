package data;

import java.io.Serializable;
import java.util.ArrayList;

public class List implements Serializable{
	/**
	 * Variavel para a escrita/leitura offline dos dados
	 */
	private static final long serialVersionUID = -6310572270119855024L;
	public static int idCount = 1;
	private int id;
	private String name;
	private ArrayList<Task> taskList; 
	
	public List(String name) {
		this.name = name;
		this.id = idCount++;
		
		taskList = new ArrayList<Task>();
	}
	
	public boolean checkName(String name) {
		
		for(Task t : taskList) {
			if(t.getName().equals(name))
				return true;
		}
		
		return false;
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setId(int id) {
		this.id = id;
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
