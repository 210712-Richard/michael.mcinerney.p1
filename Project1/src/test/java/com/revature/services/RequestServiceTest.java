package com.revature.services;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

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
import com.revature.beans.UserType;
import com.revature.data.DepartmentDao;
import com.revature.data.RequestDao;
import com.revature.data.UserDao;
import com.revature.exceptions.IllegalApprovalAttemptException;
import com.revature.util.MockitoHelper;

public class RequestServiceTest {
	private RequestService service = null;
	private static Request request = null;
	private RequestDao reqDao = null;
	private UserDao userDao = null;
	private DepartmentDao deptDao = null;

	private User user = null;
	private User supervisor = null;
	private User deptHead = null;
	private User benCo = null;

	private Department dept = null;

	private static MockitoHelper mock = null;

	@BeforeAll
	public static void beforeAll() {
		mock = new MockitoHelper();

	}

	@BeforeEach
	public void beforeTest() {
		service = new RequestServiceImpl();

		request = new ReimbursementRequest();
		request.setId(UUID.fromString("ddd9e879-52d3-47ad-a1b6-87a94cbb321d"));
		request.setUsername("Tester");
		request.setName("Service Certification");
		request.setFirstName("Test");
		request.setLastName("User");
		request.setDeptName("Test");
		request.setStartDate(LocalDate.now().plus(Period.of(0, 1, 0)));
		request.setStartTime(LocalTime.now());
		request.setLocation("101 Test Dr. Test, VA 99999");
		request.setDescription("A Service course");
		request.setCost(200.00);
		request.setGradingFormat(new GradingFormat(Format.LETTER));
		request.setType(EventType.CERTIFICATION);

		user = new User("Tester", "TestPass", "user@test.com", "Test", "User", UserType.EMPLOYEE, "Test", "TestSuper");
		supervisor = new User("TestSuper", "TestPass", "user@test.com", "Test", "Super", UserType.SUPERVISOR, "Test",
				"TestSuper");
		deptHead = new User("TestHead", "TestPass", "user@test.com", "Test", "Head", UserType.SUPERVISOR, "Test",
				"TestCEO");
		benCo = new User("TestBenCo", "TestPass", "user@test.com", "Test", "BenCo", UserType.BENEFITS_COORDINATOR,
				"Benefits", "TestCEO");

		dept = new Department("Test", "TestHead");

		reqDao = (RequestDao) mock.setPrivateMock(service, "reqDao", RequestDao.class);
		userDao = (UserDao) mock.setPrivateMock(service, "userDao", UserDao.class);
		deptDao = (DepartmentDao) mock.setPrivateMock(service, "deptDao", DepartmentDao.class);

		Mockito.when(userDao.getUser(user.getUsername())).thenReturn(user);
		Mockito.when(userDao.getUser(supervisor.getUsername())).thenReturn(supervisor);
		Mockito.when(userDao.getUser(deptHead.getUsername())).thenReturn(deptHead);
		Mockito.when(userDao.getUser(benCo.getUsername())).thenReturn(benCo);
		Mockito.when(reqDao.getRequest(request.getId())).thenReturn(request);
		Mockito.when(deptDao.getDepartment(dept.getName())).thenReturn(dept);
	}

	@Test
	public void testCreateRequestValid() {
		// Set up the argument captor
		ArgumentCaptor<Request> captor = ArgumentCaptor.forClass(Request.class);

		// Call the method
		Request newRequest = service.createRequest(request.getUsername(), request.getFirstName(), request.getLastName(),
				request.getDeptName(), request.getName(), request.getStartDate(), request.getStartTime(),
				request.getLocation(), request.getDescription(), request.getCost(), request.getGradingFormat(),
				request.getType());

		// Make sure all the fields are the same and that ID is not null.
		assertNotNull(newRequest.getId(), "Assert that a new ID was made for the request.");

		// Ensure all the fields passed in are the same
		assertEquals(request.getUsername(), newRequest.getUsername(), "Assert that the username is set.");
		assertEquals(request.getFirstName(), newRequest.getFirstName(), "Assert that the firstName is set.");
		assertEquals(request.getLastName(), newRequest.getLastName(), "Assert that the lastName is set.");
		assertEquals(request.getDeptName(), newRequest.getDeptName(), "Assert that the deptName is set.");
		assertEquals(request.getName(), newRequest.getName(), "Assert that the name is set.");
		assertEquals(request.getStartDate(), newRequest.getStartDate(), "Assert that the startDate is set.");
		assertEquals(request.getStartTime(), newRequest.getStartTime(), "Assert that the startTime is set.");
		assertEquals(request.getLocation(), newRequest.getLocation(), "Assert that the location is set.");
		assertEquals(request.getDescription(), newRequest.getDescription(), "Assert that the description is set.");
		assertEquals(request.getCost(), newRequest.getCost(), "Assert that the cost is set.");
		assertEquals(request.getGradingFormat(), newRequest.getGradingFormat(),
				"Assert that the gradingFormat is set.");
		assertEquals(request.getType(), newRequest.getType(), "Assert that the type is set.");
		assertEquals(request.getCost() * request.getType().getPercent(), newRequest.getReimburseAmount(),
				"Assert that the reimburse amount was set");

		// Make sure supervisorUsername is set to user's supervisorUsername
		assertEquals(user.getSupervisorUsername(), newRequest.getSupervisorApproval().getUsername(),
				"Assert that the supervisor that needs to do the approval is the same as the User's supervisor");

		// Make sure deadline is not the default value
		assertTrue(Request.PLACEHOLDER.isBefore(newRequest.getDeadline()),
				"Assert that the deadline changed away from the placeholder.");

		// Make sure supervisor status is awaiting
		assertEquals(ApprovalStatus.AWAITING, newRequest.getSupervisorApproval().getStatus(),
				"Assert that the supervisor that needs to do the approval is the same as the User's supervisor");

		// Make sure the method was called and passed in the correct argument
		Mockito.verify(reqDao).createRequest(captor.capture());
		assertEquals(newRequest, captor.getValue(),
				"Assert that the arguments passed to the dao are the same returned.");

		// If the user has a pending and awarded balance that is less than the max
		// amount alloted but less than the cost,
		// it should still create the request, but with a different cost.
		user.setAwardedBalance(800.00);
		user.setPendingBalance(150.00);
		Double expectedCost = Request.MAX_REIMBURSEMENT - user.getPendingBalance() - user.getAwardedBalance();

		newRequest = service.createRequest(request.getUsername(), request.getFirstName(), request.getLastName(),
				request.getDeptName(), request.getName(), request.getStartDate(), request.getStartTime(),
				request.getLocation(), request.getDescription(), request.getCost(), request.getGradingFormat(),
				request.getType());

		// Make sure all the fields are the same and that ID is not null.
		assertNotNull(newRequest.getId(), "Assert that a new ID was made for the request.");

		// Ensure all the fields passed in are the same
		assertEquals(request.getUsername(), newRequest.getUsername(), "Assert that the username is set.");
		assertEquals(request.getFirstName(), newRequest.getFirstName(), "Assert that the firstName is set.");
		assertEquals(request.getLastName(), newRequest.getLastName(), "Assert that the lastName is set.");
		assertEquals(request.getDeptName(), newRequest.getDeptName(), "Assert that the deptName is set.");
		assertEquals(request.getName(), newRequest.getName(), "Assert that the name is set.");
		assertEquals(request.getStartDate(), newRequest.getStartDate(), "Assert that the startDate is set.");
		assertEquals(request.getStartTime(), newRequest.getStartTime(), "Assert that the startTime is set.");
		assertEquals(request.getLocation(), newRequest.getLocation(), "Assert that the location is set.");
		assertEquals(request.getDescription(), newRequest.getDescription(), "Assert that the description is set.");
		assertEquals(request.getCost(), newRequest.getCost(), "Assert that the cost is set.");
		assertEquals(request.getGradingFormat(), newRequest.getGradingFormat(),
				"Assert that the gradingFormat is set.");
		assertEquals(request.getType(), newRequest.getType(), "Assert that the type is set.");
		assertEquals(expectedCost, newRequest.getReimburseAmount(), "Assert that the reimburse amount is set");
	}

