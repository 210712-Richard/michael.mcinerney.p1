package com.revature.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revature.beans.User;
import com.revature.factory.BeanFactory;
import com.revature.factory.TraceLog;
import com.revature.services.UserService;
import com.revature.services.UserServiceImpl;

import io.javalin.http.Context;

@TraceLog
public class UserControllerImpl implements UserController {

	// The service for handling User objects
	UserService userService = (UserService) BeanFactory.getFactory().getObject(UserService.class,
			UserServiceImpl.class);

	// Log
	private static Logger log = LogManager.getLogger(UserControllerImpl.class);

	@Override
	public void login(Context ctx) {

		// Get the user from the body
		User user = ctx.bodyAsClass(User.class);
		log.debug("User object from body: " + user);

		// Check to make sure the username and password from the database are the same
		user = userService.login(user.getUsername(), user.getPassword());
		log.debug("User returned from the service: " + user);

		// The user was not found
		if (user == null) {
			ctx.status(401);
			return;
		}

		// Set the logged in user to be the current user and send the user back.
		ctx.sessionAttribute("loggedUser", user);
		ctx.json(user);
	}

	@Override
	public void logout(Context ctx) {
		//Invalidate the session and send back a no-content response
		ctx.req.getSession().invalidate();
		ctx.status(204);
	}

	@Override
	public void createUser(Context ctx) {
		
	}
}
