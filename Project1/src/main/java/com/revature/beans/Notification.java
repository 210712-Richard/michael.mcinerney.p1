package com.revature.beans;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Notification {
	private String username;
	private UUID id;
	private LocalDateTime notificationTime;
	private String message;
	private UUID requestId;
	
	
	public Notification(String username, String message, UUID requestId) {
		this.username = username;
		this.id = UUID.randomUUID();
		this.message = message;
		this.requestId = requestId;
		this.notificationTime = LocalDateTime.now();
	}


	public String getUsername() {
		return username;
	}

	@Override
	public String toString() {
		return "Notification [username=" + username + ", id=" + id + ", notificationTime=" + notificationTime
				+ ", message=" + message + ", requestId=" + requestId + "]";
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public UUID getId() {
		return id;
	}


	public void setId(UUID id) {
		this.id = id;
	}


	public LocalDateTime getNotificationTime() {
		return notificationTime;
	}


	public void setNotificationTime(LocalDateTime notificationTime) {
		this.notificationTime = notificationTime;
	}


	public String getMessage() {
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
	}


	public UUID getRequestId() {
		return requestId;
	}


	public void setRequestId(UUID requestId) {
		this.requestId = requestId;
	}


	@Override
	public int hashCode() {
		return Objects.hash(id, message, notificationTime, requestId, username);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Notification other = (Notification) obj;
		return Objects.equals(id, other.id) && Objects.equals(message, other.message)
				&& Objects.equals(notificationTime, other.notificationTime)
				&& Objects.equals(requestId, other.requestId) && Objects.equals(username, other.username);
	}
	
	
	
	

}
