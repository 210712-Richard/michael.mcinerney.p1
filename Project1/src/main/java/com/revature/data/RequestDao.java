package com.revature.data;

import java.util.List;
import java.util.UUID;

import com.revature.beans.Request;

public interface RequestDao {
	
	public Request getRequest(UUID id);
	
	public List<Request> getRequests();
	
	public void updateRequest(Request request);
	
	public void createRequest(Request request);
	
}
