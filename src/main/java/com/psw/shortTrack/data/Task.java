package com.psw.shortTrack.data;

import java.time.LocalDate;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public abstract class Task implements Serializable {
	
	private static final long serialVersionUID = -5301804573647411576L;
	
	private String 		name,
						description;
	private LocalDate 	createdDate,
						deadline;
	private Boolean 	completed;
	private int 		id,
						parent_id;
	
	public static int idCount = 1;


	// Utilizado durante a criação de uma nova task
	public Task(String name, int parentID) {
		this.name = name;
		this.parent_id = parentID;
		this.createdDate = LocalDate.now();
		this.completed = false;
		this.id = idCount++;
	}
	
	//Utilizado quando carrega da database ou local files
	public Task(String name, int id, String description, LocalDate createdDate, LocalDate deadline, Boolean completed, int parentID) {
		this.name = name;
		this.id = id;
		this.description = description;
		this.createdDate = createdDate;
		this.deadline = deadline;
		this.completed = completed;
		this.parent_id = parentID;
	}
	
	public String getName() {
		return name;
	}
	
	public int getID() {
		return id;
	}
	
	public String getDescription() {
		return description;
	}
	
	public LocalDate getCreatedDate() {
		return createdDate;
	}
	
	public LocalDate getDeadlineDate() {
		return deadline;
	}
	
	public int getParentID() {
		return parent_id;
	}
	
	public Boolean chekCompleted() {
		return completed;
	}
	
	public void setName(String newName) {
		name = newName;
	}
	
	public void setID(int newID) {
		id = newID;
	}
	
	public void setDescription(String newDescription) {
		description = newDescription;
	}
	
	public void setDeadline(LocalDate newDeadline) {
		deadline = newDeadline;
	}
	
	public void setCompleted(Boolean state) {
		completed = state;
	}
	
	public void setParentID(int newParentID) {
		parent_id = newParentID;
	}
	
	public static String checkValidDate(String date) {
		
		String DATE_FORMAT = "yyy-MM-dd";
		String errorDescription;
		try {
			DateFormat df = new SimpleDateFormat(DATE_FORMAT);
	        df.setLenient(false);
	        df.parse(date);
	        return null;
	    }
		catch (ParseException e) {
			errorDescription = "Invalid date format!";
			return errorDescription;
		}
		
	}
	
}