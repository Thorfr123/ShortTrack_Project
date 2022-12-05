package com.psw.shortTrack.data;

public class Notification {
	private int type;
	private Account source, destination;
	private String message;
	
	/*
	 * Code = 0
	 * Source = Conta Teste (teste)
	 * Destination = Conta FFF (ggg@gmail.com)
	 * Message = Conta Teste invited you to the group Group 1 (Accept) (Decline)
	 */
	
	// TODO: Maybe we could add a enum to the notification types
	
	public Notification(int type, Account source, Account destination, String message) {
		this.type = type;
		this.source = source;
		this.destination = destination;		
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
	
	public int getType() {
		return type;
	}
	
	public Account getSource() {
		return source;
	}
	
	// TODO: Maybe useless
	public Account getDestination() {
		return destination;
	}
}
