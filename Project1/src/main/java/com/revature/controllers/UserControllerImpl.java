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
	private UserService userService = (UserService) BeanFactory.getFactory().getObject(UserService.class,
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
		// Invalidate the session and send back a no-content response
		ctx.req.getSession().invalidate();
		ctx.status(204);
	}

	@Override
	public void createUser(Context ctx) {
		// Get the username and user from the request
		User user = ctx.bodyAsClass(User.class);
		log.debug("User from the body: " + user);
		String username = ctx.pathParam("username");
		log.debug("Username from the path: " + username);

		// If the username being added doesn't match the one in the URL
		if (!user.getUsername().equals(username)) {
			ctx.status(400);
			ctx.html("Usernames do not match");
			return;
		}

		// If the username is unique
		if (userService.isUsernameUnique(user.getUsername())) {
			user = userService.createUser(user.getUsername(), user.getPassword(), user.getEmail(), user.getFirstName(),
					user.getLastName(), user.getType(), user.getDepartmentName(), user.getSupervisorUsername());
			log.debug("User added to the database: " + user);
			ctx.status(201);
			ctx.json(user);
		// If the username is not unique
		} else {
			ctx.status(409);
			ctx.html("Username is already taken");
		}
	}
	
	@Override
	public void deleteNotifications(Context ctx) {
		User loggedUser = ctx.sessionAttribute("loggedUser");
		log.debug("Logged in user: " + loggedUser);
		String username = ctx.pathParam("username");
		log.debug("Username from the path: " + username);
		
		//Make sure it is the correct user
		if (loggedUser == null || !loggedUser.getUsername().equals(username)) {
			log.info("Unauthorized attempt to delete user notifications");
			ctx.status(403);
			return;
		}
		
		//Delete the notifications from the database and clear them in the currently logged in user
		userService.deleteNotifications(username);
		loggedUser.getNotifications().clear();
		
		ctx.status(204);
	}
}
