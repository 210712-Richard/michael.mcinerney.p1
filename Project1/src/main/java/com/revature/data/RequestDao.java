package com.revature.data;

import java.util.List;
import java.util.UUID;

import com.revature.beans.Request;

public interface RequestDao {
	
	/**
	 * Get the Request by its id
	 * @param id The UUID of the Request
	 * @return The Request with the same UUID
	 */
	public Request getRequest(UUID id);
	
	/**
	 * Get all Requests in the database
	 * @return A List of all Requests
	 */
	public List<Request> getRequests();
	
	/**
	 * Update the Request
	 * @param request The request to update
	 */
	public void updateRequest(Request request);
	
	/**
	 * Create a new Request and put it in the database
	 * @param request The Request to put in the database
	 */
	public void createRequest(Request request);
	
}
