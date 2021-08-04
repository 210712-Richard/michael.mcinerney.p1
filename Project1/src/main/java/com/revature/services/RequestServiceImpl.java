package com.revature.services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revature.beans.Approval;
import com.revature.beans.ApprovalStatus;
import com.revature.beans.Department;
import com.revature.beans.EventType;
import com.revature.beans.Format;
import com.revature.beans.GradingFormat;
import com.revature.beans.ReimbursementRequest;
import com.revature.beans.Request;
import com.revature.beans.RequestStatus;
import com.revature.beans.User;
import com.revature.data.DepartmentDao;
import com.revature.data.DepartmentDaoImpl;
import com.revature.data.RequestDao;
import com.revature.data.RequestDaoImpl;
import com.revature.data.UserDao;
import com.revature.data.UserDaoImpl;
import com.revature.exceptions.IllegalApprovalAttemptException;
import com.revature.factory.BeanFactory;
import com.revature.factory.TraceLog;
import com.revature.util.Verifier;

@TraceLog
public class RequestServiceImpl implements RequestService {
	RequestDao reqDao = (RequestDao) BeanFactory.getFactory().getObject(RequestDao.class, RequestDaoImpl.class);
	UserDao userDao = (UserDao) BeanFactory.getFactory().getObject(UserDao.class, UserDaoImpl.class);
	DepartmentDao deptDao = (DepartmentDao) BeanFactory.getFactory().getObject(DepartmentDao.class,
			DepartmentDaoImpl.class);

	private static final Logger log = LogManager.getLogger(RequestServiceImpl.class);

	private static final Verifier VERIFIER = new Verifier();

	@Override
	public Request createRequest(String username, String firstName, String lastName, String deptName, String name,
			LocalDate startDate, LocalTime startTime, String location, String description, Double cost,
			GradingFormat gradingFormat, EventType type) {

		Request request = null;
		// VERIFIER checks to see if any of the strings are blank or null. If any are,
		// will return false
		if (VERIFIER.verifyStrings(username, firstName, lastName, deptName, name, location, description)) {
			// Make sure the other objects are not null and that the startDate is after the
			// current date
			if (VERIFIER.verifyNotNull(startDate, startTime, cost, gradingFormat, type)
					&& startDate.isAfter(LocalDate.now()) && cost > 0.00) {
				// Get the user as it will be used to check to make sure they have the balance
				User user = userDao.getUser(username);

				Double reimburseAmount = cost * type.getPercent();
				Double reimburseMax = Request.MAX_REIMBURSEMENT - user.getAwardedBalance() - user.getPendingBalance();
				reimburseMax = (reimburseAmount > reimburseMax) ? reimburseMax : reimburseAmount;
				log.debug("Maximum amount that can be reimbursed: " + reimburseMax);

				// If the maximum that can be reimbursed is not less than or equal to zero, the
				// request is valid
				if (reimburseMax > 0) {
					request = new ReimbursementRequest(username, firstName, lastName, deptName, name, startDate,
							startTime, location, description, reimburseMax, gradingFormat, type);
					request.setId(UUID.randomUUID());
					request.setReimburseAmount(reimburseMax);

					// If the date that it is starting is less than two weeks away, then need to set
					// isUrgent to true
					LocalDate twoWeeks = LocalDate.now().plus(Period.of(0, 0, 14));
					request.setIsUrgent(startDate.isBefore(twoWeeks));

					// Set the supervisor approval to the user's supervisor and start the deadline
					request.getSupervisorApproval().setUsername(user.getSupervisorUsername());
					request.getSupervisorApproval().startDeadline();
					request.getSupervisorApproval().setStatus(ApprovalStatus.AWAITING);
					log.debug("Supervisor's deadline set to " + request.getSupervisorApproval().getDeadline());
					// Add the request to the database
					reqDao.createRequest(request);

					// Make sure the user's pending amount is changed and the request added to their
					// requests list.
					user.getRequests().add(request.getId());
					user.alterPendingBalance(reimburseMax);

					// Update the user in the database
					userDao.updateUser(user);
				}
			}

		}
		log.debug("Returning request: " + request);
		return request;
	}

