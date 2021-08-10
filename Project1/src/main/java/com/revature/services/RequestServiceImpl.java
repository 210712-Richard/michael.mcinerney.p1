package com.revature.services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revature.beans.Approval;
import com.revature.beans.ApprovalStatus;
import com.revature.beans.Department;
import com.revature.beans.EventType;
import com.revature.beans.Format;
import com.revature.beans.GradingFormat;
import com.revature.beans.Notification;
import com.revature.beans.ReimbursementRequest;
import com.revature.beans.Request;
import com.revature.beans.RequestStatus;
import com.revature.beans.User;
import com.revature.data.DepartmentDao;
import com.revature.data.DepartmentDaoImpl;
import com.revature.data.NotificationDao;
import com.revature.data.NotificationDaoImpl;
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

	// All the DAO's needed to access resources
	RequestDao reqDao = (RequestDao) BeanFactory.getFactory().getObject(RequestDao.class, RequestDaoImpl.class);
	UserDao userDao = (UserDao) BeanFactory.getFactory().getObject(UserDao.class, UserDaoImpl.class);
	DepartmentDao deptDao = (DepartmentDao) BeanFactory.getFactory().getObject(DepartmentDao.class,
			DepartmentDaoImpl.class);
	NotificationDao notDao = (NotificationDao) BeanFactory.getFactory().getObject(NotificationDao.class,
			NotificationDaoImpl.class);

	// For Logging
	private static final Logger log = LogManager.getLogger(RequestServiceImpl.class);

	// For verifying strings and objects as not null
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

				// If the maximum that can be reimbursed is not less than or equal to zero, the
				// reimburse is set to the original reimburse amount
				if (reimburseMax <= 0) {
					reimburseMax = reimburseAmount;
				}
				log.debug("Maximum amount that can be reimbursed: " + reimburseMax);

				request = new ReimbursementRequest(username, firstName, lastName, deptName, name, startDate, startTime,
						location, description, cost, gradingFormat, type);
				request.setId(UUID.randomUUID());
				request.setReimburseAmount(reimburseMax);

				// If the date that it is starting is less than two weeks away, then need to set
				// isUrgent to true
				LocalDate twoWeeks = LocalDate.now().plus(Period.of(0, 0, 14));
				request.setIsUrgent(startDate.isBefore(twoWeeks));

				// Set the supervisor approval to the user's supervisor and start the deadline
				request.getSupervisorApproval().setUsername(user.getSupervisorUsername());
				request.startDeadline();
				request.getSupervisorApproval().setStatus(ApprovalStatus.AWAITING);
				log.debug("Deadline set to " + request.getDeadline());
				// Add the request to the database and create a notification for the supervisor
				reqDao.createRequest(request);
				notDao.createNotification(new Notification(user.getSupervisorUsername(), request.getId(),
						"An employee has requested reimbursement!"));
				// Make sure the user's pending amount is changed and the request added to their
				// requests list.
				user.getRequests().add(request.getId());
				user.alterPendingBalance(reimburseMax);
				log.debug("User's new pending balance: " + user.getPendingBalance());

				// Update the user in the database
				userDao.updateUser(user);

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
		if (VERIFIER.verifyNotNull(request, status) && (request.getStatus().equals(RequestStatus.ACTIVE)
				|| request.getStatus().equals(RequestStatus.APPROVED))) {
			// Put all of the approvals into an array
			Approval[] approvals = request.getApprovalArray();

			for (int i = 0; i < approvals.length; i++) {
				Approval currentApproval = approvals[i];
				log.debug("Current Approval being evaluated: " + currentApproval);

				// If the approval has already been approved, move to the next one
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
				log.debug("The next Approval to be evaluated: " + nextApproval);
				currentApproval.setStatus(status);
				log.debug("Current Approval status changed to " + currentApproval.getStatus());
				if (status.equals(ApprovalStatus.DENIED)) {

					// If the reason is blank or null, return null
					if (!VERIFIER.verifyStrings(reason)) {
						break;
					}
					request.setStatus(RequestStatus.DENIED);
					request.setReason(reason);
					User user = userDao.getUser(request.getUsername());
					user.alterPendingBalance(request.getReimburseAmount() * -1.0);
					reqDao.updateRequest(request);
					userDao.updateUser(user);
					notDao.createNotification(new Notification(request.getUsername(), request.getId(),
							"Your request has been denied. Reason: " + reason));
					notDao.deleteNotification(currentApproval.getUsername(), request.getId());
					retRequest = request;
					break;
				}

				// On the supervisor approval
				if (i == Request.SUPERVISOR_INDEX) {
					// Need to check if the supervisor is also the department head
					Department dept = deptDao.getDepartment(request.getDeptName());

					nextApproval.setUsername(dept.getDeptHeadUsername());
					nextApproval.setStatus(ApprovalStatus.AWAITING);

					// If the supervisor is also a department head, bypass the department approval
					if (dept.getDeptHeadUsername().equals(request.getSupervisorApproval().getUsername())) {
						status = ApprovalStatus.BYPASSED;
						log.debug("Status changed to " + status);
						continue;
					}

				}
				// On the BenCo approval
				else if (i == Request.BENCO_INDEX) {
					// If the grading format is a presentation, the supervisor will be set to the
					// final approval
					// else, the BenCo will be set to the final approval
					if (request.getGradingFormat().getFormat().equals(Format.PRESENTATION)) {
						nextApproval.setUsername(request.getSupervisorApproval().getUsername());
					} else {
						nextApproval.setUsername(request.getBenCoApproval().getUsername());
					}
					request.setStatus(RequestStatus.APPROVED);
					notDao.createNotification(new Notification(request.getUsername(), request.getId(),
							"Your request has been approved. Please enter your final submission when ready."));
				}
				// On final approval
				else if (i == Request.FINAL_INDEX) {
					// Set the user's balances and set the request to approved
					request.setStatus(RequestStatus.AWARDED);
					User user = userDao.getUser(request.getUsername());

					// If the reimburse amount was never changed, set it to the current reimburse
					// amount
					if (request.getFinalReimburseAmount() == null || request.getFinalReimburseAmount() == 0.0) {
						request.setFinalReimburseAmount(request.getReimburseAmount());
					}
					user.alterPendingBalance(request.getFinalReimburseAmount() * -1.0);
					user.alterAwardedBalance(request.getFinalReimburseAmount());
					notDao.createNotification(new Notification(request.getUsername(), request.getId(),
							"Your request has been finalized and the amount will be awarded."));
					userDao.updateUser(user);
				}

				// If there is another approval (Only when it is on final approval)
				if (nextApproval != null) {
					nextApproval.setStatus(ApprovalStatus.AWAITING);
					request.startDeadline();
					log.debug("New request deadline: " + request.getDeadline());
					// If the next approval is not benCo or final
					if (nextApproval.getUsername() != null && i != Request.BENCO_INDEX) {
						notDao.createNotification(new Notification(nextApproval.getUsername(), request.getId(),
								"A new request needs your approval"));
					}
				}
				// Clear out the notifications of the current approver
				notDao.deleteNotification(currentApproval.getUsername(), request.getId());
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

	@Override
	public void updateRequest(Request request) {
		if (request != null) {
			reqDao.updateRequest(request);
		}
	}

	@Override
	public void cancelRequest(Request request) {
		if (request == null) {
			return;
		}
		// Get the user
		User user = userDao.getUser(request.getUsername());

		// Cancel the request
		request.setStatus(RequestStatus.CANCELLED);

		// Need to change the user's pending balance
		// First need to check and see if finalReimburseAmount is set to see which
		// reimburse amount is part of the pending balance
		Double reimburse = (request.getFinalReimburseAmount() != null && request.getFinalReimburseAmount() > 0.0)
				? request.getFinalReimburseAmount()
				: request.getReimburseAmount();
		log.debug("Amount the user is losing from pendingBalance: " + reimburse);
		// Subtract the amount from the user
		user.alterPendingBalance(reimburse * -1.0);
		log.debug("User's new pending balance: " + user.getPendingBalance());
		userDao.updateUser(user);
		reqDao.updateRequest(request);
	}

	@Override
	public Request changeReimburseAmount(Request request, Double reimburse, String reason) {
		Request retRequest = null;
		// Make sure all the arguments are good
		if (VERIFIER.verifyNotNull(request, reimburse) && reimburse > 0.0 && VERIFIER.verifyStrings(reason)) {

			// Set all the request fields
			request.setFinalReimburseAmount(reimburse);
			request.setFinalReimburseAmountReason(reason);
			request.setNeedsEmployeeReview(true);
			request.setEmployeeAgrees(false);

			// Change the user's pending balance
			User user = userDao.getUser(request.getUsername());
			user.alterPendingBalance(reimburse - request.getReimburseAmount());
			log.debug("User's new pending balance: " + user.getPendingBalance());
			userDao.updateUser(user);
			notDao.createNotification(new Notification(request.getUsername(), request.getId(),
					"Your request reimburse amount has changed and needs your approval."));

			// Update the request and return it
			reqDao.updateRequest(request);
			retRequest = request;
		}
		return retRequest;
	}

	@Override
	public void changeEmployeeAgrees(Request request, Boolean employeeAgrees) {
		if (VERIFIER.verifyNotNull(request, employeeAgrees)) {
			// Set the request
			request.setEmployeeAgrees(employeeAgrees);
			request.setNeedsEmployeeReview(false);
			log.debug("Employee review set to: " + request.getEmployeeAgrees());

			// If the employee doens't agree, cancel the request and change their pending
			// balance
			if (!employeeAgrees) {
				request.getBenCoApproval().setStatus(ApprovalStatus.UNASSIGNED);
				cancelRequest(request);
				notDao.deleteNotification(request.getBenCoApproval().getUsername(), request.getId());
			} else { // Else, update the request
				reqDao.updateRequest(request);
				notDao.createNotification(new Notification(request.getBenCoApproval().getUsername(), request.getId(),
						"The employee agrees with the reimbursement change."));
			}

		}
	}

	@Override
	public void addFinalGrade(Request request, String grade) {
		if (VERIFIER.verifyNotNull(request) && VERIFIER.verifyStrings(grade)) {

			// Set the grade and make sure it is passing
			request.setFinalGrade(grade);
			request.setIsPassing(request.getGradingFormat().isPassing(grade));
			log.debug("Final grade: " + request.getFinalGrade() + ". Is passing: " + request.getIsPassing());
			reqDao.updateRequest(request);
			notDao.createNotification(new Notification(request.getFinalApproval().getUsername(), request.getId(),
					"Final approval is ready on request"));
		}
	}

	@Override
	public void autoApprove() {
		// Check the database to see if there are any requests needed to auto approve
		List<Request> requests = reqDao.getExpiredRequests();

		// If the list isn't null or empty, that means an active request has a past-due
		// deadline
		if (requests != null && !requests.isEmpty()) {

			// Make sure the controller doesn't approve the request
			synchronized (RequestService.APPROVAL_LOCK) {

				for (Request request : requests) {
					log.debug("Request being auto approved: " + request);

					// If the supervisor or dept head approval was needed, will just auto approve
					// those
					if (ApprovalStatus.AWAITING.equals(request.getSupervisorApproval().getStatus())
							|| ApprovalStatus.AWAITING.equals(request.getDeptHeadApproval().getStatus())) {
						changeApprovalStatus(request, ApprovalStatus.AUTO_APPROVED, null);
					}

					// If the benCo or finalApproval user didn't approve, will need to message benCo
					// supervisor
					else if (ApprovalStatus.AWAITING.equals(request.getBenCoApproval().getStatus())) {
						request.startDeadline();
						reqDao.updateRequest(request);
						String benCoSupervisorUsername = deptDao.getDepartment("Benefits").getDeptHeadUsername();
						notDao.createNotification(new Notification(benCoSupervisorUsername, request.getId(),
								"This request needs further approval."));
					}
					// If none of the requests are waiting, the request is most likely bad
					else {
						throw new IllegalApprovalAttemptException("Auto-approval attempt on Request.");
					}
				}
			}

		}
	}
}
