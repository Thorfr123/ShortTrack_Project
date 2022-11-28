package com.psw.shortTrack.data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Account {
	private String password;
	private String name;
	private String email;
	
	public Account(String email, String password, String name) {
		this.password = password;
		this.name = name;
		this.email = email;
	}
	
	public Account(String email, String name) {
		this.name = name;
		this.email = email;
	}
	
	public String getPassword() {
		return password;
	}
	
	public String getName() {
		return name;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	@Override 
	public String toString() {
		if(email != null)
			return name + " (" + email + ")";
		else
			return name;
	}
	
	public static String checkValidName(String name) {
				
		if(name.contains(" ")) {
			return "Name cannot contain white spaces!";
		}
		else if(name.length() > 30) {
			return "Name length exceeds maximum character length alowed!";
		}
		
		return null;
	}
	
	public static String checkValidEmail(String email) {
		//Regular Expression   
        String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}";  
        //Compile regular expression to get the pattern  
        Pattern pattern = Pattern.compile(regex);
        
        Matcher matcher = pattern.matcher(email);
        
        if(!matcher.matches()) {
        	return "Invalid email!";
		}
        	
		return null;
	}
	
	public static String checkValidPassword(String password) {

		if(password.length() < 5) {
			return "Password needs to be at least 5 characters length!";
		}
		
		return null;
	}
	
	public static String checkValidPassword(String password, String repeatPassword) {
		
		String errorDescription = checkValidPassword(password);
		if (errorDescription != null)
			return errorDescription;
		
		if(!password.equals(repeatPassword)) {
			return "The passwords don't match!";
		}
		
		return null;
	}
	
}
