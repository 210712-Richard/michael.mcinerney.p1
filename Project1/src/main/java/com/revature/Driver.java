package com.revature;

import com.revature.controllers.RequestController;
import com.revature.controllers.RequestControllerImpl;
import com.revature.controllers.UserController;
import com.revature.controllers.UserControllerImpl;
import com.revature.factory.BeanFactory;
import com.revature.util.DatabaseCreator;

public class Driver {

	public static void main(String[] args) {
//		setupDatabase();
//		launchJavalin();
		
	}
	
	/**
	 * Used to setup the initial tables and populate with default rows
	 */
	private static void setupDatabase() {
		DatabaseCreator.dropTables();
		try {
			Thread.sleep(40000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		DatabaseCreator.createTables();
		try {
			Thread.sleep(20000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.exit(0);
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
