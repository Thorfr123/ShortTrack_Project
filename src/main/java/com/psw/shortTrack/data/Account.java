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
	
	public static String checkValidName(String name) {				//Just Scratch
		
		String errorDescription;
		if(name.contains(" ")) {
			errorDescription = "Invalid name!";
			return errorDescription;
		}
			
		if(name.length() > 15 || name.length() < 1) {
			errorDescription = "Invalid name!";
			return errorDescription;
		}
		
		return null;
	}
	
	public static String checkValidEmail(String email) {
		//Regular Expression   
        String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}";  
        //Compile regular expression to get the pattern  
        Pattern pattern = Pattern.compile(regex);
        
        Matcher matcher = pattern.matcher(email);
        
        String errorDescription;
        if(!matcher.matches()) {
        	errorDescription = "Invalid email!";
			return errorDescription;
		}
        	
		return null;
	}
	
	public static String checkValidPassword(String password) {
		String errorDescription;
		if(password.length() < 5) {
			errorDescription = "Password to short!";
			return errorDescription;
		}
		return null;
	}
	
	public static String checkValidPassword(String password, String repeatPassword) {
		
		String errorDescription = checkValidPassword(password);
		
		if (errorDescription == null) {
			if(!password.equals(repeatPassword)) {
				errorDescription = "The passwords don't match!";
			}
		}
		
		return errorDescription;
	}
	
}
