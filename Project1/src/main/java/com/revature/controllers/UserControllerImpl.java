package com.revature.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revature.factory.BeanFactory;
import com.revature.factory.TraceLog;
import com.revature.services.UserService;
import com.revature.services.UserServiceImpl;

import io.javalin.http.Context;

@TraceLog
public class UserControllerImpl implements UserController {
	
	//The service for handling User objects
	UserService userService = (UserService) BeanFactory.getFactory().getObject(UserService.class,
			UserServiceImpl.class);
	
	//Log
	private static Logger log = LogManager.getLogger(UserControllerImpl.class);
	
	
	@Override
	public void login(Context ctx) {
		
	}
}
