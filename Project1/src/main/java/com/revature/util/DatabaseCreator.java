package com.revature.util;

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
				.append("id uuid, username text, password text, email text, firstName text, ")
				.append("lastName text, type text, departmentName text, supervisorID uuid, ")
				.append("pendingBalance double, awardedBalance double, requests list<int>, ")
				.append("reviewRequests list<int>,")
				.append("primary key(id, username, password));");
		CassandraUtil.getInstance().getSession().execute(query.toString());
		
		query = new StringBuilder("CREATE TABLE IF NOT EXISTS Request (")
				.append("id int, username text, status text, firstName text, lastName text, ")
				.append("deptName text, startDate date, startTime time, location text, ")
				.append("description text, cost double, gradingFormat tuple<text, text>, ")
				.append("type text, fileURIs List<text>, approvalMsgsURIs List<text>, workTimeMissed text, ")
				.append("reimburseAmount double, supervisorApproval tuple<text, timestamp, text>, ")
				.append("supervisorID uuid, deptHeadApproval tuple<text, timestamp, text>, ")
				.append("deptHeadID uuid, benCoApproval tuple<text, timestamp, text>, benCoID uuid, ")
				.append("finalGrade text, isPassing boolean, presFileName text, finalApproval tuple<text, timestamp, text>, ")
				.append("finalApprovalID uuid, finalReimburseAmount double, needsEmployeeReview boolean, ")
				.append("primary key(id, username));");
		CassandraUtil.getInstance().getSession().execute(query.toString());
		
		query = new StringBuilder("CREATE TABLE IF NOT EXISTS Department (")
				.append("name text PRIMARY KEY, deptHeadId uuid")
				.append(");");
		CassandraUtil.getInstance().getSession().execute(query.toString());
	}
}
