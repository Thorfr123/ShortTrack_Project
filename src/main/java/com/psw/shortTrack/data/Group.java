package com.psw.shortTrack.data;

import java.util.ArrayList;

@SuppressWarnings("serial")
public class Group extends TaskOrganizer{
	
	private ArrayList<Account> members = new ArrayList<Account>(0);
	private Account manager;
	
	public Group(String name, Account manager) {
		super(name);
		this.manager = manager;
	}
	
	public Group(String name, Account manager, int id) {
		super(name,id);
		this.manager = manager;
	}
	
	public Group(String name, Account manager, int id, ArrayList<Task> taskList) {
		super(name,id,taskList);
		this.manager = manager;
	}
	
	public Group(String name, Account manager, int id, ArrayList<Task> taskList, ArrayList<Account> members) {
		super(name,id,taskList);
		this.manager = manager;
		this.members = members;
	}
	
	public Account getManagerAccount() {
		return manager;
	}
	
	public String getManagerEmail() {
		return manager.getEmail();
	}
	
	public String getManagerName() {
		return manager.getName();
	}
	
	public ArrayList<Account> getMemberAccounts() {
		return members;
	}
	
	public void addTask(GroupTask task) {
		taskList.add(task);
	}
	
	public ArrayList<String> getMemberEmails() {
		
		ArrayList<String> memberEmails = new ArrayList<String>(0);
		
		for(Account a : members) {
			if (a != null)
				memberEmails.add(a.getEmail());
		}
		
		return memberEmails;
	}

	public void setMembers(ArrayList<Account> members) {
		this.members = members;
	}
	
	public void addMember(Account newMember) {

		for (Account acc : members) {
			if (acc.getEmail().equals(newMember.getEmail())) {
				return;
			}
		}
		members.add(newMember);
	}
	
	public void removeMember(Account member) {
		
		for (Account acc : members) {
			if (acc.getEmail().equals(member.getEmail())) {
				members.remove(acc);
			}
		}
	}

}
