package com.psw.shortTrack.data;

public class Notification {
	private int id;
	private NotificationType type;
	private Account source, destination;
	private String message;
	
	/*
	 * Code = 0
	 * Source = Conta Teste (teste)
	 * Destination = Conta FFF (ggg@gmail.com)
	 * Message = Conta Teste invited you to the group Group 1 (Accept) (Decline)
	 */
	
	public enum NotificationType {
		invitateToGroup(1),
		removedFromGroup(2);

		private final int type;
		NotificationType(final int newType) {
			type = newType;
		}
		
		public int toInt() {
			return type;
		}
		
		public static NotificationType getType(int id) {
			for (NotificationType t : values()) {
				if (t.toInt() == id) {
					return t;
				}
			}
			return null;
		}
	}
	
	public Notification(NotificationType type, Account source, Account destination, String message) {
		this.type = type;
		this.source = source;
		this.destination = destination;
		this.message = message;
	}
	
	/**
	 * Called from notification database
	 */
	public Notification(int id, int type, Account source, Account destination, String message) {
		this.id = id;
		this.type = NotificationType.getType(type);
		this.source = source;
		this.destination = destination;
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
	
	public int getTypeAsInt() {
		return type.toInt();
	}
	
	public NotificationType getType() {
		return type;
	}
	
	public Account getSource() {
		return source;
	}
	
	// TODO: Maybe useless
	public Account getDestination() {
		return destination;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
