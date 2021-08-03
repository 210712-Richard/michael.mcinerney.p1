package com.revature.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.revature.beans.EventType;
import com.revature.beans.Format;
import com.revature.beans.GradingFormat;
import com.revature.beans.ReimbursementRequest;
import com.revature.beans.Request;
import com.revature.beans.User;
import com.revature.beans.UserType;
import com.revature.data.RequestDao;
import com.revature.data.UserDao;
import com.revature.util.MockitoHelper;

public class RequestServiceTest {
	private RequestService service = null;
	private static Request request = null;
	private RequestDao reqDao = null;
	private UserDao userDao = null;

	User user = null;

	private static MockitoHelper mock = null;

	@BeforeAll
	public static void beforeAll() {
		mock = new MockitoHelper();
		request = new ReimbursementRequest();
	}

	@BeforeEach
	public void beforeTest() {
		service = new RequestServiceImpl();

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

		reqDao = (RequestDao) mock.setPrivateMock(service, "reqDao", RequestDao.class);
		userDao = (UserDao) mock.setPrivateMock(service, "userDao", UserDao.class);
	}

	@Test
	public void testCreateRequestValid() {
		// Set up the argument captor
		ArgumentCaptor<Request> captor = ArgumentCaptor.forClass(Request.class);

		Mockito.when(userDao.getUser(user.getUsername())).thenReturn(user);

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

		// Make sure supervisorUsername is set to user's supervisorUsername
		assertEquals(user.getSupervisorUsername(), newRequest.getSupervisorApproval().getUsername(),
				"Assert that the supervisor that needs to do the approval is the same as the User's supervisor");

		// Make sure deadline is not the default value
		assertTrue(Request.PLACEHOLDER.isBefore(newRequest.getSupervisorApproval().getDeadline()),
				"Assert that the deadline changed away from the placeholder.");

		// Make sure the method was called and passed in the correct argument
		Mockito.verify(reqDao).createRequest(captor.capture());
		assertEquals(newRequest, captor.getValue(),
				"Assert that the arguments passed to the dao are the same returned.");

		// If the user has a pending and awarded balance that is less than the max
		// amount alloted but less than the cost,
		// it should still create the request, but with a different cost.

		user.setAwardedBalance(800.00);
		user.setPendingBalance(100.00);
		Double expectedCost = Request.MAX_REIMBURSEMENT - user.getPendingBalance()
				- user.getAwardedBalance();
		
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
		assertEquals(expectedCost, newRequest.getCost(), "Assert that the cost is set.");
		assertEquals(request.getGradingFormat(), newRequest.getGradingFormat(),
				"Assert that the gradingFormat is set.");
		assertEquals(request.getType(), newRequest.getType(), "Assert that the type is set.");
	}

	@Test
	public void testCreateRequestInvalid() {
		// Set the user's pending and awarded balance to make sure it works
		user.setPendingBalance(500.00);
		user.setAwardedBalance(500.00);
		Mockito.when(userDao.getUser(user.getUsername())).thenReturn(user);

		Request newRequest = service.createRequest(null, request.getFirstName(), request.getLastName(),
				request.getDeptName(), request.getName(), request.getStartDate(), request.getStartTime(),
				request.getLocation(), request.getDescription(), request.getCost(), request.getGradingFormat(),
				request.getType());

		// Blank or null username returns a null

		// Blank or null firstName returns a null

		// Blank or null lastName returns a null

		// Blank or null name returns a null

		// Late or null startDate returns a null

		// Null startTime returns a null

		// Blank or null location returns a null

		// Blank or null description returns a null

		// Null or not-positive cost returns a null

		// Null gradingFormat returns a null

		// Null type returns a null

	}
}
