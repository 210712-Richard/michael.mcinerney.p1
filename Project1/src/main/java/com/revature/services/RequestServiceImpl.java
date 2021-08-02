package com.revature.services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revature.beans.EventType;
import com.revature.beans.GradingFormat;
import com.revature.beans.ReimbursementRequest;
import com.revature.beans.Request;
import com.revature.beans.User;
import com.revature.data.RequestDao;
import com.revature.data.RequestDaoImpl;
import com.revature.data.UserDao;
import com.revature.data.UserDaoImpl;
import com.revature.factory.BeanFactory;
import com.revature.factory.TraceLog;
import com.revature.util.Verifier;

@TraceLog
public class RequestServiceImpl implements RequestService {
	RequestDao reqDao = (RequestDao) BeanFactory.getFactory().getObject(RequestDao.class, RequestDaoImpl.class);
	UserDao userDao = (UserDao) BeanFactory.getFactory().getObject(UserDao.class, UserDaoImpl.class);

	private static final Logger log = LogManager.getLogger(RequestServiceImpl.class);

	private static final Verifier VERIFIER = new Verifier();

	@Override
	public Request createRequest(String username, String firstName, String lastName, String deptName, String name, LocalDate startDate,
			LocalTime startTime, String location, String description, Double cost, GradingFormat gradingFormat,
			EventType type) {
		
		Request request = null;
		// VERIFIER checks to see if any of the strings are blank or null. If any are,
		// will return false
		if (VERIFIER.verifyStrings(username, firstName, lastName, deptName, name, location, description)) {
			// Make sure the other objects are not null and that the startDate is after the
			// current date
			if (VERIFIER.verifyNotNull(startDate, startTime, cost, gradingFormat, type)
					&& startDate.isAfter(LocalDate.now())) {
				// Get the user as it will be used to check to make sure they have the balance
				User user = userDao.getUser(username);
				Double reimburseMax = (cost > Request.MAX_REIMBURSEMENT) ? Request.MAX_REIMBURSEMENT : cost;
				reimburseMax -= user.getAwardedBalance() + user.getPendingBalance();
				log.debug("Maximum amount that can be reimbursed: " + reimburseMax);
				
				//If the maximum that can be reimbursed is not less than or equal to zero, the request is valid
				if (reimburseMax <= 0) {
					request = new ReimbursementRequest(username, firstName, lastName, deptName, name, startDate, startTime, location,
							description, cost, gradingFormat, type);
					request.setId(UUID.randomUUID());
					request.setReimburseAmount(reimburseMax);
					
					//If the date that it is starting is less than two weeks away, then need to set isUrgent to true
					LocalDate twoWeeks = LocalDate.now().plus(Period.of(0, 0, 14));
					request.setIsUrgent(startDate.isBefore(twoWeeks));
					
					//Set the supervisor approval to the user's supervisor
					request.setSupervisorUsername(user.getSupervisorUsername());
					
					//Add the request to the database
					reqDao.createRequest(request);
					
					//Make sure the user's pending amount is changed and the request added to their requests list.
					user.getRequests().add(request.getId());
					user.alterPendingBalance(reimburseMax);
					
					//Update the user in the database
					userDao.updateUser(user);
				}
			}

		}
		log.debug("Returning request: " + request);
		return request;
	}
}
