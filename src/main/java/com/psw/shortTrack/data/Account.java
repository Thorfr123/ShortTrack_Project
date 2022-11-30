package com.psw.shortTrack.data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Account {
	private String name;
	private String email;
	
	public Account(String email, String name) {
		this.name = name;
		this.email = email;
	}
	
	public String getName() {
		return name;
	}
	
	public String getEmail() {
		return email;
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
		
		if (name.isBlank()) {
			return "Name cannot be empty!";
		}
		else if(name.contains(" ")) {
			return "Name cannot contain white spaces!";
		}
		else if(name.length() > 30) {
			return "Name length exceeds maximum character length allowed!";
		}
		
		return null;
	}
	
	public static String checkValidEmail(String email) {
		
        String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}";  
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        
        if (email.length() > 254) {
        	return "Email length exceeds maximum character length allowed!";
        }
        else if(!matcher.matches()) {
        	return "Invalid email!";
		}
        
		return null;
	}
	
	public static String checkValidPassword(String password) {

		String regex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(password);
		
		if(password.length() < 8) {
			return "Password needs to be at least 8 characters length!";
		}
		else if (!matcher.matches()) {
			return "Your password needs to have at least 8 characters length,\none uppercase letter, one lowercase letter and one number!";
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
