package com.psw.shortTrack.data;

public class Notification {
	private int id;
	private NotificationType type;
	private Account source, destination;
	private String message;
	private Group group;
	
	public enum NotificationType {
		invitateToGroup(1),
		removedFromGroup(2),
		leftGroup(3),
		acceptedInviteToGroup(4);

		private final int type;
		NotificationType(final int newType) { 
			type = newType;
		}
		
		public int toInt() { return type; }
		
		public static NotificationType getType(int id) {
			for (NotificationType t : values()) {
				if (t.toInt() == id) {
					return t;
				}
			}
			return null;
		}
	}
	
	/**
	 * Called when we create a new notification
	 */
	public Notification(NotificationType type, Account source, Account destination, Group group) {
		this.type = type;
		this.source = source;
		this.destination = destination;
		this.group = group;
		
		switch (type) {
		case invitateToGroup:
			this.message = this.source.getName() + " invited you to the group " + group.getName();
			break;
		case removedFromGroup:
			this.message = this.source.getName() + " removed you from the group " + group.getName();
			break;
		case leftGroup:
			this.message = this.source.getName() + " left the group " + group.getName();
			break;
		case acceptedInviteToGroup:
			this.message = this.source.getName() + " accepted your invitation to enter group " + group.getName();
			break;
		default:
			this.message = "Notification without purpose";
		}
	}
	
	/**
	 * Called from notification database
	 */
	public Notification(int id, int type, Account source, Account destination, String group_name, int group_id) {
		this (NotificationType.getType(type), source, destination, new Group(group_id, group_name));
		this.id = id;
	}
	
	public String getMessage() { return message; }
	
	public int getTypeAsInt() { return type.toInt(); }
	
	public NotificationType getType() { return type; }
	
	public Account getSource() { return source; }
	
	public Account getDestination() { return destination; }
	
	public int getId() { return id; }

	public void setId(int id) { this.id = id; }
	
	public int getGroup_id() { return group.getID(); }
	
	public Group getGroup() { return group; }
	
}
