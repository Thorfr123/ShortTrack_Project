package com.psw.shortTrack.data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;

public abstract class TaskOrganizer implements Serializable {
	
	private static final long serialVersionUID = 5424054108791604712L;
	
	private int id;
	private String name;
	private ArrayList<Task> taskList;
	
	public static int idCount = 1;
	
	public TaskOrganizer(String name, int id, ArrayList<Task> taskList) {
		this.name = name;
		this.id = id;
		this.taskList = taskList;
	}
	
	public TaskOrganizer(String name, int id) {
		this(name, id, new ArrayList<Task>());
	}
	
	public TaskOrganizer(String name) {
		this(name, idCount++);
	}
	
	public int getID() { return id; }
	
	public String getName() { return name; }
	
	public ArrayList<Task> getTaskList() { return taskList; }
	
	public void setID(int id) { this.id = id; }
	
	public void setName(String newName) { name = newName; }
	
	public void addTask(Task task) {
		taskList.add(task);
	}
	
	public void removeTask(Task task) {
		taskList.remove(task);
	}
	
	/**
	 * Checks if the name is already in use
	 * 
	 * @param name Name to check
	 * @return (True) It already exists; (False) Otherwise
	 */
	public boolean checkName(String name) {
		
		for(Task t : taskList) {
			if(t.getName().equals(name))
				return true;
		}
		return false;
		
	}
	
	public void sort(SortBy option) {
		if (taskList == null)
			return;
		
		switch(option) {
		case Name:
			taskList.sort(new TaskByName()
					.thenComparing(new TaskByCompleted())
					.thenComparing(new TaskByDeadlineDate())
					.thenComparing(new TaskByCreatedDate()));
			break;
		case CreatedDate:
			taskList.sort(new TaskByCreatedDate()
					.thenComparing(new TaskByCompleted())
					.thenComparing(new TaskByDeadlineDate())
					.thenComparing(new TaskByName()));
			break;
		case DeadlineDate:
			taskList.sort(new TaskByDeadlineDate()
					.thenComparing(new TaskByCompleted())
					.thenComparing(new TaskByCreatedDate())
					.thenComparing(new TaskByName()));
			break;
		case Completed:
			taskList.sort(new TaskByCompleted()
					.thenComparing(new TaskByDeadlineDate())
					.thenComparing(new TaskByCreatedDate())
					.thenComparing(new TaskByName()));
			break;
		default:
			taskList.sort(new TaskByCompleted()
					.thenComparing(new TaskByDeadlineDate())
					.thenComparing(new TaskByCreatedDate())
					.thenComparing(new TaskByName()));
			break;
		}
	}
	
	// TODO: comment
	public void findTaskByName(String searchName, ArrayList<Task> tasks) {
		
		for(Task t: taskList) {
			if(t.getName().toLowerCase().contains(searchName.toLowerCase()))
				tasks.add(t);
		}

	}
	
	// TODO: comment
	public void findTaskByCreatedDate(LocalDate searchCreatedDate, ArrayList<Task> tasks) {
		
		for(Task t: taskList) {
			LocalDate createdDate = t.getCreatedDate();
			if(createdDate.equals(searchCreatedDate))
				tasks.add(t);
		}

	}
	
	// TODO: comment
	public void findTaskByDeadline(LocalDate searchDeadline, ArrayList<Task> tasks) {
		
		for(Task t: taskList) {
			LocalDate deadlineDate = t.getDeadlineDate();
			if(deadlineDate == null)
				continue;
			
			if(deadlineDate.equals(searchDeadline))
				tasks.add(t);
		}

	}
	
	public class TaskByName implements Comparator<Task> {

		@Override
		public int compare(Task o1, Task o2) {
			return (o1.getName().compareTo(o2.getName()));
		}
	}
	
	public class TaskByCreatedDate implements Comparator<Task> {

		@Override
		public int compare(Task o1, Task o2) {
			if (o1.getCreatedDate() == null) {
				if (o2.getCreatedDate() == null)
					return 0;
				else
					return 1;
			}
			else if (o2.getCreatedDate() == null)
				return -1;
			
			return (o1.getCreatedDate().compareTo(o2.getCreatedDate()));
		}
	}
	
	public class TaskByDeadlineDate implements Comparator<Task> {

		@Override
		public int compare(Task o1, Task o2) {
			if (o1.getDeadlineDate() == null) {
				if (o2.getDeadlineDate() == null)
					return 0;
				else
					return 1;
			}
			else if (o2.getDeadlineDate() == null)
				return -1;
			
			return (o1.getDeadlineDate().compareTo(o2.getDeadlineDate()));
		}
	}
	
	public class TaskByCompleted implements Comparator<Task> {

		@Override
		public int compare(Task o1, Task o2) {
			return (o1.isCompleted().compareTo(o2.isCompleted()));
		}
	}

}
