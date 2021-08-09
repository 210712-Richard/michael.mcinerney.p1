package com.revature.data;

import java.util.List;

import com.revature.beans.Notification;

public interface NotificationDao {
	/**
	 * Used to get a user's notifications
	 * @param username The username of the user
	 * @return A list of notifications that have the user's username
	 */
	public List<Notification> getUserNotificationList(String username);
	
	/**
	 * Delete a user's notifications from the database
	 * @param username The username of the user
	 */
	public void deleteUserNotifications(String username);
	
	/**
	 * Create a new notification
	 * @param notification A new notification to add
	 */
	public void createNotification(Notification notification);
}