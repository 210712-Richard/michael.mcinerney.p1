package com.revature.util;

import com.revature.beans.Department;
import com.revature.beans.User;
import com.revature.beans.UserType;
import com.revature.data.DepartmentDao;
import com.revature.data.DepartmentDaoImpl;
import com.revature.data.UserDao;
import com.revature.data.UserDaoImpl;

public class DatabaseCreator {

	public static void dropTables() {
		StringBuilder query = new StringBuilder("DROP TABLE IF EXISTS User;");
		CassandraUtil.getInstance().getSession().execute(query.toString());

		query = new StringBuilder("DROP TABLE IF EXISTS Request;");
		CassandraUtil.getInstance().getSession().execute(query.toString());

		query = new StringBuilder("DROP TABLE IF EXISTS Department;");
		CassandraUtil.getInstance().getSession().execute(query.toString());
		
		query = new StringBuilder("DROP TABLE IF EXISTS Notification;");
		CassandraUtil.getInstance().getSession().execute(query.toString());
	}

	public static void createTables() {
		StringBuilder query = new StringBuilder("CREATE TABLE IF NOT EXISTS User (")
				.append("username text, password text, email text, firstName text, ")
				.append("lastName text, type text, departmentName text, supervisorUsername text, ")
				.append("pendingBalance double, awardedBalance double, requests list<UUID>, ")
				.append("primary key(username, password));");
		CassandraUtil.getInstance().getSession().execute(query.toString());

		query = new StringBuilder("CREATE TABLE IF NOT EXISTS Request (").append(
				"id uuid, username text, status text, isUrgent boolean, name text, firstName text, lastName text, ")
				.append("deptName text, startDate date, startTime time, location text, ")
				.append("description text, cost double, gradingFormat tuple<text, text>, ")
				.append("type text, fileURIs List<text>, approvalMsgsURIs List<text>, workTimeMissed text, ")
				.append("reimburseAmount double, supervisorApproval tuple<text, text>, ")
				.append(", deptHeadApproval tuple<text, text>, ")
				.append(", benCoApproval tuple<text, text>, reason text, deadline timestamp, ")
				.append("finalGrade text, isPassing boolean, presFileName text, finalApproval tuple<text, text>, ")
				.append("finalReimburseAmount double, finalReimburseAmountReason text, needsEmployeeReview boolean, employeeAgrees boolean, ")
				.append("primary key(id, username));");
		CassandraUtil.getInstance().getSession().execute(query.toString());

		query = new StringBuilder("CREATE TABLE IF NOT EXISTS Department (")
				.append("name text PRIMARY KEY, deptHeadUsername text").append(");");
		CassandraUtil.getInstance().getSession().execute(query.toString());
		
		query = new StringBuilder("CREATE TABLE IF NOT EXISTS Notification (")
				.append("username text, requestId uuid, notificationTime timestamp, message text, ")
				.append("primary key(username, requestId, notificationTime));");
		CassandraUtil.getInstance().getSession().execute(query.toString());
	}

	public static void populateDepartment() {
		DepartmentDao dao = new DepartmentDaoImpl();
		Department dept = new Department("Test", "TestHead");

		dao.createDepartment(dept);

		dept = new Department("Business", "john-doe");
		dao.createDepartment(dept);

		dept = new Department("Math", "jane-doe");
		dao.createDepartment(dept);

		dept = new Department("Engineering", "daniel-tubb");
		dao.createDepartment(dept);

		dept = new Department("Science", "shirly-cord");
		dao.createDepartment(dept);

		dept = new Department("Organization", "geoff-beesos");
		dao.createDepartment(dept);

		dept = new Department("Benefits", "sean-jones");
		dao.createDepartment(dept);
	}

	public static void populateUser() {
		UserDao dao = new UserDaoImpl();

		// Overall head of the organization
		User user = new User("geoff-beesos", "password", "geoff.besos@test.com", "Geoff", "Besos", UserType.SUPERVISOR,
				"Organization", null);
		dao.createUser(user);

		// Department heads
		user = new User("john-doe", "password", "john.doe@test.com", "John", "Doe", UserType.SUPERVISOR, "Business",
				"geoff-beesos");
		dao.createUser(user);

		user = new User("jane-doe", "password", "jane.doe@test.com", "Jane", "Doe", UserType.SUPERVISOR, "Math",
				"geoff-beesos");
		dao.createUser(user);

		user = new User("daniel-tubb", "password", "daniel.tubb@test.com", "Daniel", "Tubb", UserType.SUPERVISOR,
				"Engineering", "geoff-beesos");
		dao.createUser(user);

		user = new User("shirly-cord", "password", "shirly.cord@test.com", "Shirly", "Cord", UserType.SUPERVISOR,
				"Science", "geoff-beesos");
		dao.createUser(user);

		// Supervisor
		user = new User("joe-turn", "password", "joe.turn@test.com", "Joe", "Turn", UserType.SUPERVISOR, "Engineering",
				"daniel-tubb");
		dao.createUser(user);

		// Employee
		user = new User("mary-khan", "password", "mary.khan@test.com", "Mary", "Khan", UserType.EMPLOYEE, "Engineering",
				"joe-turn");
		dao.createUser(user);

		// BenCo
		user = new User("sean-jones", "password", "sean.jones@test.com", "Sean", "Jones", UserType.BENEFITS_COORDINATOR,
				"Benefits", "geoff-beesos");
		dao.createUser(user);
	}
}
