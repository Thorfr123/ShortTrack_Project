package com.psw.shortTrack.data;

import java.util.ArrayList;

public class User {
	
	private static ArrayList<List> lists = new ArrayList<List>(0);
	private static ArrayList<Group> groups = new ArrayList<Group>(0);
	private static Account account;
	private static Boolean logedIn = false;
	private static ArrayList<Notification> notifications = new ArrayList<Notification>(0);
	
	public static ArrayList<List> getLists() { return lists; }
	
	public static ArrayList<Group> getGroups() { return groups; }
	
	public static Account getAccount() { return account; }
	
	public static void setAccount(Account account) { User.account = account; }
	
	public static void setLists(ArrayList<List> lists) { User.lists = lists; }
	
	public static void setGroups(ArrayList<Group> groups) { User.groups = groups; }
	
	public static ArrayList<Notification> getNotifications() { return notifications; }
	
	public static void setNotifications(ArrayList<Notification> notifications) { User.notifications = notifications; }
	
	public static Boolean isLogedIn() { return logedIn; }
	
	public static void setLogedIn(Boolean newState) {
		logedIn = newState;
		
		if(!logedIn) {
			setGroups(null);
			setLists(null);
			setAccount(null);
		}
			
	}
	
	public static List getList(int ID) {
		
		for(List l: lists) {
			if(l.getID() == ID)
				return l;
		}
		
		return null;
	}
	
	public static Group getGroup(int ID) {
		
		for(Group g: groups) {
			if(g.getID() == ID)
				return g;
		}
		
		return null;
	}
	
	public static boolean checkListName(String listName) {
		for (List l : lists) {
			if (l.getName().equals(listName))
				return true;
		}
		
		return false;
	}
	
	public static Group checkGroupName(String groupName) {
		
		for(Group g : groups) {
			if(g.getName().equals(groupName) && g.getManagerEmail().equals(account.getEmail()))
				return g;
		}
		
		return null;
	}
	
}
