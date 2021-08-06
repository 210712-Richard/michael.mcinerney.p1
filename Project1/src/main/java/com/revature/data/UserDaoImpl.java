package com.revature.data;

import java.util.UUID;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.DefaultConsistencyLevel;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.cql.SimpleStatementBuilder;
import com.revature.beans.User;
import com.revature.beans.UserType;
import com.revature.factory.TraceLog;
import com.revature.util.CassandraUtil;

@TraceLog
public class UserDaoImpl implements UserDao {
	private CqlSession session = CassandraUtil.getInstance().getSession();

	@Override
	public User getUser(String username) {
		
		// Create the query and bind the parameters to it
		StringBuilder query = new StringBuilder("SELECT username, password, email, firstname, ")
				.append("lastname, type, departmentname, supervisorusername, pendingbalance, awardedbalance, requests, "
						+ "reviewrequests FROM user WHERE username = ?;");
		SimpleStatement s = new SimpleStatementBuilder(query.toString()).build();
		BoundStatement bound = session.prepare(s).bind(username);
		
		//Execute the query and get the result set
		ResultSet rs = session.execute(bound);

		Row row = rs.one();
		
		//If the row is null, return a null object
		if (row == null) {
			return null;
		}
		
		//Set all the row's data to the user
		User user = new User();

		user.setUsername(row.getString("username"));
		user.setPassword(row.getString("password"));
		user.setEmail(row.getString("email"));
		user.setFirstName(row.getString("firstname"));
		user.setLastName(row.getString("lastname"));
		user.setType(UserType.valueOf(row.getString("type")));
		user.setDepartmentName(row.getString("departmentname"));
		user.setSupervisorUsername(row.getString("supervisorusername"));
		user.setPendingBalance(row.getDouble("pendingbalance"));
		user.setAwardedBalance(row.getDouble("awardedbalance"));
		user.setRequests(row.getList("requests", UUID.class));
		user.setReviewRequests(row.getList("reviewrequests", UUID.class));

		return user;
	}

	@Override
	public User getUser(String username, String password) {

		//Create the query and bind the parameters
		StringBuilder query = new StringBuilder("SELECT username, password, email, firstname, ")
				.append("lastname, type, departmentname, supervisorusername, pendingbalance, awardedbalance, requests, "
						+ "reviewrequests FROM user WHERE username = ? AND password = ?;");
		SimpleStatement s = new SimpleStatementBuilder(query.toString()).build();
		BoundStatement bound = session.prepare(s).bind(username, password);
		
		//Execute the query and get the result set
		ResultSet rs = session.execute(bound);

		Row row = rs.one();
		
		//If the row is null, return null
		if (row == null) {
			return null;
		}

		//Set the row's data to a User and return the User
		User user = new User();

		user.setUsername(row.getString("username"));
		user.setPassword(row.getString("password"));
		user.setEmail(row.getString("email"));
		user.setFirstName(row.getString("firstname"));
		user.setLastName(row.getString("lastname"));
		user.setType(UserType.valueOf(row.getString("type")));
		user.setDepartmentName(row.getString("departmentname"));
		user.setSupervisorUsername(row.getString("supervisorusername"));
		user.setPendingBalance(row.getDouble("pendingbalance"));
		user.setAwardedBalance(row.getDouble("awardedbalance"));
		user.setRequests(row.getList("requests", UUID.class));
		user.setReviewRequests(row.getList("reviewrequests", UUID.class));

		return user;
	}

	@Override
	public void updateUser(User user) {
		//Create the query and bind the parameters
		StringBuilder query = new StringBuilder("UPDATE user SET email=?, firstname=?, ").append(
				"lastname=?, type=?, departmentname=?, supervisorusername=?, pendingbalance=?, awardedbalance=?, requests=?, "
						+ "reviewrequests=? WHERE username = ? AND password = ?");
		SimpleStatement s = new SimpleStatementBuilder(query.toString())
				.setConsistencyLevel(DefaultConsistencyLevel.LOCAL_QUORUM).build();
		BoundStatement bound = session.prepare(s).bind(user.getEmail(), user.getFirstName(), user.getLastName(),
				user.getType().toString(), user.getDepartmentName(), user.getSupervisorUsername(),
				user.getPendingBalance(), user.getAwardedBalance(), user.getRequests(), user.getReviewRequests(),
				user.getUsername(), user.getPassword());
		
		//Execute the query
		session.execute(bound);
	}

	@Override
	public void createUser(User user) {
		//Create the query and bind the parameters
		StringBuilder query = new StringBuilder("INSERT INTO user (username, password, email, firstname, ")
				.append("lastname, type, departmentname, supervisorusername, pendingbalance, awardedbalance, requests, "
						+ "reviewrequests")
				.append(") values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
		SimpleStatement s = new SimpleStatementBuilder(query.toString())
				.setConsistencyLevel(DefaultConsistencyLevel.LOCAL_QUORUM).build();
		BoundStatement bound = session.prepare(s).bind(user.getUsername(), user.getPassword(), user.getEmail(),
				user.getFirstName(), user.getLastName(), user.getType().toString(), user.getDepartmentName(),
				user.getSupervisorUsername(), user.getPendingBalance(), user.getAwardedBalance(), user.getRequests(),
				user.getReviewRequests());
		
		//Execute the query
		session.execute(bound);
	}
}
