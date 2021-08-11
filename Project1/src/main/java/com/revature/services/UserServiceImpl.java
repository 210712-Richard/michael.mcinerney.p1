package com.revature.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revature.beans.User;
import com.revature.beans.UserType;
import com.revature.data.NotificationDao;
import com.revature.data.NotificationDaoImpl;
import com.revature.data.UserDao;
import com.revature.data.UserDaoImpl;
import com.revature.factory.BeanFactory;
import com.revature.factory.TraceLog;
import com.revature.util.Verifier;

@TraceLog
public class UserServiceImpl implements UserService {
	
	//The DAO for users and notifications
	private UserDao userDao;
	private NotificationDao notDao;
	
	//For logging
	private static Logger log = LogManager.getLogger(UserServiceImpl.class);
	
	//For verifying objects and strings
	private static final Verifier VERIFIER = new Verifier();
	
	/**
	 * Constructor use for normal use
	 */
	public UserServiceImpl() {
		userDao = (UserDao) BeanFactory.getFactory().getObject(UserDao.class, UserDaoImpl.class);
		notDao = (NotificationDao) BeanFactory.getFactory().getObject(NotificationDao.class, NotificationDaoImpl.class);
	}

	/**
	 * Constructor used for tests
	 */
	public UserServiceImpl(UserDao userDao, NotificationDao notDao) {
		this.userDao = userDao;
		this.notDao = notDao;
	}

	@Override
	public User login(String username, String password) {
		
		// Make sure the username and password are not blank and null
		if (!VERIFIER.verifyStrings(username, password)) {
			return null;
		}
		
		//Get the user
		User user = userDao.getUser(username, password);
		log.debug("User returned: " + user);
		
		//Get the user's notifications if the user was found
		if (user != null) {
			user.setNotifications(notDao.getUserNotificationList(username));
		}
		
		return user;
	}

	@Override
	public User createUser(String username, String password, String email, String firstName, String lastName,
			UserType type, String deptName, String supervisorUsername) {
		// Create the new user
		User newUser = new User(username, password, email, firstName, lastName, type, deptName, supervisorUsername);
		log.debug("User being created: " + newUser);
		
		//Send the data to the DAO to put in the database
		userDao.createUser(newUser);
		
		return newUser;
	}

	@Override
	public Boolean isUsernameUnique(String username) {
		//Get the user from the database
		User user = userDao.getUser(username);
		log.debug("User returned: " + user);
		
		//If the user is in the database, return false.
		if (user != null) {
			return false;
		}
		//Otherwise, return true
		return true;
	}

	@Override
	public User updateUser(User user) {
		
		//IF a null user was passed in, return null
		if (user == null) {
			return null;
		}
		
		//Send the updated user to the database to put the new data
		userDao.updateUser(user);
		return user;
	}
	
	@Override
	public void deleteNotifications(String username) {
		
		// Make sure the username is not null or blank
		if (VERIFIER.verifyStrings(username)) {
			// Delete all notifications that this user has received
			notDao.deleteUserNotifications(username);
		}
	}

}
