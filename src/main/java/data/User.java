package data;

import java.util.ArrayList;

public class User {
	
	private static ArrayList<List> lists = new ArrayList<List>(0);
	private static Account account;
	
	public static ArrayList<List> getLists() {
		return lists;
	}
	
	public static Account getAccount() {
		return account;
	}
	
	public static void setAccount(Account account) {
		User.account = account;
	}
	
	public static void setLists(ArrayList<List> lists) {
		User.lists = lists;
	}
	
	public static List getList(int ID) {
		
		for(List l: lists) {
			if(l.getID() == ID)
				return l;
		}
		
		return null;
	}
}
