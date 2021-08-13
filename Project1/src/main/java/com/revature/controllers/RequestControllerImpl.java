package com.revature.controllers;

import java.io.InputStream;
import java.util.UUID;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revature.beans.Approval;
import com.revature.beans.ApprovalStatus;
import com.revature.beans.Format;
import com.revature.beans.ReimbursementRequest;
import com.revature.beans.Request;
import com.revature.beans.RequestStatus;
import com.revature.beans.User;
import com.revature.beans.UserType;
import com.revature.exceptions.IllegalApprovalAttemptException;
import com.revature.factory.BeanFactory;
import com.revature.factory.TraceLog;
import com.revature.services.RequestService;
import com.revature.services.RequestServiceImpl;
import com.revature.util.S3Util;
import com.revature.util.Verifier;

import io.javalin.http.Context;

@TraceLog
public class RequestControllerImpl implements RequestController {
	private RequestService reqService = (RequestService) BeanFactory.getFactory().getObject(RequestService.class,
			RequestServiceImpl.class);
	// Log
	private static Logger log = LogManager.getLogger(RequestControllerImpl.class);

	// Used to verify string and objects
	private static final Verifier VERIFIER = new Verifier();

	// Filetypes that can be uploaded with requests (Not including .msg and .pptx
	// for emails and presentations respectively)
	private static final String[] FILETYPES = { "pdf", "jpg", "png", "txt", "doc" };

	// The S3 instance for uploading and downloading files
	private static final S3Util S3_INSTANCE = S3Util.getInstance();

	@Override
	public void createRequest(Context ctx) {
		User loggedUser = ctx.sessionAttribute("loggedUser");

		// Make sure the user is logged in
		if (loggedUser == null) {
			log.info("Unauthorized request creation attempted");
			ctx.status(401);
			return;
		}

		// Get the request from the body
		Request request = ctx.bodyAsClass(ReimbursementRequest.class);
		log.debug("Request from the body: " + request);

		// Create the request.
		request = reqService.createRequest(loggedUser.getUsername(), request.getFirstName(), request.getLastName(),
				request.getDeptName(), request.getName(), request.getStartDate(), request.getStartTime(),
				request.getLocation(), request.getDescription(), request.getCost(), request.getGradingFormat(),
				request.getType());

		// If the request returned had an issue
		if (request == null) {
			log.debug("The request was incorrect in the request body");
			ctx.status(400);
			ctx.html("The Reimbursement Request sent was incorrectly entered.");
		} else { // Otherwise, return the request
			log.debug("Request Created");
			ctx.status(201);
			ctx.json(request);
		}
	}