	@Test
	public void testCreateRequestInvalid() {

		// Blank or null username returns a null
		Request newRequest = service.createRequest(null, request.getFirstName(), request.getLastName(),
				request.getDeptName(), request.getName(), request.getStartDate(), request.getStartTime(),
				request.getLocation(), request.getDescription(), request.getCost(), request.getGradingFormat(),
				request.getType());
		assertNull("Assert that a null username returns null", newRequest);

		newRequest = service.createRequest(" ", request.getFirstName(), request.getLastName(), request.getDeptName(),
				request.getName(), request.getStartDate(), request.getStartTime(), request.getLocation(),
				request.getDescription(), request.getCost(), request.getGradingFormat(), request.getType());
		assertNull("Assert that a blank username returns null", newRequest);

		// Blank or null firstName returns a null
		newRequest = service.createRequest(request.getUsername(), null, request.getLastName(), request.getDeptName(),
				request.getName(), request.getStartDate(), request.getStartTime(), request.getLocation(),
				request.getDescription(), request.getCost(), request.getGradingFormat(), request.getType());
		assertNull("Assert that a null firstName returns null", newRequest);

		newRequest = service.createRequest(request.getUsername(), "", request.getLastName(), request.getDeptName(),
				request.getName(), request.getStartDate(), request.getStartTime(), request.getLocation(),
				request.getDescription(), request.getCost(), request.getGradingFormat(), request.getType());
		assertNull("Assert that a blank firstName returns null", newRequest);

		// Blank or null lastName returns a null
		newRequest = service.createRequest(request.getUsername(), request.getFirstName(), null, request.getDeptName(),
				request.getName(), request.getStartDate(), request.getStartTime(), request.getLocation(),
				request.getDescription(), request.getCost(), request.getGradingFormat(), request.getType());
		assertNull("Assert that a null lastName returns null", newRequest);

		newRequest = service.createRequest(request.getUsername(), request.getFirstName(), " ", request.getDeptName(),
				request.getName(), request.getStartDate(), request.getStartTime(), request.getLocation(),
				request.getDescription(), request.getCost(), request.getGradingFormat(), request.getType());
		assertNull("Assert that a blank lastName returns null", newRequest);

		// Blank or null deptName returns a null
		newRequest = service.createRequest(request.getUsername(), request.getFirstName(), request.getLastName(), null,
				request.getName(), request.getStartDate(), request.getStartTime(), request.getLocation(),
				request.getDescription(), request.getCost(), request.getGradingFormat(), request.getType());
		assertNull("Assert that a null deptName returns null", newRequest);

		newRequest = service.createRequest(request.getUsername(), request.getFirstName(), request.getLastName(), " ",
				request.getName(), request.getStartDate(), request.getStartTime(), request.getLocation(),
				request.getDescription(), request.getCost(), request.getGradingFormat(), request.getType());
		assertNull("Assert that a blank deptName returns null", newRequest);

		// Blank or null name returns a null
		newRequest = service.createRequest(request.getUsername(), request.getFirstName(), request.getLastName(),
				request.getDeptName(), null, request.getStartDate(), request.getStartTime(), request.getLocation(),
				request.getDescription(), request.getCost(), request.getGradingFormat(), request.getType());
		assertNull("Assert that a null name returns null", newRequest);

		newRequest = service.createRequest(request.getUsername(), request.getFirstName(), request.getLastName(),
				request.getDeptName(), " ", request.getStartDate(), request.getStartTime(), request.getLocation(),
				request.getDescription(), request.getCost(), request.getGradingFormat(), request.getType());
		assertNull("Assert that a blank name returns null", newRequest);

		// Late or null startDate returns a null
		newRequest = service.createRequest(request.getUsername(), request.getFirstName(), request.getLastName(),
				request.getDeptName(), request.getName(), null, request.getStartTime(), request.getLocation(),
				request.getDescription(), request.getCost(), request.getGradingFormat(), request.getType());
		assertNull("Assert that a null startDate returns null", newRequest);

		newRequest = service.createRequest(request.getUsername(), request.getFirstName(), request.getLastName(),
				request.getDeptName(), request.getName(), LocalDate.now().minus(Period.of(0, 0, 1)),
				request.getStartTime(), request.getLocation(), request.getDescription(), request.getCost(),
				request.getGradingFormat(), request.getType());
		assertNull("Assert that a late startDate returns null", newRequest);

		// Null startTime returns a null
		newRequest = service.createRequest(request.getUsername(), request.getFirstName(), request.getLastName(),
				request.getDeptName(), request.getName(), request.getStartDate(), null, request.getLocation(),
				request.getDescription(), request.getCost(), request.getGradingFormat(), request.getType());
		assertNull("Assert that a null startTime returns null", newRequest);

		// Blank or null location returns a null
		newRequest = service.createRequest(request.getUsername(), request.getFirstName(), request.getLastName(),
				request.getDeptName(), request.getName(), request.getStartDate(), request.getStartTime(), null,
				request.getDescription(), request.getCost(), request.getGradingFormat(), request.getType());
		assertNull("Assert that a null location returns null", newRequest);

		newRequest = service.createRequest(request.getUsername(), request.getFirstName(), request.getLastName(),
				request.getDeptName(), request.getName(), request.getStartDate(), request.getStartTime(), " ",
				request.getDescription(), request.getCost(), request.getGradingFormat(), request.getType());
		assertNull("Assert that a blank location returns null", newRequest);

		// Blank or null description returns a null
		newRequest = service.createRequest(request.getUsername(), request.getFirstName(), request.getLastName(),
				request.getDeptName(), request.getName(), request.getStartDate(), request.getStartTime(),
				request.getLocation(), null, request.getCost(), request.getGradingFormat(), request.getType());
		assertNull("Assert that a null description returns null", newRequest);

		newRequest = service.createRequest(request.getUsername(), request.getFirstName(), request.getLastName(),
				request.getDeptName(), request.getName(), request.getStartDate(), request.getStartTime(),
				request.getLocation(), " ", request.getCost(), request.getGradingFormat(), request.getType());
		assertNull("Assert that a blank description returns null", newRequest);

		// Null or not-possible cost returns a null
		newRequest = service.createRequest(request.getUsername(), request.getFirstName(), request.getLastName(),
				request.getDeptName(), request.getName(), request.getStartDate(), request.getStartTime(),
				request.getLocation(), request.getDescription(), null, request.getGradingFormat(), request.getType());
		assertNull("Assert that a null cost returns null", newRequest);

		newRequest = service.createRequest(request.getUsername(), request.getFirstName(), request.getLastName(),
				request.getDeptName(), request.getName(), request.getStartDate(), request.getStartTime(),
				request.getLocation(), request.getDescription(), 0.00, request.getGradingFormat(), request.getType());
		assertNull("Assert that a zero cost returns null", newRequest);

		newRequest = service.createRequest(request.getUsername(), request.getFirstName(), request.getLastName(),
				request.getDeptName(), request.getName(), request.getStartDate(), request.getStartTime(),
				request.getLocation(), request.getDescription(), -1.00, request.getGradingFormat(), request.getType());
		assertNull("Assert that a negative cost returns null", newRequest);

		// Null gradingFormat returns a null
		newRequest = service.createRequest(request.getUsername(), request.getFirstName(), request.getLastName(),
				request.getDeptName(), request.getName(), request.getStartDate(), request.getStartTime(),
				request.getLocation(), request.getDescription(), request.getCost(), null, request.getType());
		assertNull("Assert that a null gradingFormat returns null", newRequest);

		// Null type returns a null
		newRequest = service.createRequest(request.getUsername(), request.getFirstName(), request.getLastName(),
				request.getDeptName(), request.getName(), request.getStartDate(), request.getStartTime(),
				request.getLocation(), request.getDescription(), request.getCost(), request.getGradingFormat(), null);
		assertNull("Assert that a null type returns null", newRequest);

	}

