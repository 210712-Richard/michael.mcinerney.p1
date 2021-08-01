package com.revature.data;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.revature.beans.Approval;
import com.revature.beans.EventType;
import com.revature.beans.GradingFormatFactory;
import com.revature.beans.ReimbursementRequest;
import com.revature.beans.Request;

public class RequestDaoTest {
	RequestDao requestDao = null;
	Request request = null;

	@BeforeEach
	public void beforeTest() {
		requestDao = new RequestDaoImpl();
		request = new ReimbursementRequest();

		request.setId(999);
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
		request.setReimburseAmount(Request.MAX_REIMBURSEMENT * request.getType().getPercent());
		request.setDeptHeadApproval(new Approval());
		request.setSupervisorApproval(new Approval());
		request.setBenCoApproval(new Approval());
		request.setFinalApproval(new Approval());

	}

	@Test
	public void testCreateRequest() {
		// Make sure a valid request does not throw an exception
		assertAll("Assert that an exception is not thrown for the creation.", () -> requestDao.createRequest(request));

		// Make sure a null request throws an exception.
		assertThrows(Exception.class, () -> requestDao.createRequest(null),
				"Assert that an exception is thrown for the creation of a null department.");
	}

	@Test
	public void testUpdateRequest() {
		// Make sure a valid request does not throw an exception
		assertAll("Assert that an exception is not thrown for the creation.", () -> requestDao.updateRequest(request));

		// Make sure a null request throws an exception.
		assertThrows(Exception.class, () -> requestDao.updateRequest(null),
				"Assert that an exception is thrown for the creation of a null department.");
	}

	@Test
	public void testGetRequestValid() {
		Request getRequest = requestDao.getRequest(request.getId());

		// Make sure the user returned is the same.
		assertEquals(getRequest.getId(), request.getId(), "Assert that both request ids are the same.");
		assertEquals(getRequest.getUsername(), request.getUsername(),
				"Assert that both request usernames are the same.");
	}

	@Test
	public void testGetRequestInvalid() {

		// Make sure an invalid username results in a null
		assertNull("Assert that a id not in the database returns a null", requestDao.getRequest(-1));

		// Make sure a null username returns a null
		assertNull("Assert that a null request returns a null", requestDao.getRequest(null));
	}

	@Test
	public void testGetRequests() {
		List<Request> requests = requestDao.getRequests();

		assertTrue(requests != null,
				"Assert that the request list is not null.");
	}
}
