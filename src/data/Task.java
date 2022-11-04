package data;

import java.time.LocalDate;
import java.io.Serializable;

public class Task implements Serializable {
	/**
	 * Variavel para a escrita/leitura offline dos dados
	 */
	private static final long serialVersionUID = -5301804573647411576L;
	private String name; 
	private int ID;
	private String description;
	private LocalDate createdDate;
	private LocalDate deadline;
	private boolean completed;
	
	public Task(String name) {
		this.name = name;
		
		createdDate = LocalDate.now();
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
	
	public LocalDate getCreatedDate() {
		return createdDate;
	}
	
	public LocalDate getDeadlineDate() {
		return deadline;
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
	
	public void setDeadline(LocalDate newDeadline) {
		deadline = newDeadline;
	}
	
	public void setCompleted(boolean state) {
		completed = state;
	}
	
}