	@Test
	public void testChangeApprovalStatusSupervisorApproves() {
		// Set the pending balance to the request cost
		request.getSupervisorApproval().setUsername(user.getSupervisorUsername());
		request.getSupervisorApproval().setStatus(ApprovalStatus.AWAITING);
		request.setReimburseAmount(request.getCost() * request.getType().getPercent());
		user.setPendingBalance(request.getReimburseAmount());

		ArgumentCaptor<Request> reqCaptor = ArgumentCaptor.forClass(Request.class);
		ArgumentCaptor<String> deptNameCaptor = ArgumentCaptor.forClass(String.class);

		Request retRequest = service.changeApprovalStatus(request, ApprovalStatus.APPROVED, "This is a test");

		assertEquals(request, retRequest, "Assert that the request returned is the same request");
		assertEquals(ApprovalStatus.APPROVED, request.getSupervisorApproval().getStatus(),
				"Assert that the supervisor approved the request.");
		assertEquals(ApprovalStatus.AWAITING, request.getDeptHeadApproval().getStatus(),
				"Assert that the dept head approval is now awaiting.");
		assertEquals(request.getDeptHeadApproval().getUsername(), dept.getDeptHeadUsername(),
				"Assert that the dept head is set to do the approval");
		assertNotEquals(request.getDeadline(), Request.PLACEHOLDER,
				"Assert that the time limit has changed for the Request from the placeholder");

		Mockito.verify(reqDao).updateRequest(reqCaptor.capture());
		Mockito.verify(deptDao).getDepartment(deptNameCaptor.capture());

		assertEquals(request, reqCaptor.getValue(),
				"Assert that the request passed into updateRequest is the same request");
		assertEquals(dept.getName(), deptNameCaptor.getValue(),
				"Assert that the department name passed into getDepartment is the same");

	}

