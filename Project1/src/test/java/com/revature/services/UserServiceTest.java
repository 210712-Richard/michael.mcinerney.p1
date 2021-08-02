package com.revature.services;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.revature.beans.User;
import com.revature.beans.UserType;
import com.revature.data.UserDao;
import com.revature.util.MockitoHelper;

import software.amazon.awssdk.core.ServiceConfiguration;

public class UserServiceTest {
	private UserService service = null;
	private User user = null;
	private UserDao dao = null;

	private static MockitoHelper<UserDao> mock = null;

	@BeforeAll
	public static void beforeAll() {
		mock = new MockitoHelper<UserDao>(UserDao.class);
	}

	@BeforeEach
	public void beforeTest() {
		service = new UserServiceImpl();

		user = new User("Tester", "TestPass", "user@test.com", "Test", "User", UserType.EMPLOYEE, "Test", "TestSuper");

		dao = mock.setPrivateMock(service, "userDao");
	}

	@Test
	public void testLoginValid() {
		// Set up mockito so that when dao.getUser is called, it will return the user as
		// it should.
		Mockito.when(dao.getUser(user.getUsername(), user.getPassword())).thenReturn(user);

		// Use ArgumentCaptor to get arguments
		ArgumentCaptor<String> usernameCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> passwordCaptor = ArgumentCaptor.forClass(String.class);
		// Get the user using the user credentials
		User loginUser = service.login(user.getUsername(), user.getPassword());

		// Make sure the user is the same.
		assertEquals(user, loginUser, "Assert that the user returned is the same user.");

		// Verify getUser was called and get the arguments used
		Mockito.verify(dao).getUser(usernameCaptor.capture(), passwordCaptor.capture());

		// Make sure the arguments are correct
		assertEquals(user.getUsername(), usernameCaptor.getValue(),
				"Assert that the username used is the user's username.");
		assertEquals(user.getPassword(), passwordCaptor.getValue(),
				"Assert that the password used is the user's password.");
	}

	@Test
	public void testLoginInvalid() {
		// Set up the mock for the wrong combination:
		String wrong = "Wrong";
		Mockito.when(dao.getUser(user.getUsername(), wrong)).thenReturn(null);
		Mockito.when(dao.getUser(wrong, user.getPassword())).thenReturn(null);

		// The username/password combo is wrong
		User nullUser = service.login(user.getUsername(), wrong);
		assertNull("Assert that an incorrect password returns a null User.", nullUser);

		nullUser = service.login(wrong, user.getPassword());
		assertNull("Assert that an incorrect username returns a null User.", nullUser);

		// The username and/or password is blank
		nullUser = service.login(user.getUsername(), " ");
		assertNull("Assert that a blank password returns a null User.", nullUser);

		nullUser = service.login("   ", user.getPassword());
		assertNull("Assert that a blank username returns a null User.", nullUser);

		// The username and/or password is null
		nullUser = service.login(user.getUsername(), null);
		assertNull("Assert that a null password returns a null User.", nullUser);

		nullUser = service.login(null, user.getPassword());
		assertNull("Assert that a null username returns a null User.", nullUser);
	}

	@Test
	public void testCreateUserValid() {
		// Have a captor ready to get arguments from Mockito
		ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

		//Call the method
		service.createUser(user.getUsername(), user.getPassword(), user.getEmail(), user.getFirstName(),
				user.getLastName(), user.getType(), user.getDepartmentName(), user.getSupervisorUsername());

		//Verify the dao.createUser is called and capture the arguments
		Mockito.verify(dao).createUser(captor.capture());

		//Make sure the argument matches the user
		User capture = captor.getValue();
		assertEquals(user, capture,
				"Assert that the user passed in is the same as the user with the details passed in.");
	}
	
}
