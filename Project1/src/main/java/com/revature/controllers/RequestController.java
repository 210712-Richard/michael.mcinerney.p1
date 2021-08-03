package com.revature.controllers;

import io.javalin.http.Context;

public interface RequestController {
	/**
	 * Creates the request
	 * @param ctx The context.<br>
	 * The body should have firstName, lastName, deptName, startDate, startTime, location, description,
	 * cost, gradingFormat, and type
	 */
	public void createRequest(Context ctx);
	
	/**
	 * Change the approval status of the request
	 * @param ctx The context.<br>
	 * The body should have the approval and (if the request is declined) a reason
	 */
	public void changeApprovalStatus(Context ctx);
}
