package com.revature.controllers;

import java.util.UUID;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revature.beans.Approval;
import com.revature.beans.ApprovalStatus;
import com.revature.beans.ReimbursementRequest;
import com.revature.beans.Request;
import com.revature.beans.RequestStatus;
import com.revature.beans.User;
import com.revature.exceptions.IllegalApprovalAttemptException;
import com.revature.factory.BeanFactory;
import com.revature.factory.TraceLog;
import com.revature.services.RequestService;
import com.revature.services.RequestServiceImpl;
import com.revature.util.S3Util;

import io.javalin.http.Context;

@TraceLog
public class RequestControllerImpl implements RequestController {
	RequestService reqService = (RequestService) BeanFactory.getFactory().getObject(RequestService.class,
			RequestServiceImpl.class);
	private static Logger log = LogManager.getLogger(RequestControllerImpl.class);

	private static final String[] FILETYPES = { "pdf", "jpg", "png", "txt", "doc" };
	private static final S3Util s3Instance = S3Util.getInstance();

	@Override
	public void createRequest(Context ctx) {
		User loggedUser = ctx.sessionAttribute("loggedUser");

		// Make sure the user is logged in
		if (loggedUser == null) {
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

		// If the request was not found
		if (request == null) {
			// TODO: verify status code
			ctx.status(400);
			ctx.html("The Reimbursement Request sent was incorrectly entered.");
		} else { // Otherwise, return the request
			ctx.status(201);
			ctx.json(request);
		}
	}

	@Override
	public void changeApprovalStatus(Context ctx) {
		User loggedUser = ctx.sessionAttribute("loggedUser");

		// Make sure the user is logged in
		if (loggedUser == null) {
			ctx.status(401);
			return;
		}

		// Get the request
		Request request = reqService.getRequest(UUID.fromString(ctx.pathParam("requestId")));
		log.debug("Request from requestId: " + request);
		Request approval = ctx.bodyAsClass(ReimbursementRequest.class);
		log.debug("Request from body: " + approval);

		// If the request was not found
		if (request == null) {
			ctx.status(404);
			ctx.html("No Request with that ID found");
			return;
		}

		if (approval == null || approval.getSupervisorApproval() == null
				|| (!approval.getSupervisorApproval().getStatus().equals(ApprovalStatus.APPROVED)
						&& !approval.getSupervisorApproval().getStatus().equals(ApprovalStatus.DENIED))
				|| !request.getStatus().equals(RequestStatus.ACTIVE)) {
			// TODO check status codes
			ctx.status(406);
			ctx.html("This request cannot be approved or denied any further.");
			return;
		}

		// Verify the user is allowed to change the approval status
		Approval[] approvals = request.getApprovalArray();

		for (int i = 0; i < approvals.length; i++) {
			Approval currentApproval = approvals[i];

			// If the approval has been approved or bypassed already, move to the next one
			if (currentApproval.getStatus().equals(ApprovalStatus.APPROVED)
					|| currentApproval.getStatus().equals(ApprovalStatus.AUTO_APPROVED)
					|| currentApproval.getStatus().equals(ApprovalStatus.BYPASSED)) {
				continue;

			}
			// If the current approval is unassigned, need to send back a 403
			if (currentApproval.getStatus().equals(ApprovalStatus.UNASSIGNED)) {
				ctx.status(403);
				return;
			}
			if ((i == Request.BENCO_INDEX && loggedUser.getDepartmentName().equals("Benefits"))
					|| currentApproval.getUsername().equals(loggedUser.getUsername())) {
				// If it is on BenCoApproval, set the BenCoApproval username to the current user
				if (i == Request.BENCO_INDEX) {
					currentApproval.setUsername(loggedUser.getUsername());
					// If the final reimburseamount was set and is different then the actual
					// reimburseamount
					if (approval.getFinalReimburseAmount() != null
							&& approval.getFinalReimburseAmount() != request.getReimburseAmount()) {
						// If the user did not provide a reason for why they are changing the reimburse
						// amount
						if (approval.getFinalReimburseAmountReason() == null
								|| approval.getFinalReimburseAmountReason().isBlank()) {
							// TODO check status code
							ctx.status(400);
							ctx.html("If changing the reimburse amount, need a reason");
							return;
						}
						// TODO handle this case
						ctx.status(501);
						ctx.html("Doesn't handle reimburse changes yet.");
						return;
					}
				}
				try {
					request = reqService.changeApprovalStatus(request, approval.getSupervisorApproval().getStatus(),
							approval.getReason());
					// If the request returned null, then the request was bad
					if (request == null) {
						ctx.status(400);
						ctx.html("Approval is invalid.");
					} else {
						ctx.json(request);
					}

					return;
				} catch (IllegalApprovalAttemptException e) {
					ctx.status(500);
					ctx.html("Server Error");
					return;
				}
			}
		}

		ctx.status(403);
	}

	@Override
	public void getRequest(Context ctx) {
		User loggedUser = ctx.sessionAttribute("loggedUser");

		// Make sure the user is logged in
		if (loggedUser == null) {
			ctx.status(401);
			return;
		}
		UUID requestId = UUID.fromString(ctx.pathParam("requestId"));
		Request request = reqService.getRequest(requestId);
		log.debug("Request from the requestId" + request);
		if (request == null) {
			ctx.status(404);
			ctx.html("No request with that ID");
			return;
		}

		// If the user is not authorized to see the final grade, set the return request
		// to null
		// NOTE: This will not be saved, this is just for returning it
		if (!loggedUser.getUsername().equals(request.getUsername())
				&& !loggedUser.getUsername().equals(request.getFinalApproval().getUsername())) {
			request.setFinalGrade(null);
			request.setIsPassing(null);
		}

		ctx.json(request);
	}

	@Override
	public void cancelRequest(Context ctx) {
		User loggedUser = ctx.sessionAttribute("loggedUser");
		Request request = reqService.getRequest(UUID.fromString(ctx.pathParam("requestId")));

		// Make sure the Request was found
		if (request == null) {
			ctx.status(404);
			ctx.html("No Request with that ID");
			return;
		}

		// Make sure the user is logged in and owns the Request
		if (loggedUser == null || !loggedUser.getUsername().equals(request.getUsername())) {
			ctx.status(403);
			return;
		}

		if (request.getStatus().equals(RequestStatus.ACTIVE)) {
			reqService.cancelRequest(request);
			ctx.status(204);
		} else {
			ctx.status(406);
			ctx.html("The request cannot be cancelled");
		}

	}

	@Override
	public void uploadExtraFile(Context ctx) {
		User loggedUser = ctx.sessionAttribute("loggedUser");
		String filetype = ctx.header("filetype");
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
		if (!request.getStatus().equals(RequestStatus.ACTIVE)
				|| !request.getSupervisorApproval().getStatus().equals(ApprovalStatus.AWAITING)) {
			ctx.status(403);
			return;
		}

		// If the user is not the owner of the request
		if (!loggedUser.getUsername().equals(request.getUsername())) {
			ctx.status(403);
			return;
		}

		// Generate the key and upload to the bucket
		String key = request.getId() + "/files/" + request.getFileURIs().size() + "." + filetype;
		s3Instance.uploadToBucket(key, ctx.bodyAsBytes());
		
		//Add the key to the request, update database, and return request
		request.getFileURIs().add(key);
		reqService.updateRequest(request);
		ctx.json(request);
	}

	@Override
	public void uploadMessageFile(Context ctx) {
		User loggedUser = ctx.sessionAttribute("loggedUser");
		String filetype = ctx.header("filetype");
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

		// If the request is ready to be processed by BenCo or cancelled by the
		// user
		if (!request.getStatus().equals(RequestStatus.ACTIVE)
				|| request.getBenCoApproval().getStatus().equals(ApprovalStatus.AWAITING)) {
			ctx.status(403);
			return;
		}

		// If the user is not the owner of the request
		if (!loggedUser.getUsername().equals(request.getUsername())) {
			ctx.status(403);
			return;
		}

		// Generate the key and upload to the bucket
		String key = request.getId() + "/messages/" + request.getFileURIs().size() + "." + filetype;
		s3Instance.uploadToBucket(key, ctx.bodyAsBytes());
		request.getApprovalMsgsURIs().add(key);
		
		// Bypass the request if the supervisor or department head are awaiting
		if (request.getSupervisorApproval().getStatus().equals(ApprovalStatus.AWAITING)
				|| request.getDeptHeadApproval().getStatus().equals(ApprovalStatus.AWAITING)) {
			reqService.changeApprovalStatus(request, ApprovalStatus.BYPASSED, null);
		}
		//Return request
		ctx.json(request);
			
	}
}
