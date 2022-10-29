package database;

import java.util.ArrayList;
import data.*;

public class AccountsDatabase {							//Just for test
	
	private static ArrayList<Account> accounts;
	
	public AccountsDatabase() {
		if(accounts == null)
			accounts = new ArrayList<Account>();
	}

	public boolean checkLogin(String username, String password) {
		
		for(Account a : accounts) {
			if(username.equals(a.getUsername()) && password.equals(a.getPassword()))
				return true;
		}
		
		return false;
	}
	
	public boolean checkEmail(String email) {
		
		for(Account a : accounts) {
			if(email.equals(a.getEmail()))
				return false;
		}
		
		return true;
	}
	
	public Account getAccount(String username, String password) {
		for(Account a : accounts) {
			if(username.equals(a.getUsername()) && password.equals(a.getPassword()))
				return a;
		}
		
		return null;
	}
	
	public void addAccount(Account account) {
		accounts.add(account);
	}
}


