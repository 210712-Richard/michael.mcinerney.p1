package com.revature.services;

import com.revature.beans.User;
import com.revature.beans.UserType;

public interface UserService {

	/**
	 * Allows the user to login using their username and password
	 * 
	 * @param username The username of the user
	 * @param password The password of the user
	 * @return The user with the same username and password; null otherwise
	 */
	public User login(String username, String password);

	/**
	 * Create the user with the arguments
	 * @param username The username of the user
	 * @param password The password of the user
	 * @param firstName The first name of the user
	 * @param lastName The last name of the user
	 * @param email The email of the user
	 * @param type The UserType of the user
	 * @param supervisorUsername The username of the supervisor of the user
	 */
	public void createUser(String username, String password, String email, String firstName, String lastName, 
			UserType type, String deptName,  String supervisorUsername);
}