	@Test
	public void testChangeApprovalStatusSupervisorDenied() {

		request.getSupervisorApproval().setUsername(user.getSupervisorUsername());
		request.getSupervisorApproval().setStatus(ApprovalStatus.AWAITING);
		request.setReimburseAmount(request.getCost() * request.getType().getPercent());
		user.setPendingBalance(request.getReimburseAmount());

		ArgumentCaptor<String> getUserCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<User> updateUserCaptor = ArgumentCaptor.forClass(User.class);
		ArgumentCaptor<Request> reqCaptor = ArgumentCaptor.forClass(Request.class);

		Request retRequest = service.changeApprovalStatus(request, ApprovalStatus.DENIED, "This is a test");

		assertEquals(request, retRequest, "Assert that the request returned is the same request");
		assertEquals(ApprovalStatus.DENIED, request.getSupervisorApproval().getStatus(),
				"Assert that the supervisor approved the request.");
		assertEquals(RequestStatus.DENIED, request.getStatus(), "Assert that the request was denied.");
		assertEquals(0.0, user.getPendingBalance(), "Assert that the pending balance was set back to zero.");

		Mockito.verify(userDao).getUser(getUserCaptor.capture());
		Mockito.verify(userDao).updateUser(updateUserCaptor.capture());
		Mockito.verify(reqDao).updateRequest(reqCaptor.capture());

		assertEquals(request, reqCaptor.getValue(),
				"Assert that the request passed into updateRequest is the same request");

		assertEquals(user.getUsername(), getUserCaptor.getValue(),
				"Assert that the user's username is passed in to get user");
		assertEquals(user, updateUserCaptor.getValue(), "Assert that the the user is updated.");
	}

	@Test
	public void testChangeApprovalStatusDeptHeadApproves() {
		// Set the pending balance to the request cost
		request.getSupervisorApproval().setStatus(ApprovalStatus.APPROVED);
		request.getDeptHeadApproval().setUsername(dept.getDeptHeadUsername());
		request.getDeptHeadApproval().setStatus(ApprovalStatus.AWAITING);
		request.setReimburseAmount(request.getCost() * request.getType().getPercent());

		ArgumentCaptor<Request> reqCaptor = ArgumentCaptor.forClass(Request.class);

		Request retRequest = service.changeApprovalStatus(request, ApprovalStatus.APPROVED, null);

		assertEquals(request, retRequest, "Assert that the request returned is the same request");
		assertEquals(ApprovalStatus.APPROVED, request.getDeptHeadApproval().getStatus(),
				"Assert that the supervisor approved the request.");
		assertEquals(ApprovalStatus.AWAITING, request.getBenCoApproval().getStatus(),
				"Assert that the BenCo approval is now in awaiting.");
		assertNotEquals(request.getDeadline(), Request.PLACEHOLDER,
				"Assert that the time limit has changed for the Request from the placeholder");

		Mockito.verify(reqDao).updateRequest(reqCaptor.capture());

		assertEquals(request, reqCaptor.getValue(),
				"Assert that the request passed into updateRequest is the same request");

	}

	@Test
	public void testChangeApprovalStatusDeptHeadDenied() {
		// Set the pending balance to the request cost
		request.getSupervisorApproval().setStatus(ApprovalStatus.APPROVED);
		request.getDeptHeadApproval().setUsername(dept.getDeptHeadUsername());
		request.getDeptHeadApproval().setStatus(ApprovalStatus.AWAITING);
		request.setReimburseAmount(request.getCost() * request.getType().getPercent());
		user.setPendingBalance(request.getReimburseAmount());

		ArgumentCaptor<String> getUserCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<User> updateUserCaptor = ArgumentCaptor.forClass(User.class);
		ArgumentCaptor<Request> reqCaptor = ArgumentCaptor.forClass(Request.class);

		Request retRequest = service.changeApprovalStatus(request, ApprovalStatus.DENIED, "This is a test");

		assertEquals(request, retRequest, "Assert that the request returned is the same request");
		assertEquals(ApprovalStatus.DENIED, request.getDeptHeadApproval().getStatus(),
				"Assert that the supervisor approved the request.");
		assertEquals(RequestStatus.DENIED, request.getStatus(), "Assert that the request was denied.");
		assertEquals(0.0, user.getPendingBalance(), "Assert that the pending balance was set back to zero.");

		Mockito.verify(userDao).getUser(getUserCaptor.capture());
		Mockito.verify(userDao).updateUser(updateUserCaptor.capture());
		Mockito.verify(reqDao).updateRequest(reqCaptor.capture());

		assertEquals(request, reqCaptor.getValue(),
				"Assert that the request passed into updateRequest is the same request");
		assertEquals(user.getUsername(), getUserCaptor.getValue(),
				"Assert that the user's username is passed in to get user");
		assertEquals(user, updateUserCaptor.getValue(), "Assert that the the user is updated.");
	}

	@Test
	public void testChangeApprovalStatusBenCoApprovedFinalToBenCo() {
		// Set the pending balance to the request cost
		request.getSupervisorApproval().setStatus(ApprovalStatus.APPROVED);
		request.getDeptHeadApproval().setStatus(ApprovalStatus.APPROVED);
		request.getBenCoApproval().setUsername(benCo.getUsername());
		request.getBenCoApproval().setStatus(ApprovalStatus.AWAITING);
		request.setReimburseAmount(request.getCost() * request.getType().getPercent());

		ArgumentCaptor<Request> reqCaptor = ArgumentCaptor.forClass(Request.class);

		Request retRequest = service.changeApprovalStatus(request, ApprovalStatus.APPROVED, null);

		assertEquals(request, retRequest, "Assert that the request returned is the same request");
		assertEquals(ApprovalStatus.APPROVED, request.getBenCoApproval().getStatus(),
				"Assert that the supervisor approved the request.");
		assertEquals(ApprovalStatus.AWAITING, request.getFinalApproval().getStatus(),
				"Assert that the BenCo approval is now in awaiting.");
		assertEquals(request.getBenCoApproval().getUsername(), request.getFinalApproval().getUsername(),
				"Assert that the finalApproval username is set to the supervisor's username");
		assertNotEquals(request.getDeadline(), Request.PLACEHOLDER,
				"Assert that the time limit has changed for the Request from the placeholder");

		Mockito.verify(reqDao).updateRequest(reqCaptor.capture());

		assertEquals(request, reqCaptor.getValue(),
				"Assert that the request passed into updateRequest is the same request");
	}