	@Override
	public Request changeApprovalStatus(Request request, ApprovalStatus status, String reason) {
		Request retRequest = null;
		// If the status is denied and there is a reason, or the request status is
		// active and the arguments are not empty
		if (VERIFIER.verifyNotNull(request, status)
				&& ((status.equals(ApprovalStatus.DENIED) && VERIFIER.verifyStrings(reason))
						|| request.getStatus().equals(RequestStatus.ACTIVE))) {
			// Put all of the approvals into an array
			Approval[] approvals = { request.getSupervisorApproval(), request.getDeptHeadApproval(),
					request.getBenCoApproval(), request.getFinalApproval() };

			for (int i = 0; i < approvals.length; i++) {
				Approval currentApproval = approvals[i];
				
				//If the approval has already been approved, move to the next one
				if (currentApproval.getStatus().equals(ApprovalStatus.APPROVED)
						|| currentApproval.getStatus().equals(ApprovalStatus.AUTO_APPROVED)
						|| currentApproval.getStatus().equals(ApprovalStatus.BYPASSED)) {
					continue;

				}
				// If the approval somehow made it to a denied or unassigned approval (this
				// should not happen, need to throw exception
				else if (currentApproval.getStatus().equals(ApprovalStatus.DENIED)
						|| currentApproval.getStatus().equals(ApprovalStatus.UNASSIGNED)) {
					throw new IllegalApprovalAttemptException(
							"The approval that is being evaluated is " + currentApproval.getStatus());
				}
				Approval nextApproval = (i + 1 < approvals.length) ? approvals[i + 1] : null;
				currentApproval.setStatus(status);

				if (status.equals(ApprovalStatus.DENIED)) {
					request.setStatus(RequestStatus.DENIED);
					request.setReason(reason);
					User user = userDao.getUser(request.getUsername());
					user.alterPendingBalance(request.getReimburseAmount() * -1.0);
					reqDao.updateRequest(request);
					userDao.updateUser(user);
					retRequest = request;
					break;
				}

				// On the supervisor approval
				if (i == 0) {
					// Need to check if the supervisor is also the department head
					Department dept = deptDao.getDepartment(request.getDeptName());

					nextApproval.setUsername(dept.getDeptHeadUsername());
					nextApproval.setStatus(ApprovalStatus.AWAITING);

					// If the supervisor is also a department head, bypass the department approval
					if (dept.getDeptHeadUsername().equals(request.getSupervisorApproval().getUsername())) {
						status = ApprovalStatus.BYPASSED;
						continue;
					}
				}
				// On the BenCo approval
				else if (i == 2) {
					// If the grading format is a presentation, the supervisor will be set to the
					// final approval
					// else, the BenCo will be set to the final approval
					if (request.getGradingFormat().getFormat().equals(Format.PRESENTATION)) {
						request.getFinalApproval().setUsername(request.getSupervisorApproval().getUsername());
					} else {
						request.getFinalApproval().setUsername(request.getBenCoApproval().getUsername());
					}
				}
				// On final approval
				else if (i == 3) {
					// Set the user's balances and set the request to approved
					request.setStatus(RequestStatus.APPROVED);
					User user = userDao.getUser(request.getUsername());
					user.alterPendingBalance(request.getReimburseAmount() * -1.0);
					user.alterAwardedBalance(request.getReimburseAmount());
					userDao.updateUser(user);
				}

				// If there is another approval (Only when it is on final approval)
				if (nextApproval != null) {
					nextApproval.setStatus(ApprovalStatus.AWAITING);
					nextApproval.startDeadline();
				}

				reqDao.updateRequest(request);
				retRequest = request;
				break;

			}

		}
		return retRequest;
	}

	@Override
	public Request getRequest(UUID id) {

		Request retRequest = null;

		// Make sure the id isn't null
		if (id != null) {
			retRequest = reqDao.getRequest(id);
		}

		log.debug("The request returned: " + retRequest);
		return retRequest;
	}
}
