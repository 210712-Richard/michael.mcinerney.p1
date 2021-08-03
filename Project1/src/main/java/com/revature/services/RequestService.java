package com.revature.services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import com.revature.beans.ApprovalStatus;
import com.revature.beans.EventType;
import com.revature.beans.GradingFormat;
import com.revature.beans.Request;

public interface RequestService {
	
	/**
	 * Creates a request with the parameters
	 * @param username The username of the user
	 * @param firstName The first name of the user
	 * @param lastName The last name of the user
	 * @param deptName The department name of the user
	 * @param name The name of the event
	 * @param startDate The start date of the event
	 * @param startTime The start time of the event
	 * @param location The location of the event
	 * @param description The description of the event
	 * @param cost The cost of the event
	 * @param gradingFormat The grading format of the event
	 * @param type The type of event the event is
	 * @return A new Request
	 */
	public Request createRequest(String username, String firstName, String lastName, String deptName, String name, LocalDate startDate,
			LocalTime startTime, String location, String description, Double cost, GradingFormat gradingFormat,
			EventType type);
	
	/**
	 * Change the approval status of the request 
	 * @param request The request being changed
	 * @param status The status the request is adding 
	 * @param reason The potential reason (can be null if just being approved)
	 * @return The request. Will return null if there was an issue
	 */
	public Request changeApprovalStatus(Request request, ApprovalStatus status, String reason);
	
	/**
	 * Get the Request based on the ID of the user
	 * @param id
	 * @return
	 */
	public Request getRequest(UUID id);
}
