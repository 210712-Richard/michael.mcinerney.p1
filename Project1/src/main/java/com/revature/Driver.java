package com.revature;

import com.revature.controllers.RequestController;
import com.revature.controllers.RequestControllerImpl;
import com.revature.controllers.UserController;
import com.revature.controllers.UserControllerImpl;
import com.revature.factory.BeanFactory;

public class Driver {

	public static void main(String[] args) {
//		setupDatabase();
//		launchJavalin();
		
	}
	
	/**
	 * Used to setup the initial tables and populate with default rows
	 */
	private static void setupDatabase() {
		
	}
	
	/**
	 * Used to launch Javalin to start the REST API
	 */
	private static void launchJavalin() {
		UserController userControl = (UserController) BeanFactory.getFactory().getObject(UserController.class,
				UserControllerImpl.class);
		RequestController reqControl = (RequestController) BeanFactory.getFactory().getObject(RequestController.class,
				RequestControllerImpl.class);
	}

}
