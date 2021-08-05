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
	
	/**
	 * Get the request by the requestId in the path param
	 * @param ctx The context
	 */
	public void getRequest(Context ctx);
	
	/**
	 * Allow the user to cancel their request
	 * @param ctx The context
	 */
	public void cancelRequest(Context ctx);
	
	/**
	 * Allow the user to upload extra files to their request
	 * @param ctx The context<br>
	 * The body should be the file and should include a filetype header
	 */
	public void uploadExtraFile(Context ctx);
	
	/**
	 * Upload an approval message
	 * @param ctx The context
	 */
	public void uploadMessageFile(Context ctx);
}
