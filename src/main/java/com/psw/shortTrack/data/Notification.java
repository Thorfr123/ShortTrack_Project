package com.psw.shortTrack.data;

public class Notification {
	private int id;
	private NotificationType type;
	private Account source, destination;
	private String message;
	private Group ref_group;
	private Task ref_task;
	
	public enum NotificationType {
		invitateToGroup(1),
		removedFromGroup(2),
		leftGroup(3),
		acceptedInviteToGroup(4),
		askForHelp(5),
		acceptedAskForHelp(6);

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
	public Notification(NotificationType type, Account source, Account destination, Group ref_group, Task ref_task) {
		this.type = type;
		this.source = source;
		this.destination = destination;
		this.ref_group = ref_group;
		this.ref_task = ref_task;
		this.message = buildMessage();
	}
	
	public Notification(NotificationType type, Account source, Account destination, Group ref_group) {
		this(type, source, destination, ref_group, null);
	}
	
	/**
	 * Called from notification database
	 */
	public Notification(int id, int type, Account source, Account destination, Group ref_group, Task ref_task) {		
		this (NotificationType.getType(type), source, destination, ref_group, ref_task);
		this.id = id;
	}
	
	private String buildMessage() {
		switch (type) {
		case invitateToGroup:
			return (this.source.getName() + " invited you to the group " + ref_group.getName());
		case removedFromGroup:
			return (this.source.getName() + " removed you from the group " + ref_group.getName());
		case leftGroup:
			return (this.source.getName() + " left the group " + ref_group.getName());
		case acceptedInviteToGroup:
			return (this.source.getName() + " accepted your invitation to enter group " + ref_group.getName());
		case askForHelp:
			return (this.source.getName() + " asked for help in the task " + ref_task.getName() + " of the group " + ref_group.getName());
		case acceptedAskForHelp:
			return (this.source.getName() + " accepted your help request for the task " + ref_task.getName() + " of the group " + ref_group.getName());
		default:
			return ("Notification without purpose");
		}
	}
	
	public NotificationType getResponseType() {
		switch (type) {
		case invitateToGroup:
			return NotificationType.acceptedInviteToGroup;
		case askForHelp:
			return NotificationType.acceptedAskForHelp;
		default:
			return null;
		}
	}
	
	public String getMessage() { return message; }
	
	public int getTypeAsInt() { return type.toInt(); }
	
	public NotificationType getType() { return type; }
	
	public Account getSource() { return source; }
	
	public Account getDestination() { return destination; }
	
	public int getId() { return id; }

	public void setId(int id) { this.id = id; }
	
	public Group getRefGroup() { return ref_group; }
	
	public Task getRefTask() { return ref_task; }
	
}
