package com.revature.controllers;

import io.javalin.http.Context;

public interface UserController {
	
	/**
	 * Allows the user to login
	 * @param ctx The Context.
	 * Should have username and password fields
	 */
	public void login(Context ctx);
	
	/**
	 * Allows the user to logout
	 * @param ctx The Context
	 * Should have nothing in the body.
	 */
	public void logout(Context ctx);
}
