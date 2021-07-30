package com.revature.data;

import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.revature.beans.User;
import com.revature.beans.UserType;
import com.revature.util.MockitoHelper;

public class UserDaoTest {
	UserDao userDao = null;
	CqlSession session = null;
	User user = null;

	MockitoHelper<CqlSession> mock;

	@BeforeAll
	public void beforeAll() {
		mock = new MockitoHelper<CqlSession>(CqlSession.class);
	}

	@BeforeEach
	public void beforeTest() {
		userDao = new UserDaoImpl();
		user = new User();
	}

	/**** addUser(User user) Tests ****/

	@Test
	public void testAddUser() {
		//Set up mock and input
		session = mock.setPrivateMock(userDao, "session");
		User newUser = new User("newUser", "password", "email@user.com", "New", "User", UserType.EMPLOYEE,
				"Test", UUID.fromString("0"));
		
		//Use the argument captor
		ArgumentCaptor<BoundStatement> captor = ArgumentCaptor.forClass(BoundStatement.class);
		
		//Use mock to verify execute was called.
		Mockito.verify(session).execute(captor.capture());
		
		//Verify that all the arguments were passed in correctly
		
	}

	/**** getUser(UUID id) Tests ****/

	@Test
	public void testGetUserByIDValid() {

	}

	@Test
	public void testGetUserByIDInvalid() {

	}

	/**** getUser(String username) Tests ****/

	@Test
	public void testGetUserByNameValid() {

	}

	@Test
	public void testGetUserByNameInvalid() {

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
