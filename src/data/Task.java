package data;

import java.time.LocalDateTime;

public class Task {
	private String name; 
	private int ID;
	private String description;
	private LocalDateTime createdDateTime;
	private LocalDateTime deadlineDateTime;
	private boolean completed;
	
	public Task(String name) {
		this.name = name;
		
		createdDateTime = LocalDateTime.now();
		completed = false;
	}
	
	public String getName() {
		return name;
	}
	
	public int getID() {
		return ID;
	}
	
	public String description() {
		return description;
	}
	
	public LocalDateTime createdDateTime() {
		return createdDateTime;
	}
	
	public LocalDateTime deadlineDateTime() {
		return deadlineDateTime;
	}
	
	public boolean completed() {
		return completed;
	}
	
	public void setName(String newName) {
		name = newName;
	}
	
	public void setID(int newID) {
		ID = newID;
	}
	
	public void setDescription(String newDescription) {
		description = newDescription;
	}
	
	public void setDeadlineDateTime(LocalDateTime newDeadlineDateTime) {
		deadlineDateTime = newDeadlineDateTime;
	}
	
	public void changeCompleted() {
		completed = !completed;
	}
	
}
