package com.revature.util;

import com.revature.beans.Department;
import com.revature.data.DepartmentDao;
import com.revature.data.DepartmentDaoImpl;

public class DatabaseCreator {
	
	
	public static void dropTables() {
		StringBuilder query = new StringBuilder("DROP TABLE IF EXISTS User;");
		CassandraUtil.getInstance().getSession().execute(query.toString());
		
		query = new StringBuilder("DROP TABLE IF EXISTS Request;");
		CassandraUtil.getInstance().getSession().execute(query.toString());
		
		query = new StringBuilder("DROP TABLE IF EXISTS Department;");
		CassandraUtil.getInstance().getSession().execute(query.toString());
	}
	
	public static void createTables() {
		StringBuilder query = new StringBuilder("CREATE TABLE IF NOT EXISTS User (")
				.append("username text, password text, email text, firstName text, ")
				.append("lastName text, type text, departmentName text, supervisorUsername text, ")
				.append("pendingBalance double, awardedBalance double, requests list<int>, ")
				.append("reviewRequests list<int>,")
				.append("primary key(username, password));");
		CassandraUtil.getInstance().getSession().execute(query.toString());
		
		query = new StringBuilder("CREATE TABLE IF NOT EXISTS Request (")
				.append("id int, username text, status text, firstName text, lastName text, ")
				.append("deptName text, startDate date, startTime time, location text, ")
				.append("description text, cost double, gradingFormat tuple<text, text>, ")
				.append("type text, fileURIs List<text>, approvalMsgsURIs List<text>, workTimeMissed text, ")
				.append("reimburseAmount double, supervisorApproval tuple<text, timestamp, text>, ")
				.append("supervisorUsername text, deptHeadApproval tuple<text, timestamp, text>, ")
				.append("deptHeadUsername text, benCoApproval tuple<text, timestamp, text>, benCoUsername text, ")
				.append("finalGrade text, isPassing boolean, presFileName text, finalApproval tuple<text, timestamp, text>, ")
				.append("finalApprovalUsername text, finalReimburseAmount double, needsEmployeeReview boolean, ")
				.append("primary key(id, username));");
		CassandraUtil.getInstance().getSession().execute(query.toString());
		
		query = new StringBuilder("CREATE TABLE IF NOT EXISTS Department (")
				.append("name text PRIMARY KEY, deptHeadUsername text")
				.append(");");
		CassandraUtil.getInstance().getSession().execute(query.toString());
	}
	
	public static void populateDepartment() {
		DepartmentDao dao = new DepartmentDaoImpl();
		Department dept = new Department("Test", "TestHead");
		dao.createDepartment(dept);
	}
	
	public static void populateUser() {
		
	}
	
	public static void populateRequest() {
		
	}
}