	@Override
	public void changeApprovalStatus(Context ctx) {
		User loggedUser = ctx.sessionAttribute("loggedUser");

		// Make sure the user is logged in
		if (loggedUser == null) {
			log.info("Unauthorized attempt at changeApprovalStatus");
			ctx.status(401);
			return;
		}

		Request request = reqService.getRequest(UUID.fromString(ctx.pathParam("requestId")));
		log.debug("Request from requestId: " + request);

		// Get the approval status from the body
		Request approval = ctx.bodyAsClass(ReimbursementRequest.class);
		log.debug("Request from body: " + approval);
		// If the request was not found
		if (request == null) {
			ctx.status(404);
			ctx.html("No Request with that ID found");
			return;
		}

		// If the approval in the body is null or if the user is trying to do a status
		// that isn't APPROVED or DENIED
		if (approval == null || approval.getSupervisorApproval() == null
				|| (!ApprovalStatus.APPROVED.equals(approval.getSupervisorApproval().getStatus())
						&& !ApprovalStatus.DENIED.equals(approval.getSupervisorApproval().getStatus()))) {
			log.debug("Request did not have status of APPROVED or DENIED");
			ctx.status(400);
			ctx.html("This request needs a status of APPROVED or DENIED.");
			return;
		}

		// If the request isn't active, approved, or if the request needs employee
		// review, then can't do a Approval yet
		if ((!RequestStatus.ACTIVE.equals(request.getStatus()) && !RequestStatus.APPROVED.equals(request.getStatus()))
				|| request.getNeedsEmployeeReview()) {
			log.debug("Request either isn't active, approved, or needs employee review");
			ctx.status(409);
			ctx.html("The request cannot be approved further at this time");
			return;
		}

		// Verify the user is allowed to change the approval status
		Approval[] approvals = request.approvalArray();
		for (int i = 0; i < approvals.length; i++) {
			Approval currentApproval = approvals[i];
			log.debug("Current approval being evaluated: " + currentApproval);
			// If the approval has been approved, auto approved, or bypassed already, move
			// to the next one
			if (ApprovalStatus.APPROVED.equals(currentApproval.getStatus())
					|| ApprovalStatus.AUTO_APPROVED.equals(currentApproval.getStatus())
					|| ApprovalStatus.BYPASSED.equals(currentApproval.getStatus())) {
				continue;
			}

			// If the current approval is unassigned, this means the user is not assigned to
			// this status and need to send back a 403
			if (currentApproval.getStatus().equals(ApprovalStatus.UNASSIGNED)) {
				log.debug("User is not authorized to approve request");
				ctx.status(403);
				return;
			}

			// If the user is part of Benefits and is trying to access the BenCo approval or
			// the user's username is set to the currentApproval
			if ((i == Request.BENCO_INDEX && loggedUser.getDepartmentName().equals("Benefits"))
					|| loggedUser.getUsername().equals(currentApproval.getUsername())) {

				// If it is on BenCoApproval, set the BenCoApproval username to the current user
				if (i == Request.BENCO_INDEX) {
					currentApproval.setUsername(loggedUser.getUsername());
					log.debug("BenCoApproval set to " + currentApproval.getUsername());
				}

				// If evaluating the final request and the final grade or final presentation
				// have not been set yet
				if (i == Request.FINAL_INDEX && !VERIFIER.verifyStrings(request.getFinalGrade())
						&& !VERIFIER.verifyStrings(request.getPresFileName())) {
					log.debug("Final grade/presentation has not been uploaded yet");
					ctx.status(409);
					ctx.html("Waiting for the user to get the final grade");
					return;
				}
				try {

					// Perform the approval request
					request = reqService.changeApprovalStatus(request, approval.getSupervisorApproval().getStatus(),
							approval.getReason(), i);

					/// If the request returned null, then there is an issue with the Approval
					if (request == null) {
						log.debug("Approval was invalid: " + approval.getSupervisorApproval());
						ctx.status(400);
						ctx.html("Approval is invalid.");
					} else { // Otherwise, the request was approved and send it back
						ctx.json(request);
					}
					return;
					// IllegalApprovalAttemptException means an Approval was attempted that
					// shouldn't have been, and that is on the server, so send back 500
				} catch (IllegalApprovalAttemptException e) {
					ctx.status(500);
					ctx.html("Server Error");
					return;
				}
			}
		}

		// This status shouldn't be called, but if it is, there is an issue with the
		// server
		log.warn("changeApprovalStatus made it to end of method with request: " + request);
		ctx.status(500);
		ctx.html("The request had no more approvals to check");
	}

	@Override
	public void getRequest(Context ctx) {
		User loggedUser = ctx.sessionAttribute("loggedUser");

		// Make sure the user is logged in
		if (loggedUser == null) {
			log.info("Unauthorized attempt at getRequest");
			ctx.status(401);
			return;
		}
		UUID requestId = UUID.fromString(ctx.pathParam("requestId"));
		Request request = reqService.getRequest(requestId);
		log.debug("Request from the requestId" + request);
		// Make sure the request exists
		if (request == null) {
			ctx.status(404);
			ctx.html("No request with that ID");
			return;
		}

		// If the user is not the owner or is not a supervisor or BenCo, then return a
		// 403
		if (!loggedUser.getUsername().equals(request.getUsername()) && UserType.EMPLOYEE.equals(loggedUser.getType())) {
			log.info(loggedUser.getUsername() + " is forbidden from seeing this request");
			ctx.status(403);
			return;
		}
		// If the user is not authorized to see the final grade, set the final grade
		// and presentation file name to null
		// NOTE: This will not be saved, this is just for returning it
		if (!loggedUser.getUsername().equals(request.getUsername())
				&& !loggedUser.getUsername().equals(request.getFinalApproval().getUsername())) {
			log.info("User is not allowed to see final grade or presentation");
			request.setFinalGrade(null);
			request.setIsPassing(null);
			request.setPresFileName(null);

		}
		log.debug("Request being send back: " + request);
		ctx.json(request);
	}

