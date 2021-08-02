package com.revature.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
		
	}
}