	@Test
	public void testChangeApprovalStatusBenCoApprovedFinalToSupervisor() {
		// Set the pending balance to the request cost
		request.getSupervisorApproval().setStatus(ApprovalStatus.APPROVED);
		request.getDeptHeadApproval().setStatus(ApprovalStatus.APPROVED);
		request.getBenCoApproval().setUsername(benCo.getUsername());
		request.getBenCoApproval().setStatus(ApprovalStatus.AWAITING);
		request.setReimburseAmount(request.getCost() * request.getType().getPercent());
		request.setGradingFormat(new GradingFormat(Format.PRESENTATION));

		ArgumentCaptor<Request> reqCaptor = ArgumentCaptor.forClass(Request.class);

		Request retRequest = service.changeApprovalStatus(request, ApprovalStatus.APPROVED, null);

		assertEquals(request, retRequest, "Assert that the request returned is the same request");
		assertEquals(ApprovalStatus.APPROVED, request.getBenCoApproval().getStatus(),
				"Assert that the benCo approved the request.");
		assertEquals(ApprovalStatus.AWAITING, request.getFinalApproval().getStatus(),
				"Assert that the final approval is now in awaiting.");
		assertEquals(request.getSupervisorApproval().getUsername(), request.getFinalApproval().getUsername(),
				"Assert that the finalApproval username is set to the supervisor's username");
		assertNotEquals(request.getDeadline(), Request.PLACEHOLDER,
				"Assert that the time limit has changed for the Request from the placeholder");

		Mockito.verify(reqDao).updateRequest(reqCaptor.capture());

		assertEquals(request, reqCaptor.getValue(),
				"Assert that the request passed into updateRequest is the same request");
	}

	@Test
	public void testChangeApprovalStatusBenCoDenied() {
		// Set the pending balance to the request cost
		request.getSupervisorApproval().setStatus(ApprovalStatus.APPROVED);
		request.getDeptHeadApproval().setStatus(ApprovalStatus.APPROVED);
		request.getBenCoApproval().setUsername(benCo.getUsername());
		request.getBenCoApproval().setStatus(ApprovalStatus.AWAITING);
		request.setReimburseAmount(request.getCost() * request.getType().getPercent());
		user.setPendingBalance(request.getReimburseAmount());

		ArgumentCaptor<String> getUserCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<User> updateUserCaptor = ArgumentCaptor.forClass(User.class);
		ArgumentCaptor<Request> reqCaptor = ArgumentCaptor.forClass(Request.class);

		Request retRequest = service.changeApprovalStatus(request, ApprovalStatus.DENIED, "This is a test");

		assertEquals(request, retRequest, "Assert that the request returned is the same request");
		assertEquals(ApprovalStatus.DENIED, request.getBenCoApproval().getStatus(),
				"Assert that the supervisor approved the request.");
		assertEquals(RequestStatus.DENIED, request.getStatus(), "Assert that the request was denied.");
		assertEquals(0.0, user.getPendingBalance(), "Assert that the pending balance was set back to zero.");

		Mockito.verify(userDao).getUser(getUserCaptor.capture());
		Mockito.verify(userDao).updateUser(updateUserCaptor.capture());
		Mockito.verify(reqDao).updateRequest(reqCaptor.capture());

		assertEquals(request, reqCaptor.getValue(),
				"Assert that the request passed into updateRequest is the same request");
		assertEquals(user.getUsername(), getUserCaptor.getValue(),
				"Assert that the user's username is passed in to get user");
		assertEquals(user, updateUserCaptor.getValue(), "Assert that the the user is updated.");
	}

	@Test
	public void testChangeApprovalStatusFinalApproved() {
		// Set the pending balance to the request cost
		user.setPendingBalance(request.getCost());
		request.getSupervisorApproval().setStatus(ApprovalStatus.APPROVED);
		request.getDeptHeadApproval().setStatus(ApprovalStatus.APPROVED);
		request.getBenCoApproval().setStatus(ApprovalStatus.APPROVED);
		request.getFinalApproval().setUsername(benCo.getUsername());
		request.getFinalApproval().setStatus(ApprovalStatus.AWAITING);
		request.setReimburseAmount(request.getCost() * request.getType().getPercent());

		ArgumentCaptor<Request> reqCaptor = ArgumentCaptor.forClass(Request.class);
		ArgumentCaptor<String> usernameCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

		Request retRequest = service.changeApprovalStatus(request, ApprovalStatus.APPROVED, null);

		assertEquals(request, retRequest, "Assert that the request returned is the same request");
		assertEquals(ApprovalStatus.APPROVED, request.getFinalApproval().getStatus(),
				"Assert that the supervisor approved the request.");
		assertEquals(RequestStatus.APPROVED, request.getStatus(), "Assert that the request was approved.");
		assertEquals(0.0, user.getPendingBalance(), "Assert that the pending balance was set back to zero.");
		assertEquals(request.getReimburseAmount(), user.getAwardedBalance(),
				"Assert that the awarded balance is equal to the reimbursement amount.");

		Mockito.verify(reqDao).updateRequest(reqCaptor.capture());
		Mockito.verify(userDao).getUser(usernameCaptor.capture());
		Mockito.verify(userDao).updateUser(userCaptor.capture());

		assertEquals(request, reqCaptor.getValue(),
				"Assert that the request passed into updateRequest is the same request");
		assertEquals(user.getUsername(), usernameCaptor.getValue(),
				"Assert that the user's username is passed in to get user");
		assertEquals(user, userCaptor.getValue(), "Assert that the the user is updated.");
	}

