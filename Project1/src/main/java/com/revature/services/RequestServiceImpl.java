package com.revature.services;

import java.time.LocalDate;
import java.time.LocalTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revature.beans.EventType;
import com.revature.beans.GradingFormat;
import com.revature.beans.Request;
import com.revature.data.RequestDao;
import com.revature.data.RequestDaoImpl;
import com.revature.factory.BeanFactory;
import com.revature.factory.TraceLog;

@TraceLog
public class RequestServiceImpl implements RequestService {
	RequestDao reqDao = (RequestDao) BeanFactory.getFactory().getObject(RequestDao.class,
			RequestDaoImpl.class);
	private static Logger log = LogManager.getLogger(RequestServiceImpl.class);
	
	@Override
	public Request createRequest(String username, String firstName, String lastName, String name, LocalDate startDate,
			LocalTime startTime, String location, String description, Double cost, GradingFormat gradingFormat,
			EventType type) {
		
		return null;
	}
}
