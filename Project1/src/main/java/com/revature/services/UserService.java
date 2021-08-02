package com.revature.services;

import com.revature.beans.User;

public interface UserService {
	
	/**
	 * Allows the user to login using their username and password
	 * @param username The username of the user
	 * @param password The password of the user
	 * @return The user with the same username and password; null otherwise
	 */
	public User login(String username, String password);
	
	/**
	 * Adds the user to the database
	 * @param user The user being added to the database
	 */
	public void createUser(User user);
}
