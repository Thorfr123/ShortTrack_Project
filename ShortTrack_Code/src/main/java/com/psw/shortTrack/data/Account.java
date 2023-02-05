package com.psw.shortTrack.data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Account {
	private String name;
	private String email;
	
	public Account(String email, String name) {
		this.email = email;
		this.name = name;
	}
	
	public String getName() { return name; }
	
	public String getEmail() { return email; }

	public void setName(String name) { this.name = name; }

	public void setEmail(String email) { this.email = email; }
		
	@Override
	public String toString() {
		if(email != null)
			return name + " (" + email + ")";
		else
			return name;
	}
	
	/**
	 * Checks if the name is valid.
	 * It checks for length limits and if it contains numbers.
	 * 
	 * @param name Name to check
	 * @return (Null) If name is valid; (String) If name is invalid, with the error message
	 */
	public static String checkValidName(String name) {
		
		if (name.isBlank()) {
			return "None of the name fields can be empty!";
		}
		else if(name.contains(" ")) {
			return "None of the name fields can contain white spaces!";
		}
		else if (name.matches(".*\\d+.*")) {
			return "None of the name fields can contain numbers!";
		}
		else if(name.length() > 30) {
			return "Name length exceeds maximum character length allowed!";
		}
		else {
			return null;
		}
		
	}
	
	/**
	 * Checks if the email is valid.
	 * It checks for character length and email format.
	 * 
	 * @param email Email to check
	 * @return (Null) If email is valid; (String) If email is invalid, with the error message
	 */
	public static String checkValidEmail(String email) {
		
        Pattern pattern = Pattern.compile("^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}");
        Matcher matcher = pattern.matcher(email);

        if (email.length() > 254) {
        	return "Email length exceeds maximum character length allowed!";
        }
        else if(!matcher.matches()) {
        	return "Invalid email format!";
		}
        else {
        	return null;
        }
        
	}
	
	/**
	 * Checks if the password is valid.
	 * It only verifies minimum character length
	 * 
	 * @param password Password to check
	 * @return (Null) If password is valid; (String) If password is invalid, with the error message
	 */
	public static String checkValidPassword(String password) {

		if(password.length() < 8) {
			return "Password needs to be at least 8 characters length!";
		}
		else {
			return null;
		}
		
	}
	
	/**
	 * Checks if both the password and the password confirmation are valid.
	 * It checks if the passwords match and character minimum length.
	 * 
	 * @param password Password to check
	 * @param repeatPassword Password confirmation
	 * @return (Null) If both fields are valid; (String) If at least one field is invalid, with the error message
	 */
	public static String checkValidPassword(String password, String repeatPassword) {
		
		String errorDescription = checkValidPassword(password);
		if (errorDescription != null) {
			return errorDescription;
		}
		else if(!password.equals(repeatPassword)) {
			return "The passwords don't match!";
		}
		else {
			return null;
		}
		
	}
	
}
