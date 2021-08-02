package com.revature.services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.revature.beans.EventType;
import com.revature.beans.GradingFormatFactory;
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
		request.setGradingFormat(new GradingFormatFactory().getGradingFormat());
		request.setType(EventType.CERTIFICATION);

		user = new User("Tester", "TestPass", "user@test.com", "Test", "User", UserType.EMPLOYEE, "Test", "TestSuper");

		dao = mock.setPrivateMock(service, "requestDao");
	}

	@Test
	public void testCreateRequestValid() {
		//TODO
	}
	
	@Test
	public void testCreateRequestInvalid() {
		//TODO
	}
}
