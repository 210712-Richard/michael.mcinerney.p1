package com.revature.data;

import java.util.List;

import com.revature.beans.Request;

public interface RequestDao {
	
	public Request getRequest(Integer id);
	
	public List<Request> getRequests();
	
	public void updateRequest(Request request);
	
	public void createRequest(Request request);
	
}
