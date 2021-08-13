package com.revature.services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.util.Arrays;
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
	private RequestDao reqDao;
	private UserDao userDao;
	private DepartmentDao deptDao;
	private NotificationDao notDao;

	// For Logging
	private static final Logger log = LogManager.getLogger(RequestServiceImpl.class);

	// For verifying strings and objects as not null
	private static final Verifier VERIFIER = new Verifier();

	/**
	 * This is the RequestServiceImpl used for other classes
	 */
	public RequestServiceImpl() {
		reqDao = (RequestDao) BeanFactory.getFactory().getObject(RequestDao.class, RequestDaoImpl.class);
		userDao = (UserDao) BeanFactory.getFactory().getObject(UserDao.class, UserDaoImpl.class);
		deptDao = (DepartmentDao) BeanFactory.getFactory().getObject(DepartmentDao.class, DepartmentDaoImpl.class);
		notDao = (NotificationDao) BeanFactory.getFactory().getObject(NotificationDao.class, NotificationDaoImpl.class);
	}

	/**
	 * This is the RequestServiceImpl used for testing
	 */
	public RequestServiceImpl(RequestDao reqDao, UserDao userDao, DepartmentDao deptDao, NotificationDao notDao) {
		this.reqDao = reqDao;
		this.userDao = userDao;
		this.deptDao = deptDao;
		this.notDao = notDao;
	}

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

				// Set the reimburse amount to the cost multiplied by the max percent covered
				Double reimburseAmount = cost * type.getPercent();
				log.debug("Maximum amount allowed to be reimbursed for request: " + reimburseAmount);

				// Set the maximum amount that can normally be reimbursed to the max request
				// minus the user's total balance used
				Double reimburseMax = Request.MAX_REIMBURSEMENT - user.getTotalBalance();
				log.debug("Maximum amount the user is alloted normally: " + reimburseMax);

				// If the amount that can be reimbursed for the request is greater than the
				// user's reimburse balance, set to the user's reimburse balance, otherwise set
				// to request reimburse amount
				reimburseMax = (reimburseAmount > reimburseMax) ? reimburseMax : reimburseAmount;

				// If the maximum that can be reimbursed is not less than or equal to zero, the
				// reimburse is set to the original reimburse amount so BenCo can approve if
				// they want to
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
				log.debug("Request is marked as urgent: " + request.getIsUrgent());

				// Set the supervisor approval to the user's supervisor and start the deadline
				request.getSupervisorApproval().setUsername(user.getSupervisorUsername());
				request.startDeadline();
				request.getSupervisorApproval().setStatus(ApprovalStatus.AWAITING);
				log.debug("Deadline set to " + request.getDeadline());

				// Add the request to the database and create a notification for the supervisor
				reqDao.createRequest(request);
				notDao.createNotification(new Notification(user.getSupervisorUsername(), request.getId(),
						"An employee has requested reimbursement."));

				// Make sure the user's pending amount is changed and the request added to their
				// requests list.
				user.getRequests().add(request.getId());
				user.alterPendingBalance(reimburseMax);
				log.debug("User's new pending balance: " + user.getPendingBalance());

				// Update the user in the database
				userDao.updateUser(user);

			}

		}
		return request;
	}

	@Override
	public Request changeApprovalStatus(Request request, ApprovalStatus status, String reason, Integer index) {

		// The request that will be returned
		Request retRequest = null;
		// Verify the objects (except reason) are not null and that the index is in the
		// correct range
		if (VERIFIER.verifyNotNull(request, status, index) && index >= Request.SUPERVISOR_INDEX
				&& index <= Request.FINAL_INDEX) {

			// Put all of the approvals into an array
			Approval[] approvals = request.approvalArray();
			log.debug("Approvals array: " + Arrays.toString(approvals));

			Approval currentApproval = approvals[index];
			log.debug("Current Approval being evaluated: " + currentApproval);

			// If the approval somehow made it to a denied or unassigned approval (this
			// should not happen, need to throw exception
			if (!currentApproval.getStatus().equals(ApprovalStatus.AWAITING)) {
				throw new IllegalApprovalAttemptException(
						"The approval that is being evaluated is " + currentApproval.getStatus());
			}

			// Set the next approval needed after the current one. If this is the fianl
			// approval, this will be null
			Approval nextApproval = (index + 1 < approvals.length) ? approvals[index + 1] : null;
			log.debug("The next Approval to be evaluated: " + nextApproval);

			if (status.equals(ApprovalStatus.DENIED)) {
				// If the reason is blank or null, return null
				if (!VERIFIER.verifyStrings(reason)) {
					return null;
				}

				// Set the status of this approval
				currentApproval.setStatus(status);
				log.debug("Current Approval status changed to " + currentApproval.getStatus());

				// Deny the request and set the reason
				request.setStatus(RequestStatus.DENIED);
				request.setReason(reason);
				log.debug("Reason for the denial: " + reason);

				// Get the user and subtract the reimburse amount from
				User user = userDao.getUser(request.getUsername());
				Double reimburseAmount = (request.getFinalReimburseChanged() ? request.getFinalReimburseAmount()
						: request.getReimburseAmount());
				user.alterPendingBalance(reimburseAmount * -1.0);
				log.debug("User's new pending balance: " + user.getPendingBalance());

				// Update the user and send them a notification telling them the request has
				// been denied
				userDao.updateUser(user);
				notDao.createNotification(new Notification(request.getUsername(), request.getId(),
						"Your request has been denied. Reason: " + reason));

			} else { // The request is being approved by this user

				// Set the status
				currentApproval.setStatus(status);
				log.debug("Current Approval status changed to " + currentApproval.getStatus());

				// On the supervisor approval
				if (index == Request.SUPERVISOR_INDEX) {
					// Need to check if the supervisor is also the department head
					Department dept = deptDao.getDepartment(request.getDeptName());

					// Set the next approval to the department head
					nextApproval.setUsername(dept.getDeptHeadUsername());
					nextApproval.setStatus(ApprovalStatus.AWAITING);

					// If the supervisor is also a department head, bypass the department approval
					if (dept.getDeptHeadUsername().equals(request.getSupervisorApproval().getUsername())) {

						// Perform a recursive call to bypass the department head approval and return
						// the result of that call
						return changeApprovalStatus(request, ApprovalStatus.BYPASSED, reason, Request.DEPT_HEAD_INDEX);
					}

				}
				// On the BenCo approval
				else if (index == Request.BENCO_INDEX) {

					// If the grading format is a presentation, the supervisor will be set to the
					// final approval
					// else, the BenCo will be set to the final approval
					if (Format.PRESENTATION.equals(request.getGradingFormat().getFormat())) {
						nextApproval.setUsername(request.getSupervisorApproval().getUsername());
					} else {
						nextApproval.setUsername(request.getBenCoApproval().getUsername());
					}
					log.debug("Final approval username: " + nextApproval.getUsername());

					// Set the request status to approved and send a notification to the request
					// creator
					request.setStatus(RequestStatus.APPROVED);
					notDao.createNotification(new Notification(request.getUsername(), request.getId(),
							"Your request has been approved. Please enter your final submission when ready."));
				}

				// On final approval
				else if (index == Request.FINAL_INDEX) {
					// Set the user's balances and set the request to approved
					request.setStatus(RequestStatus.AWARDED);
					User user = userDao.getUser(request.getUsername());

					// If the reimburse amount was never changed, set it to the current reimburse
					// amount
					if (!request.getFinalReimburseChanged()) {
						request.setFinalReimburseAmount(request.getReimburseAmount());
					}

					// Remove the reimburse amount from pending and put it in awarded
					user.alterPendingBalance(request.getFinalReimburseAmount() * -1.0);
					log.debug("User's new pending balance: " + user.getPendingBalance());
					user.alterAwardedBalance(request.getFinalReimburseAmount());
					log.debug("User's new awarded balance: " + user.getAwardedBalance());

					// Send a notification to the user and update the user
					notDao.createNotification(new Notification(request.getUsername(), request.getId(),
							"Your request has been finalized and the amount will be awarded."));
					userDao.updateUser(user);
				}

				// If there is another approval to do (That means the approval is not final)
				if (nextApproval != null) {
					// Set the status and restart the deadline
					nextApproval.setStatus(ApprovalStatus.AWAITING);
					request.startDeadline();
					log.debug("New request deadline: " + request.getDeadline());
					// If the next approval is not benCo or final
					if (nextApproval.getUsername() != null && index != Request.BENCO_INDEX) {
						notDao.createNotification(new Notification(nextApproval.getUsername(), request.getId(),
								"A new request needs your approval"));
					}
				}

			}
			// Clear out the notifications of the current approver
			notDao.deleteNotification(currentApproval.getUsername(), request.getId());

			// Update the request and set the return request to the current request
			reqDao.updateRequest(request);
			retRequest = request;
		}

		return retRequest;
	}

	@Override
	public Request getRequest(UUID id) {

		// The request that will be returned.
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
		// Make sure the request isn't null then update the request
		if (request != null) {
			reqDao.updateRequest(request);
		}
	}

	@Override
	public void cancelRequest(Request request) {

		// Make sure the request isn't null
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
		Double reimburse = (request.getFinalReimburseChanged()) ? request.getFinalReimburseAmount()
				: request.getReimburseAmount();
		log.debug("Amount the user is losing from pendingBalance: " + reimburse);
		// Subtract the amount from the user
		user.alterPendingBalance(reimburse * -1.0);
		log.debug("User's new pending balance: " + user.getPendingBalance());

		// Update the user and the request
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

			// If the employee doesn't agree, cancel the request and change their pending
			// balance
			if (!employeeAgrees) {
				request.getBenCoApproval().setStatus(ApprovalStatus.UNASSIGNED);
				cancelRequest(request);

				// Delete the notifications that the BenCo had for this request
				notDao.deleteNotification(request.getBenCoApproval().getUsername(), request.getId());
			} else { // Else, update the request and send a notification to the BenCo
				reqDao.updateRequest(request);
				notDao.createNotification(new Notification(request.getBenCoApproval().getUsername(), request.getId(),
						"The employee agrees with the reimbursement change."));
			}

		}
	}

	@Override
	public void addFinalGrade(Request request, String grade) {
		if (request != null && VERIFIER.verifyStrings(grade)) {

			// Set the grade and make sure it is passing
			request.setFinalGrade(grade);
			request.setIsPassing(request.getGradingFormat().isPassing(grade));
			log.debug("Final grade: " + request.getFinalGrade() + ". Is passing: " + request.getIsPassing());

			// Update the request in the database and send a new notification to the final
			// approver
			reqDao.updateRequest(request);
			notDao.createNotification(new Notification(request.getFinalApproval().getUsername(), request.getId(),
					"Final approval is ready on request"));
		}
	}

	@Override
	public void autoApprove() {

		// Make sure other requests aren't being approved at this time
		// Check the database to see if there are any requests needed to auto approve
		List<Request> requests = reqDao.getExpiredRequests();

		// If the list isn't null or empty, that means an active request has a past-due
		// deadline
		if (requests != null && !requests.isEmpty()) {

			// Make sure the controller doesn't get the request while it is getting the
			// request

			for (Request request : requests) {
				log.debug("Request being auto approved: " + request);

				// If the supervisor or dept head approval was needed, will just auto approve
				// those
				if (ApprovalStatus.AWAITING.equals(request.getSupervisorApproval().getStatus())
						|| ApprovalStatus.AWAITING.equals(request.getDeptHeadApproval().getStatus())) {
					// Figure out which one needs to be auto-approved and get the correct index
					Integer index = ApprovalStatus.AWAITING.equals(request.getSupervisorApproval().getStatus())
							? Request.SUPERVISOR_INDEX
							: Request.DEPT_HEAD_INDEX;

					// Update the approval
					changeApprovalStatus(request, ApprovalStatus.AUTO_APPROVED, null, index);
					log.debug("Approval status changed to " + request.approvalArray()[index].getStatus());
				}

				// If the benCo or finalApproval user didn't approve, will need to message benCo
				// supervisor
				else if (ApprovalStatus.AWAITING.equals(request.getBenCoApproval().getStatus())) {
					// Reset the deadline and update the request
					request.startDeadline();
					reqDao.updateRequest(request);

					// Get the BenCo Supervisor and send them a notification
					String benCoSupervisorUsername = deptDao.getDepartment("Benefits").getDeptHeadUsername();
					log.debug("BenCo supervisor's username: " + benCoSupervisorUsername);
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
