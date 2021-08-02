package com.revature.services;

import com.revature.beans.User;

public interface UserService {
	
	public User login(String username, String password);
}
