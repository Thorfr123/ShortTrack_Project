package com.psw.shortTrack.data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.io.Serializable;

public abstract class Task implements Serializable {
	
	private static final long serialVersionUID = -5301804573647411576L;
	
	private String 		name,
						description;
	private LocalDate 	createdDate,
						deadline;
	private Boolean 	completed = false;
	private int 		id,
						parent_id;
	
	public static int idCount = 1;

	// Utilizado durante a criação de uma nova task
	public Task(String name, int parentID) {
		this.name = name;
		this.parent_id = parentID;
		this.createdDate = LocalDate.now();
		this.id = idCount++;
	}
	
	// Utilizado nas notificações
	public Task(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	//Utilizado quando carrega da database
	public Task(String name, int id, String description, LocalDate createdDate, LocalDate deadline, 
					Boolean completed, int parentID) {
		this.name = name;
		this.id = id;
		this.description = description;
		this.createdDate = createdDate;
		this.deadline = deadline;
		this.completed = completed;
		this.parent_id = parentID;
	}
	
	public String getName() { return name; }
	
	public int getID() { return id; }
	
	public String getDescription() { return description; }
	
	public LocalDate getCreatedDate() { return createdDate; }
	
	public LocalDate getDeadlineDate() { return deadline; }
	
	public int getParentID() { return parent_id; }
	
	public Boolean isCompleted() { return completed; }
	
	public void setName(String newName) { name = newName; }
	
	public void setID(int newID) { id = newID; }
	
	public void setDescription(String newDescription) { description = newDescription; }
	
	public void setDeadline(LocalDate newDeadline) { deadline = newDeadline; }
	
	public void setCompleted(Boolean newState) { completed = newState; }
	
	/**
	 * Checks if the date, provided as a String, has a valid format and returns it as a LocalDate.
	 * 
	 * @param date Date to check and parse
	 * @return Parsed date or (null) if it can't be parsed
	 */
	public static LocalDate checkValidDate(String date) {
		
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d");
			return LocalDate.parse(date, formatter);
		}
		catch (Exception e) {
			return null;
		}
		
	}
	
}