	@Override
	public void cancelRequest(Context ctx) {
		User loggedUser = ctx.sessionAttribute("loggedUser");

		// Make sure the user is logged in
		if (loggedUser == null) {
			log.info("User is not authorized to cancel request");
			ctx.status(401);
			return;
		}

		Request request = reqService.getRequest(UUID.fromString(ctx.pathParam("requestId")));
		log.debug("Request for that ID: " + request);

		// Make sure the Request was found
		if (request == null) {
			ctx.status(404);
			ctx.html("No Request with that ID");
			return;
		}

		// Make sure the user is logged in and owns the Request
		if (!loggedUser.getUsername().equals(request.getUsername())) {
			log.info(loggedUser.getUsername() + " is forbidden from cancelling this request");
			ctx.status(403);
			return;
		}

		// If the Request is ACTIVE or APPROVED, then the user can cancel.
		if (RequestStatus.ACTIVE.equals(request.getStatus()) || RequestStatus.APPROVED.equals(request.getStatus())) {
			reqService.cancelRequest(request);
			ctx.status(204);
		} else { // Otherwise, send back a 409
			ctx.status(409);
			ctx.html("The request cannot be cancelled");
		}

	}

	@Override
	public void uploadExtraFile(Context ctx) {
		User loggedUser = ctx.sessionAttribute("loggedUser");
		String filetype = ctx.header("filetype");
		log.debug("Filetype from header: " + filetype);

		// Make sure the user is logged in
		if (loggedUser == null) {
			ctx.status(401);
			return;
		}

		// Make sure the filetype is correct
		Boolean isValidFiletype = Stream.of(FILETYPES).anyMatch((type) -> type.equals(filetype));
		if (!isValidFiletype) {
			ctx.status(400);
			ctx.html("Incorret filetype entered");
			return;
		}

		// Get the request from the UUID in the path
		UUID requestId = UUID.fromString(ctx.pathParam("requestId"));
		Request request = reqService.getRequest(requestId);
		log.debug("Request from the requestId" + request);

		// If no request was found with that id
		if (request == null) {
			ctx.status(404);
			ctx.html("No request with that ID");
			return;
		}

		// If the request has already been processed by a supervisor or cancelled by the
		// user
		if (!RequestStatus.ACTIVE.equals(request.getStatus())
				|| !ApprovalStatus.AWAITING.equals(request.getSupervisorApproval().getStatus())) {
			ctx.status(403);
			return;
		}

		// If the user is not the owner of the request
		if (!loggedUser.getUsername().equals(request.getUsername())) {
			ctx.status(403);
			return;
		}

		// Generate the key and upload to the bucket
		String key = request.getId() + "/files/file" + request.getFileURIs().size() + "." + filetype;
		S3_INSTANCE.uploadToBucket(key, ctx.bodyAsBytes());

		// Add the key to the request, update database, and return request
		request.getFileURIs().add(key);
		reqService.updateRequest(request);
		ctx.json(request);
	}

