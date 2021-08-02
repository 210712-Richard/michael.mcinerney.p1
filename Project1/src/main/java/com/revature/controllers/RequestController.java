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
}
