package com.revature.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revature.beans.User;
import com.revature.data.UserDao;
import com.revature.data.UserDaoImpl;
import com.revature.factory.BeanFactory;
import com.revature.factory.TraceLog;

@TraceLog
public class UserServiceImpl implements UserService {
	UserDao userDao = (UserDao) BeanFactory.getFactory().getObject(UserDao.class,
			UserDaoImpl.class);
	private static Logger log = LogManager.getLogger(UserServiceImpl.class);
	
	@Override
	public User login(String username, String password) {
		if (username == null || username.isBlank() || password == null || password.isBlank()) {
			return null;
		}
		User user = userDao.getUser(username, password);
		log.debug("User returned: " + user);
		return user;
	}
}