	@Override
	public void uploadMessageFile(Context ctx) {
		User loggedUser = ctx.sessionAttribute("loggedUser");
		String filetype = ctx.header("filetype");
		log.debug("Filetype from header: " + filetype);

		// The only filetype allowed to send for approval messages
		final String MSG = "msg";
		// Make sure the user is logged in
		if (loggedUser == null) {
			ctx.status(401);
			return;
		}

		// Make sure the filetype is correct
		if (!MSG.equals(filetype)) {
			ctx.status(400);
			ctx.html("Incorret filetype entered");
			return;
		}

		// Get the request from the UUID in the path
		UUID requestId = UUID.fromString(ctx.pathParam("requestId"));
		Request request = reqService.getRequest(requestId);
		log.debug("Request from the requestId" + request);

		// If no request was found with that id
		if (request == null) {
			ctx.status(404);
			ctx.html("No request with that ID");
			return;
		}

		// If the user is not the owner of the request
		if (!loggedUser.getUsername().equals(request.getUsername())) {
			log.info(loggedUser.getUsername() + " is not the owner of the request");
			ctx.status(403);
			return;
		}

		// If the request has already been processed by the supervisor or cancelled by
		// the user
		if (!RequestStatus.ACTIVE.equals(request.getStatus())
				|| !ApprovalStatus.AWAITING.equals(request.getSupervisorApproval().getStatus())) {
			ctx.status(403);
			return;
		}

		// Generate the key and upload to the bucket
		String key = request.getId() + "/messages/approvalEmail." + filetype;
		S3_INSTANCE.uploadToBucket(key, ctx.bodyAsBytes());
		request.setApprovalMsgURI(key);

		// Bypass the request
		reqService.changeApprovalStatus(request, ApprovalStatus.BYPASSED, null, Request.SUPERVISOR_INDEX);

		// Return request
		ctx.json(request);

	}

	@Override
	public void uploadPresentation(Context ctx) {
		User loggedUser = ctx.sessionAttribute("loggedUser");
		String filetype = ctx.header("filetype");
		log.debug("Filetype from header: " + filetype);

		// The only filetype allowed for presentations
		final String PPT = "pptx";

		// Make sure the user is logged in
		if (loggedUser == null) {
			ctx.status(401);
			return;
		}

		// Make sure the filetype is correct
		if (!PPT.equals(filetype)) {
			ctx.status(400);
			ctx.html("Incorret filetype entered");
			return;
		}

		// Get the request from the UUID in the path
		UUID requestId = UUID.fromString(ctx.pathParam("requestId"));
		Request request = reqService.getRequest(requestId);
		log.debug("Request from the requestId" + request);

		// If no request was found with that id
		if (request == null) {
			ctx.status(404);
			ctx.html("No request with that ID");
			return;
		}

		// If the user is not the owner of the request
		if (!loggedUser.getUsername().equals(request.getUsername())) {
			log.info(loggedUser.getUsername() + " is forbidden from seeing this request");
			ctx.status(403);
			return;
		}

		// If the request has not been approved or if the final approval is not ready or
		// if the request doesn't need a presentation, send back a 406
		if (!RequestStatus.APPROVED.equals(request.getStatus())
				|| !ApprovalStatus.AWAITING.equals(request.getFinalApproval().getStatus())
				|| !Format.PRESENTATION.equals(request.getGradingFormat().getFormat())) {
			ctx.status(409);
			ctx.html("The request cannot accept a final grade.");
			return;
		}

		// Generate the key and upload to the bucket
		String key = request.getId() + "/presentations/presentation." + filetype;
		S3_INSTANCE.uploadToBucket(key, ctx.bodyAsBytes());
		request.setPresFileName(key);
		log.debug("Presentation File URI: " + request.getPresFileName());
		// Update and return request
		reqService.addFinalGrade(request, "true");
		log.debug("Final grade on request: " + request.getFinalGrade());
		;
		ctx.json(request);

	}

	public void getExtraFile(Context ctx) {
		User loggedUser = ctx.sessionAttribute("loggedUser");
		// Make sure the user is logged in
		if (loggedUser == null) {
			log.info("Unauthorized attempt to get a file");
			ctx.status(401);
			return;
		}

		Request request = reqService.getRequest(UUID.fromString(ctx.pathParam("requestId")));
		log.debug("Request from the requestId in path: " + request);
		Integer index = Integer.parseInt(ctx.pathParam("index"));
		log.debug("The index from the path: " + index);

		// If the request was not found
		if (request == null) {
			ctx.status(404);
			ctx.html("The request wasn't found");
			return;
		}

		// Make sure the index being looked up isn't null or out of range
		if (index == null || index < 0 || index >= request.getFileURIs().size()) {
			ctx.status(400);
			ctx.html("The file needs to have a valid position.");
			return;
		}
		String key = request.getFileURIs().get(index);
		// Read the file and send it back
		try {
			InputStream file = S3_INSTANCE.getObject(key);
			ctx.result(file);
		} catch (Exception e) { // Error reading file. Will send back a 500
			ctx.status(500);
		}
	}

