package com.psw.shortTrack.data;

public class Notification {
	int code;
	Account source, destination;
	String message;
	
	public Notification(int code, Account source, Account destination, String message) {
		this.code = code;
		this.source = source;
		this.destination = destination;		
		this.message = message;
	}
}
