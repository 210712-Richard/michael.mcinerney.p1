package com.revature;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.revature.controllers.RequestController;
import com.revature.controllers.RequestControllerImpl;
import com.revature.controllers.UserController;
import com.revature.controllers.UserControllerImpl;
import com.revature.factory.BeanFactory;
import com.revature.util.DatabaseCreator;

import io.javalin.Javalin;
import io.javalin.plugin.json.JavalinJackson;

public class Driver {

	public static void main(String[] args) {
//		setupDatabase();
		launchJavalin();
		
	}
	

	
	/**
	 * Used to launch Javalin to start the REST API
	 */
	private static void launchJavalin() {
		
		//Change mapper settings to make sure LocalDateTime objects show correctly
		ObjectMapper jackson = new ObjectMapper();
		jackson.registerModule(new JavaTimeModule());
		jackson.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		JavalinJackson.configure(jackson);
		
		//Start Javalin to process HTTP requests
		Javalin app = Javalin.create().start(8080);
		
		//The controllers for users and requests.
		UserController userControl = (UserController) BeanFactory.getFactory().getObject(UserController.class,
				UserControllerImpl.class);
		RequestController reqControl = (RequestController) BeanFactory.getFactory().getObject(RequestController.class,
				RequestControllerImpl.class);
		
		//As a user, I can login
		app.post("/users", userControl::login);
		
		//As a user, I can logout
		app.delete("/users", userControl::logout);
		
		//As an employee, I can create a reimbursement request.
		
		//As an employee, I can cancel my reimbursement request.
		
		//As an employee, I can upload the final grade/presentation for the reimbursement request. 

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
			Thread.sleep(90000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		DatabaseCreator.populateUser();
		DatabaseCreator.populateDepartment();
		System.exit(0);
	}
}