	public void getMessage(Context ctx) {
		User loggedUser = ctx.sessionAttribute("loggedUser");
		// Make sure the user is logged in
		if (loggedUser == null) {
			log.info("Unauthorized attempt to get approval message");
			ctx.status(401);
			return;
		}

		Request request = reqService.getRequest(UUID.fromString(ctx.pathParam("requestId")));
		log.debug("Request from the requestId in path: " + request);
		// If the request was not found
		if (request == null || request.getApprovalMsgURI() == null) {
			ctx.status(404);
			ctx.html("The file wasn't found");
			return;
		}
		String key = request.getApprovalMsgURI();

		// Attempt to read the file
		try {
			InputStream file = S3_INSTANCE.getObject(key);
			ctx.result(file);
		} catch (Exception e) { // If exception, return a 500
			ctx.status(500);
		}
	}

	public void getPresentation(Context ctx) {
		User loggedUser = ctx.sessionAttribute("loggedUser");
		// Make sure the user is logged in
		if (loggedUser == null) {
			log.info("Unauthorized attempt to getPresentation");
			ctx.status(401);
			return;
		}

		Request request = reqService.getRequest(UUID.fromString(ctx.pathParam("requestId")));
		log.debug("Request from the requestId in path: " + request);

		// If the request was not found
		if (request == null || request.getPresFileName() == null) {
			ctx.status(404);
			ctx.html("The presentation wasn't found");
			return;
		}

		// If the user is not the request creator and is not the final approver
		if (!loggedUser.getUsername().equals(request.getUsername())
				&& !loggedUser.getUsername().equals(request.getFinalApproval().getUsername())) {
			log.info(loggedUser.getUsername() + " is forbidden from seeing the presentation");
			ctx.status(403);
			return;
		}
		String key = request.getPresFileName();

		// Attempt to read the file
		try {
			InputStream file = S3_INSTANCE.getObject(key);
			ctx.result(file);
		} catch (Exception e) { // If exception, return a 500
			ctx.status(500);
		}
	}

	@Override
	public void changeReimburseAmount(Context ctx) {
		User loggedUser = ctx.sessionAttribute("loggedUser");

		// Make sure the user is logged in and is a BenCo
		if (loggedUser == null || !UserType.BENEFITS_COORDINATOR.equals(loggedUser.getType())) {
			log.info("Unauthorized attempt to change reimbursement");
			ctx.status(403);
			return;
		}

		Request request = reqService.getRequest(UUID.fromString(ctx.pathParam("requestId")));
		log.debug("Request found from requestId path param: " + request);
		Request approval = ctx.bodyAsClass(ReimbursementRequest.class);
		log.debug("Request from the body: " + approval);

		// If the request was not found
		if (request == null) {
			ctx.status(404);
			ctx.html("The request was not found");
			return;
		}

		// Make sure the request is awaiting BenCo approval
		if (!ApprovalStatus.AWAITING.equals(request.getBenCoApproval().getStatus())) {
			ctx.status(403);
			return;
		}

		// If the request final reimburse amount has been set already
		if (request.getFinalReimburseChanged()) {
			ctx.status(409);
			ctx.html("The reimburse amount has been finalized already.");
			return;
		}
		// If the final reimburseamount was set and is different then the actual
		// reimburseamount
		if (approval.getFinalReimburseAmount() != null && approval.getFinalReimburseAmount() > 0.0) {

			// If the final amount is the same as the reimburse amount, no change needed
			if (approval.getFinalReimburseAmount() == request.getReimburseAmount()) {
				ctx.json(request);
				return;
			}
			// If the user did not provide a reason for why they are changing the reimburse
			// amount, need to send back a 400
			if (approval.getFinalReimburseAmountReason() == null
					|| approval.getFinalReimburseAmountReason().isBlank()) {

				ctx.status(400);
				ctx.html("If changing the reimburse amount, need a reason");
				return;
			}
			// Set the final reimburse amount
			request.getBenCoApproval().setUsername(loggedUser.getUsername());
			request = reqService.changeReimburseAmount(request, approval.getFinalReimburseAmount(),
					approval.getFinalReimburseAmountReason());

			// If the request does not equal null, return the request
			// else, return a 500 status code
			if (request != null) {
				ctx.json(request);
			} else {
				ctx.status(500);
			}
		} else { // The reimburse amount was not set correctly
			ctx.status(400);
			ctx.html("The reimburse amount was not set correctly");
		}

	}