	@Test
	public void testChangeApprovalStatusFinalDenied() {
		// Set the pending balance to the request cost
		request.getSupervisorApproval().setStatus(ApprovalStatus.APPROVED);
		request.getDeptHeadApproval().setStatus(ApprovalStatus.APPROVED);
		request.getBenCoApproval().setStatus(ApprovalStatus.APPROVED);
		request.getFinalApproval().setUsername(user.getSupervisorUsername());
		request.getFinalApproval().setStatus(ApprovalStatus.AWAITING);
		request.setReimburseAmount(request.getCost() * request.getType().getPercent());
		user.setPendingBalance(request.getReimburseAmount());

		ArgumentCaptor<String> getUserCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<User> updateUserCaptor = ArgumentCaptor.forClass(User.class);
		ArgumentCaptor<Request> reqCaptor = ArgumentCaptor.forClass(Request.class);

		Request retRequest = service.changeApprovalStatus(request, ApprovalStatus.DENIED, "This is a test");

		assertEquals(request, retRequest, "Assert that the request returned is the same request");
		assertEquals(ApprovalStatus.DENIED, request.getFinalApproval().getStatus(),
				"Assert that the supervisor approved the request.");
		assertEquals(RequestStatus.DENIED, request.getStatus(), "Assert that the request was denied.");
		assertEquals(0.0, user.getPendingBalance(), "Assert that the pending balance was set back to zero.");

		Mockito.verify(userDao).getUser(getUserCaptor.capture());
		Mockito.verify(userDao).updateUser(updateUserCaptor.capture());
		Mockito.verify(reqDao).updateRequest(reqCaptor.capture());

		assertEquals(request, reqCaptor.getValue(),
				"Assert that the request passed into updateRequest is the same request");
		assertEquals(user.getUsername(), getUserCaptor.getValue(),
				"Assert that the user's username is passed in to get user");
		assertEquals(user, updateUserCaptor.getValue(), "Assert that the the user is updated.");

	}

	@Test
	public void testChangeApprovalStatusInvalid() {
		// Request and status can't be null

		Request nullRequest = service.changeApprovalStatus(null, ApprovalStatus.APPROVED, "reason");
		assertNull("Assert that a null request returns a null", nullRequest);

		nullRequest = service.changeApprovalStatus(request, null, "reason");
		assertNull("Assert that a null status returns a null", nullRequest);

		// If the request has been approved, denied, or cancelled already.
		request.setStatus(RequestStatus.APPROVED);
		nullRequest = service.changeApprovalStatus(request, ApprovalStatus.APPROVED, "reason");
		assertNull("Assert that an approved request returns a null", nullRequest);

		request.setStatus(RequestStatus.DENIED);
		nullRequest = service.changeApprovalStatus(request, ApprovalStatus.APPROVED, "reason");
		assertNull("Assert that a denied request returns a null", nullRequest);

		request.setStatus(RequestStatus.CANCELLED);
		nullRequest = service.changeApprovalStatus(request, ApprovalStatus.APPROVED, "reason");
		assertNull("Assert that a cancelled request returns a null", nullRequest);

		// Request can't be null or blank if status is DENIED
		nullRequest = service.changeApprovalStatus(request, ApprovalStatus.DENIED, " ");
		assertNull("Assert that a blank reason with a DENIED approval status returns a null", nullRequest);

		nullRequest = service.changeApprovalStatus(request, ApprovalStatus.DENIED, null);
		assertNull("Assert that a null reason with a DENIED approval status returns a null", nullRequest);

		// Make sure an exception is thrown if an approval has DENIED or AWAITING and is
		// evaluated
		request.getSupervisorApproval().setStatus(ApprovalStatus.DENIED);
		request.setStatus(RequestStatus.ACTIVE);
		assertThrows(IllegalApprovalAttemptException.class, () -> {
			service.changeApprovalStatus(request, ApprovalStatus.APPROVED, null);
		});

		request.getSupervisorApproval().setStatus(ApprovalStatus.UNASSIGNED);
		assertThrows(IllegalApprovalAttemptException.class, () -> {
			service.changeApprovalStatus(request, ApprovalStatus.APPROVED, null);
		});
	}

	@Test
	public void testGetRequestValid() {

		// This should retrieve the request specified by the ID
		Request getRequest = service.getRequest(request.getId());
		assertEquals(request, getRequest, "Assert that the request returned is the same.");

		// Verify the method for the reqDao was called with the ID
		ArgumentCaptor<UUID> captor = ArgumentCaptor.forClass(UUID.class);
		Mockito.verify(reqDao).getRequest(captor.capture());

		assertEquals(request.getId(), captor.getValue(), "Assert that the ID passed in is the same.");
	}

	@Test
	public void testGetRequestInvalid() {

		// Should return null if a null request is passed in
		Request nullRequest = service.getRequest(null);
		assertNull("Assert that a null was returned from a null id", nullRequest);
	}

	@Test
	public void testUpdateRequestValid() {
		// Capture the Request
		ArgumentCaptor<Request> captor = ArgumentCaptor.forClass(Request.class);

		// Call the method
		service.updateRequest(request);

		// Verify it reqDao was called
		Mockito.verify(reqDao).updateRequest(captor.capture());

		// Make sure the capture is the same request
		assertEquals(request, captor.getValue(), "Assert that the request passed in is the same request");

	}

	@Test
	public void testUpdateRequestInvalid() {

		// Call the method
		service.updateRequest(null);

		// Make sure the reqDao is not called
		Mockito.verifyZeroInteractions(reqDao);

	}

	@Test
	public void testCancelRequestValidReimburse() {
		ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
		ArgumentCaptor<Request> reqCaptor = ArgumentCaptor.forClass(Request.class);
		Double reimburse = 200.00;
		Double zero = 0.0;

		request.setReimburseAmount(reimburse);
		user.setPendingBalance(reimburse);

		service.cancelRequest(request);

		assertEquals(RequestStatus.CANCELLED, request.getStatus(), "Assert that the request was cancelled");
		assertEquals(zero, user.getPendingBalance(), "Assert that pending balance is empty");

		Mockito.verify(userDao).updateUser(userCaptor.capture());
		Mockito.verify(reqDao).updateRequest(reqCaptor.capture());

		assertEquals(request, reqCaptor.getValue(), "Assert that the Request was passed into updateRequest");
		assertEquals(user, userCaptor.getValue(), "Assert that the User was passed into updateUser");
	}

