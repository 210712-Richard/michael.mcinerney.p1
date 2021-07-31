package com.revature.data;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.revature.beans.User;
import com.revature.beans.UserType;

public class UserDaoTest {
	private UserDao userDao = null;
	private User user = null;

	@BeforeAll
	public static void beforeAll() {
		
	}

	@BeforeEach
	public void beforeTest() {
		userDao = new UserDaoImpl();
		user = new User("Tester", "TestPass", "user@test.com", "Test", "User", UserType.EMPLOYEE, "Test",
				"TestSuper");

		// Make sure a valid department is passed through to the creation.
		
	}

	/**** addUser(User user) Tests ****/

	@Test
	public void testAddUser() {
		// Set up mock and input
		User newUser = new User("newUser", "password", "email@user.com", "New", "User", UserType.EMPLOYEE, "Test",
				"Test");
		
		//Make sure a valid user does not throw an exception
		assertAll("Assert that an exception is not thrown for the creation.", () -> userDao.addUser(user));

		// Make sure a null department throws an exception.
		assertThrows(Exception.class, () -> userDao.addUser(null),
				"Assert that an exception is thrown for the creation of a null department.");

	}

	/**** getUser(String username) Tests ****/

	@Test
	public void testGetUserByNameValid() {
		User getUser = userDao.getUser(user.getUsername());

		// Make sure the user returned is the same.
		assertEquals(getUser, user, "Assert that both users are the same.");
	}

	@Test
	public void testGetUserByNameInvalid() {
		
		//Make sure an invalid username results in a null
		assertNull("Assert that a username not in the database returns a null", userDao.getUser("WrongUser"));
		
		//Make sure a null username returns a null
		assertNull("Assert that a null username returns a null", userDao.getUser(null));
	}

	/**** getUser(String username, String password) Tests ****/

	@Test
	public void testGetUserByNamePasswordValid() {

	}

	@Test
	public void testGetUserByNamePasswordInvalid() {

	}

	/**** updateUser(User user) Tests ****/

	@Test
	public void testUpdateUser() {

	}
}
