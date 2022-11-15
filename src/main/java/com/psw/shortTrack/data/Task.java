package com.psw.shortTrack.data;

import java.time.LocalDate;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Task implements Serializable {
	/**
	 * Variavel para a escrita/leitura offline dos dados
	 */
	private static final long serialVersionUID = -5301804573647411576L;
	public static int idCount = 1;
	private String name; 
	private int id;
	private String description;
	private LocalDate createdDate;
	private LocalDate deadline;
	private Boolean completed;
	private int parentID;

	public Task(String name, int parentID) {
		this.name = name;
		this.parentID = parentID;
		
		createdDate = LocalDate.now();
		completed = false;
		id = idCount++;
	}
	
	public Task(String name) {			//need some changes in the database
		this(name,0);
	}
	
	public Task(String name, int id, String description, LocalDate createdDate, LocalDate deadline, Boolean completed) {
		this.name = name;
		this.id = id;
		this.description = description;
		this.createdDate = createdDate;
		this.deadline = deadline;
		this.completed = completed;
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
		return parentID;
	}
	
	public boolean chekCompleted() {
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
	
	public void setCompleted(boolean state) {
		completed = state;
	}
	
	public void setParentID(int newParentID) {		// Maybe useless
		parentID = newParentID;
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