	@Test
	public void testCancelRequestValidFinalReimburse() {
		// Set up the ArgumentCaptors
		ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
		ArgumentCaptor<Request> reqCaptor = ArgumentCaptor.forClass(Request.class);

		// Values to be used for the test
		Double reimburse = 200.00;
		Double finalReimburse = 100.00;
		Double zero = 0.0;

		// Set reimburse and finalreimburse. Finalreimburse should be the one used
		// though
		request.setReimburseAmount(reimburse);
		request.setFinalReimburseAmount(finalReimburse);
		user.setPendingBalance(finalReimburse);

		service.cancelRequest(request);

		assertEquals(RequestStatus.CANCELLED, request.getStatus(), "Assert that the request was cancelled");
		assertEquals(zero, user.getPendingBalance(), "Assert that pending balance is empty");

		Mockito.verify(userDao).updateUser(userCaptor.capture());
		Mockito.verify(reqDao).updateRequest(reqCaptor.capture());

		assertEquals(request, reqCaptor.getValue(), "Assert that the Request was passed into updateRequest");
		assertEquals(user, userCaptor.getValue(), "Assert that the User was passed into updateUser");
	}

	@Test
	public void testCancelRequestInvalid() {
		service.cancelRequest(null);

		Mockito.verifyZeroInteractions(reqDao);
		Mockito.verifyZeroInteractions(userDao);
	}

	@Test
	public void testChangeReimburseAmountValid() {
		ArgumentCaptor<Request> reqCaptor = ArgumentCaptor.forClass(Request.class);
		ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
		Double reimburse = 200.00;
		Double finalReimburse = 100.00;
		String reason = "this is a reason";

		request.setReimburseAmount(reimburse);
		user.setPendingBalance(reimburse);

		Request retRequest = service.changeReimburseAmount(request, finalReimburse, reason);

		// Make sure all the fields were set correctly
		assertEquals(request, retRequest, "Assert that the request returned is the same request.");
		assertEquals(finalReimburse, request.getFinalReimburseAmount(), "Assert that the finalReimburseAmount is set");
		assertEquals(reason, request.getFinalReimburseAmountReason(),
				"Assert that the finalReimburseAmountReason is set");
		assertEquals(true, request.getNeedsEmployeeReview(), "Assert that needsEmployeeReview is true");
		assertEquals(false, request.getEmployeeAgrees(), "Assert that employeeAgrees is set to false");
		assertEquals(finalReimburse, user.getPendingBalance(), "Assert that the user's pending balance was changed");

		// Verify updateRequest was called and the request was set in
		Mockito.verify(reqDao).updateRequest(reqCaptor.capture());
		Mockito.verify(userDao).updateUser(userCaptor.capture());
		assertEquals(request, reqCaptor.getValue(), "Assert that the request passed in is the same request");
		assertEquals(user, userCaptor.getValue(), "Assert that the user passed in is the same user");
	}

	@Test
	public void testChangeReimburseAmountInvalid() {
		Double reimburse = 200.00;
		Double finalReimburse = 100.00;
		String reason = "this is a reason";

		request.setReimburseAmount(reimburse);

		// Null requests return null
		Request nullRequest = service.changeReimburseAmount(null, finalReimburse, reason);
		assertNull("Assert that a null request returns a null", nullRequest);

		// Null or less than zero finalreimburse returns null
		nullRequest = service.changeReimburseAmount(request, null, reason);
		assertNull("Assert that a null finalReimburse returns a null", nullRequest);

		nullRequest = service.changeReimburseAmount(request, 0.0, reason);
		assertNull("Assert that a zero finalReimburse returns a null", nullRequest);

		nullRequest = service.changeReimburseAmount(request, -1.0, reason);
		assertNull("Assert that a negative finalReimburse returns a null", nullRequest);

		// Null or blank reason returns null
		nullRequest = service.changeReimburseAmount(request, finalReimburse, null);
		assertNull("Assert that a null reason returns a null", nullRequest);

		nullRequest = service.changeReimburseAmount(request, null, " ");
		assertNull("Assert that a blank reason returns a null", nullRequest);

		Mockito.verifyZeroInteractions(reqDao);
		Mockito.verifyZeroInteractions(userDao);
	}

	@Test
	public void testChangeEmployeeAgreesValidTrue() {
		ArgumentCaptor<Request> reqCaptor = ArgumentCaptor.forClass(Request.class);
		request.getBenCoApproval().setStatus(ApprovalStatus.AWAITING);
		Double finalReimburse = 200.00;
		request.setFinalReimburseAmount(finalReimburse);
		Boolean employeeAgrees = true;

		service.changeEmployeeAgrees(request, employeeAgrees);

		assertEquals(employeeAgrees, request.getEmployeeAgrees(), "Assert that employee agrees is set in the request");
		assertEquals(false, request.getNeedsEmployeeReview(),
				"Assert that needs employee review is set in the request");

		Mockito.verify(reqDao).updateRequest(reqCaptor.capture());

		assertEquals(request, reqCaptor.getValue(), "Assert that the request passed in is the same request");
	}

	@Test
	public void testChangEmployeeAgreesValidFalse() {
		ArgumentCaptor<Request> reqCaptor = ArgumentCaptor.forClass(Request.class);
		ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
		request.getBenCoApproval().setStatus(ApprovalStatus.AWAITING);
		Double finalReimburse = 200.00;
		request.setFinalReimburseAmount(finalReimburse);
		user.setPendingBalance(finalReimburse);
		Boolean employeeAgrees = false;

		service.changeEmployeeAgrees(request, employeeAgrees);

		assertEquals(employeeAgrees, request.getEmployeeAgrees(), "Assert that employee agrees is set in the request");
		assertEquals(false, request.getNeedsEmployeeReview(),
				"Assert that needs employee review is set in the request");
		assertEquals(RequestStatus.CANCELLED, request.getStatus(), "Assert that the request status was sent.");
		assertEquals(ApprovalStatus.UNASSIGNED, request.getBenCoApproval().getStatus(),
				"Assert that the benCoApproval status was set");
		assertEquals(0.0, user.getPendingBalance(), "Assert that the user's pending balance was reset");

		Mockito.verify(reqDao).updateRequest(reqCaptor.capture());
		Mockito.verify(userDao).updateUser(userCaptor.capture());

		assertEquals(request, reqCaptor.getValue(), "Assert that the request passed in is the same request");
		assertEquals(user, userCaptor.getValue(), "Assert that the user passed in is the same user");
	}

