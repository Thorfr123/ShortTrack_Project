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
	
	public String getDescription() {
		return description;
	}
	
	public LocalDateTime getCreatedDateTime() {
		return createdDateTime;
	}
	
	public LocalDateTime getDeadlineDateTime() {
		return deadlineDateTime;
	}
	
	public boolean chekCompleted() {
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
