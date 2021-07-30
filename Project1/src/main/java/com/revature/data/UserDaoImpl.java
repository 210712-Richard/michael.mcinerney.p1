package com.revature.data;

import java.util.UUID;

import com.datastax.oss.driver.api.core.CqlSession;
import com.revature.beans.User;
import com.revature.factory.TraceLog;
import com.revature.util.CassandraUtil;

@TraceLog
public class UserDaoImpl implements UserDao {
	private CqlSession session = CassandraUtil.getInstance().getSession();
	
	@Override
	public User getUser(UUID id) {
		return null;
	}

	@Override
	public User getUser(String username) {
		return null;
	}

	@Override
	public User getUser(String username, String password) {
		return null;
	}

	@Override
	public void addUser(User user) {
		
	}

	@Override
	public void updateUser(User user) {
		
	}
}