	@Override
	public void finalReimburseCheck(Context ctx) {
		User loggedUser = ctx.sessionAttribute("loggedUser");

		// Make sure the user is logged in
		if (loggedUser == null) {
			ctx.status(401);
			return;
		}

		// Get the request
		Request request = reqService.getRequest(UUID.fromString(ctx.pathParam("requestId")));
		log.debug("Request from the requestId path param: " + request);

		// If the request wasn't found
		if (request == null) {
			ctx.status(404);
			ctx.html("No Request Found");
			return;
		}
		// If the user is not the owner of the request or the request does not need
		// their review
		if (!loggedUser.getUsername().equals(request.getUsername()) || !request.getNeedsEmployeeReview()) {
			log.info(loggedUser.getUsername() + " cannot agree to this request");
			ctx.status(403);
			return;
		}

		// Get the review. If it is null or the getEmployeeAgrees is not a part of it
		Request review = ctx.bodyAsClass(ReimbursementRequest.class);
		log.debug("Request from the body: " + review);

		// If the employeeAgrees was not set, return a 400
		if (review.getEmployeeAgrees() == null) {
			ctx.status(400);
			ctx.html("Need to know if the employee agrees or not");
			return;
		}

		reqService.changeEmployeeAgrees(request, review.getEmployeeAgrees());
		log.debug("Employee agrees status: " + request.getEmployeeAgrees());
		ctx.status(204);
	}

	@Override
	public void putFinalGrade(Context ctx) {
		User loggedUser = ctx.sessionAttribute("loggedUser");

		// Make sure the user is logged in
		if (loggedUser == null) {
			log.info("Unauthorized attempt to upload final grade");
			ctx.status(401);
			return;
		}

		Request request = reqService.getRequest(UUID.fromString(ctx.pathParam("requestId")));
		log.debug("Request from the requestId path param: " + request);

		// If the request wasn't found
		if (request == null) {
			ctx.status(404);
			ctx.html("No request found");
			return;
		}

		// If the user does not own the request, return a 403
		if (!loggedUser.getUsername().equals(request.getUsername())) {
			log.info(loggedUser.getUsername() + " is forbidden from uploading grades to this request");
			ctx.status(403);
			return;
		}
		// If the request is looking for a presentation or is not ready to accept a
		// grade, return a 409
		if (Format.PRESENTATION.equals(request.getGradingFormat().getFormat())
				|| !(RequestStatus.APPROVED.equals(request.getStatus())
						&& ApprovalStatus.AWAITING.equals(request.getFinalApproval().getStatus()))) {
			ctx.status(409);
			ctx.html("This request can't take the grade.");
			return;
		}

		Request grade = ctx.bodyAsClass(ReimbursementRequest.class);
		log.debug("Request from the body: " + grade);

		// If the grade was null
		if (grade.getFinalGrade() == null) {
			ctx.status(400);
			ctx.html("The grade was not entered correctly");
			return;
		}

		// If the final grade was not set
		if (request.getFinalGrade() != null) {
			ctx.status(409);
			ctx.html("The final grade has already been sent");
			return;
		}

		// Add the final grade and return a 204
		reqService.addFinalGrade(request, grade.getFinalGrade());
		log.debug("Request final grade is now: " + request.getFinalGrade());
		ctx.json(request);
	}
}
