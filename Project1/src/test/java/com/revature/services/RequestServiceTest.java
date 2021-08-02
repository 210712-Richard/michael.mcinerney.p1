package com.revature.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
import com.revature.util.MockitoHelper;

public class RequestServiceTest {
	private RequestService service = null;
	private Request request = null;
	private RequestDao dao = null;

	User user = null;

	private static MockitoHelper<RequestDao> mock = null;

	@BeforeAll
	public static void beforeAll() {
		mock = new MockitoHelper<RequestDao>(RequestDao.class);
	}

	@BeforeEach
	public void beforeTest() {
		service = new RequestServiceImpl();

		request = new ReimbursementRequest();
		request.setUsername("Tester");
		request.setName("Test Certification");
		request.setFirstName("Test");
		request.setLastName("User");
		request.setDeptName("Test");
		request.setStartDate(LocalDate.now().plus(Period.of(0, 1, 0)));
		request.setStartTime(LocalTime.now());
		request.setLocation("101 Test Dr. Test, VA 99999");
		request.setDescription("A Test course");
		request.setCost(200.00);
		request.setGradingFormat(new GradingFormat(Format.LETTER));
		request.setType(EventType.CERTIFICATION);

		user = new User("Tester", "TestPass", "user@test.com", "Test", "User", UserType.EMPLOYEE, "Test", "TestSuper");

		dao = mock.setPrivateMock(service, "requestDao");
	}

	@Test
	public void testCreateRequestValid() {
		// Set up the argument captor
		ArgumentCaptor<Request> captor = ArgumentCaptor.forClass(Request.class);

		//Call the method
		Request newRequest = service.createRequest(request.getUsername(), request.getFirstName(), request.getLastName(),
				request.getDeptName(), request.getName(), request.getStartDate(), request.getStartTime(), request.getLocation(),
				request.getDescription(), request.getCost(), request.getGradingFormat(), request.getType());
		
		//Make sure all the fields are the same and that ID is not null.
		assertNotNull(newRequest.getId(), "Assert that a new ID was made for the request.");
		request.setId(newRequest.getId());
		assertEquals(request, newRequest, "Assert that both requests have the same values.");
		
		//Make sure the method was called and passed in the correct argument
		Mockito.verify(dao).createRequest(captor.capture());
		assertEquals(newRequest, captor.getValue(), "Assert that the arguments passed to the dao are the same returned.");
	}

	@Test
	public void testCreateRequestInvalid() {
		// Call the method
		Request newRequest = service.createRequest(request.getUsername(), request.getFirstName(), request.getLastName(),
				request.getDeptName(), request.getName(), request.getStartDate(), request.getStartTime(), request.getLocation(),
				request.getDescription(), request.getCost(), request.getGradingFormat(), request.getType());
		
		//Blank or null username returns a null
		
		//Blank or null firstName returns a null
		
		//Blank or null lastName returns a null
		
		//Blank or null name returns a null
		
		//Late or null startDate returns a null
		
		//Null startTime returns a null
		
		//Blank or null location returns a null
		
		//Blank or null description returns a null
		
		//Null or not-positive cost returns a null
		
		//Null gradingFormat returns a null
		
		//Null type returns a null
		
		
	}
}
