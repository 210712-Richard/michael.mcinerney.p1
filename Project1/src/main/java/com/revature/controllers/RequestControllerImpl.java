package com.revature.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revature.beans.ReimbursementRequest;
import com.revature.beans.Request;
import com.revature.beans.User;
import com.revature.factory.BeanFactory;
import com.revature.factory.TraceLog;
import com.revature.services.RequestService;
import com.revature.services.RequestServiceImpl;

import io.javalin.http.Context;

@TraceLog
public class RequestControllerImpl implements RequestController {
	RequestService reqService = (RequestService) BeanFactory.getFactory().getObject(RequestService.class,
			RequestServiceImpl.class);
	private static Logger log = LogManager.getLogger(RequestControllerImpl.class);

	@Override
	public void createRequest(Context ctx) {
		User loggedUser = ctx.sessionAttribute("loggedUser");

		// Make sure the user is logged in
		if (loggedUser == null) {
			ctx.status(401);
			return;
		}

		// Get the request from the body
		Request request = ctx.bodyAsClass(ReimbursementRequest.class);
		log.debug("Request from the body: " + request);
		
		//Create the request.
		request = reqService.createRequest(loggedUser.getUsername(), request.getFirstName(), request.getLastName(),
				request.getDeptName(), request.getName(), request.getStartDate(), request.getStartTime(),
				request.getLocation(), request.getDescription(), request.getCost(), request.getGradingFormat(),
				request.getType());
		
		//If the request was not found
		if (request == null) {
			//TODO: verify status code
			ctx.status(400);
			ctx.html("The Reimbursement Request sent was incorrectly entered.");
		} else { //Otherwise, return the request
			ctx.status(201);
			ctx.json(request);
		}
	}
}