	@Test
	public void testChangeEmployeeAgreesInvalid() {
		request.getBenCoApproval().setStatus(ApprovalStatus.AWAITING);
		Double finalReimburse = 200.00;
		request.setFinalReimburseAmount(finalReimburse);
		Boolean employeeAgrees = true;

		service.changeEmployeeAgrees(null, employeeAgrees);
		service.changeEmployeeAgrees(request, null);

		Mockito.verifyZeroInteractions(reqDao);
		Mockito.verifyZeroInteractions(userDao);
	}

	@Test
	public void testAddFinalGradeValid() {
		String grade = "C";

		ArgumentCaptor<Request> reqCaptor = ArgumentCaptor.forClass(Request.class);

		service.addFinalGrade(request, grade);

		assertEquals(grade, request.getFinalGrade(), "Assert that the final grade was set");
		assertNotNull(request.getIsPassing(), "Assert that the isPassing is set");

		Mockito.verify(reqDao).updateRequest(reqCaptor.capture());

		assertEquals(request, reqCaptor.getValue(), "Assert that the request was passed in");
	}

	@Test
	public void testAddFinalGradeInvalid() {
		String grade = "C";

		service.addFinalGrade(null, grade);

		service.addFinalGrade(request, "");
		service.addFinalGrade(request, null);

		Mockito.verifyZeroInteractions(reqDao);
	}

	@Test
	public void testAutoApproveSupervisor() {
		
		ArgumentCaptor<Request> reqCaptor = ArgumentCaptor.forClass(Request.class);
		// Set approval status to awaiting and the deadline to expired
		request.setSupervisorApproval(new Approval(ApprovalStatus.AWAITING, supervisor.getUsername()));
		request.setDeadline(Request.PLACEHOLDER);

		// Create the list and add the request to it
		List<Request> requests = new ArrayList<Request>();
		requests.add(request);

		Mockito.when(reqDao.getExpiredRequests()).thenReturn(requests);

		service.autoApprove();

		assertEquals(ApprovalStatus.AUTO_APPROVED, request.getSupervisorApproval().getStatus(),
				"Assert that the supervisor approval was auto-approved");
		assertEquals(ApprovalStatus.AWAITING, request.getDeptHeadApproval().getStatus(),
				"Assert that the dept head approval was seet to awaiting.");
		assertNotEquals(Request.PLACEHOLDER, request.getDeadline(), "Assert that the deadline was set");
		
		Mockito.verify(reqDao).updateRequest(reqCaptor.capture());
		
		assertEquals(request, reqCaptor.getValue(), "Assert that the request was passed in to the updateRequest");
	}

	@Test
	public void testAutoApproveDeptHead() {
		
		ArgumentCaptor<Request> reqCaptor = ArgumentCaptor.forClass(Request.class);
		// Set approval status to awaiting and the deadline to expired
		request.setSupervisorApproval(new Approval(ApprovalStatus.APPROVED, supervisor.getUsername()));
		request.setDeptHeadApproval(new Approval(ApprovalStatus.AWAITING, deptHead.getUsername()));
		request.setDeadline(Request.PLACEHOLDER);

		// Create the list and add the request to it
		List<Request> requests = new ArrayList<Request>();
		requests.add(request);

		Mockito.when(reqDao.getExpiredRequests()).thenReturn(requests);

		service.autoApprove();

		assertEquals(ApprovalStatus.AUTO_APPROVED, request.getDeptHeadApproval().getStatus(),
				"Assert that the dept head approval was auto-approved");
		assertEquals(ApprovalStatus.AWAITING, request.getBenCoApproval().getStatus(),
				"Assert that the benCo approval was seet to awaiting.");
		assertNotEquals(Request.PLACEHOLDER, request.getDeadline(), "Assert that the deadline was set");
		
		Mockito.verify(reqDao).updateRequest(reqCaptor.capture());
		
		assertEquals(request, reqCaptor.getValue(), "Assert that the request was passed in to the updateRequest");
	}
	
	@Test
	public void testAutoApproveBenCo() {
		ArgumentCaptor<Request> reqCaptor = ArgumentCaptor.forClass(Request.class);
		// Set approval status to awaiting and the deadline to expired
		request.setSupervisorApproval(new Approval(ApprovalStatus.APPROVED, supervisor.getUsername()));
		request.setDeptHeadApproval(new Approval(ApprovalStatus.APPROVED, deptHead.getUsername()));
		request.setBenCoApproval(new Approval(ApprovalStatus.AWAITING, benCo.getUsername()));
		request.setDeadline(Request.PLACEHOLDER);

		// Create the list and add the request to it
		List<Request> requests = new ArrayList<Request>();
		requests.add(request);

		Mockito.when(reqDao.getExpiredRequests()).thenReturn(requests);

		service.autoApprove();

		assertEquals(ApprovalStatus.AWAITING, request.getBenCoApproval().getStatus(),
				"Assert that the benCo approval was seet to awaiting.");
		assertNotEquals(Request.PLACEHOLDER, request.getDeadline(), "Assert that the deadline was set");
		
		Mockito.verify(reqDao).updateRequest(reqCaptor.capture());
		
		assertEquals(request, reqCaptor.getValue(), "Assert that the request was passed in to the updateRequest");
	}

	@Test
	public void testAutoApproveInvalid() {
		// Set all the approval statuses to approved and the deadline to expired
		request.setSupervisorApproval(new Approval(ApprovalStatus.APPROVED, supervisor.getUsername()));
		request.setDeptHeadApproval(new Approval(ApprovalStatus.APPROVED, deptHead.getUsername()));
		request.setBenCoApproval(new Approval(ApprovalStatus.APPROVED, benCo.getUsername()));
		request.setFinalApproval(new Approval(ApprovalStatus.APPROVED, benCo.getUsername()));
		request.setDeadline(Request.PLACEHOLDER);

		// Create the list and add the request to it
		List<Request> requests = new ArrayList<Request>();
		requests.add(request);

		Mockito.when(reqDao.getExpiredRequests()).thenReturn(requests);

		assertThrows(IllegalApprovalAttemptException.class, () -> service.autoApprove(),
				"Assert that an active request with no awaiting approvals throws an exception.");
	}
}
