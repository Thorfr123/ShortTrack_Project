package com.psw.shortTrack.data;

public class Notification {
	private int code;
	private Account source, destination;
	private String message;
	
	/*
	 * Code = 0
	 * Source = Conta Teste (teste)
	 * Destination = Conta FFF (ggg@gmail.com)
	 * Message = Conta Teste invited you to the group Group 1 (Accept) (Decline)
	 */
	
	public Notification(int code, Account source, Account destination, String message) {
		this.code = code;
		this.source = source;
		this.destination = destination;		
		this.message = message;
	}
	
}
