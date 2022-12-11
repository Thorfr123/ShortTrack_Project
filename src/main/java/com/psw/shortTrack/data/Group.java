package com.psw.shortTrack.data;

import java.util.ArrayList;

@SuppressWarnings("serial")
public class Group extends TaskOrganizer{
	
	private ArrayList<Account> members = new ArrayList<Account>(0);
	private Account manager;
	
	public Group(String name, Account manager, int id, ArrayList<Task> taskList, ArrayList<Account> members) {
		super(name,id,taskList);
		this.manager = manager;
		this.members = members;
	}
	
	public Group(String name, Account manager) {
		super(name);
		this.manager = manager;
	}
	
	public Group(int id, String name) {
		super(name, id);
	}
	
	public Account getManagerAccount() { return manager; }
	
	public String getManagerEmail() { return manager.getEmail(); }
	
	public ArrayList<Account> getMemberAccounts() { return members; }
	
	public ArrayList<String> getMemberEmails() {
		
		if (members == null)
			return null;
		
		ArrayList<String> memberEmails = new ArrayList<String>(members.size());
		for(Account a : members) {
			if (a != null)
				memberEmails.add(a.getEmail());
		}
		return memberEmails;
		
	}
	
}
