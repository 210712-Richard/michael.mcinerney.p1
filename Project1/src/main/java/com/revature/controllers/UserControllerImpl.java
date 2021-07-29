package com.revature.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revature.factory.BeanFactory;
import com.revature.factory.TraceLog;
import com.revature.services.UserService;
import com.revature.services.UserServiceImpl;

@TraceLog
public class UserControllerImpl implements UserController {
	UserService userService = (UserService) BeanFactory.getFactory().getObject(UserService.class,
			UserServiceImpl.class);
	private static Logger log = LogManager.getLogger(UserControllerImpl.class);
}
