package data;

import java.io.Serializable;
import java.util.ArrayList;

public class List implements Serializable{
	/**
	 * Variavel para a escrita/leitura offline dos dados
	 */
	private static final long serialVersionUID = -6310572270119855024L;
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
	
	public void orderByName() {
		
		if(taskList == null)
			return;
		
		ArrayList<Task> newTaskList = new ArrayList<Task>();
        while(taskList.size() != 0) {
        	Task pickTask = taskList.get(0);
    		for(Task t : taskList) {
    			if(t.getName().compareTo(pickTask.getName()) < 0)
    				pickTask = t;
    		}
    		taskList.remove(pickTask);
    		newTaskList.add(pickTask);
        }
        
        taskList = newTaskList;
	}
	
	public void orderByCreatedDate() {
		
	}
	
	public void orderByDeadline() {
		
	}
	
	public void orderByCompleted() {
		
	}
	
	public ArrayList<Task> findTaskByName(String name) { 		// return all tasks that include that string
		
		return null;
	}
}
