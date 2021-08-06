package com.revature.data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.DefaultConsistencyLevel;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.cql.SimpleStatementBuilder;
import com.datastax.oss.driver.api.core.data.TupleValue;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.core.type.TupleType;
import com.revature.beans.Approval;
import com.revature.beans.ApprovalStatus;
import com.revature.beans.EventType;
import com.revature.beans.Format;
import com.revature.beans.GradingFormat;
import com.revature.beans.ReimbursementRequest;
import com.revature.beans.Request;
import com.revature.beans.RequestStatus;
import com.revature.factory.TraceLog;
import com.revature.util.CassandraUtil;

@TraceLog
public class RequestDaoImpl implements RequestDao {
	private CqlSession session = CassandraUtil.getInstance().getSession();

	// Tuple types
	private static final TupleType GRADE_TUPLE = DataTypes.tupleOf(DataTypes.TEXT, DataTypes.TEXT);
	private static final TupleType APPROVAL_TUPLE = DataTypes.tupleOf(DataTypes.TEXT, DataTypes.TIMESTAMP,
			DataTypes.TEXT);

	public Request getRequest(UUID id) {
		
		//Create the query and bind the parameters
		StringBuilder query = new StringBuilder("SELECT ")
				.append("id, username, status, isurgent, name, firstname, lastname, ")
				.append("deptname, startdate, starttime, location, description, cost, gradingFormat, ")
				.append("type, fileuris, approvalmsgsuris, worktimemissed, reimburseamount, supervisorapproval, ")
				.append("deptheadapproval, bencoapproval, reason, ")
				.append("finalgrade, ispassing, presfilename, finalapproval, finalreimburseamount, ")
				.append("finalreimburseamountreason, needsemployeereview, employeeagrees ")
				.append("FROM request WHERE id = ?;");
		SimpleStatement s = new SimpleStatementBuilder(query.toString()).build();
		BoundStatement bound = session.prepare(s).bind(id);
		
		//Execute the query and get the result set
		ResultSet rs = session.execute(bound);
		Row row = rs.one();

		//If the row is null, return null
		if (row == null) {
			return null;
		}
		
		//Create a new request using the data from the row and return the request
		Request request = new ReimbursementRequest();
		request.setId(row.getUuid("id"));
		request.setUsername(row.getString("username"));
		request.setStatus(RequestStatus.valueOf(row.getString("status")));
		request.setIsUrgent(row.getBoolean("isurgent"));
		request.setName(row.getString("name"));
		request.setFirstName(row.getString("firstname"));
		request.setLastName(row.getString("lastname"));
		request.setDeptName(row.getString("deptname"));
		request.setStartDate(row.getLocalDate("startdate"));
		request.setStartTime(row.getLocalTime("starttime"));
		request.setLocation(row.getString("location"));
		request.setDescription(row.getString("description"));
		request.setCost(row.getDouble("cost"));
		request.setGradingFormat(
				new GradingFormat(Format.valueOf(row.getTupleValue("gradingformat").get(0, String.class)),
						row.getTupleValue("gradingformat").get(1, String.class)));
		request.setType(EventType.valueOf(row.getString("type")));
		request.setFileURIs(row.getList("fileuris", String.class));
		request.setApprovalMsgsURIs(row.getList("approvalmsgsuris", String.class));
		request.setWorkTimeMissed(row.getString("worktimemissed"));
		request.setReimburseAmount(row.getDouble("reimburseamount"));
		request.setSupervisorApproval(new Approval(
				ApprovalStatus.valueOf(row.getTupleValue("supervisorapproval").get(0, String.class)),
				LocalDateTime.ofInstant(row.getTupleValue("supervisorapproval").get(1, Instant.class), ZoneOffset.UTC),
				row.getTupleValue("supervisorapproval").get(2, String.class)));
		request.setDeptHeadApproval(new Approval(
				ApprovalStatus.valueOf(row.getTupleValue("deptheadapproval").get(0, String.class)),
				LocalDateTime.ofInstant(row.getTupleValue("deptheadapproval").get(1, Instant.class), ZoneOffset.UTC),
				row.getTupleValue("deptheadapproval").get(2, String.class)));
		request.setBenCoApproval(
				new Approval(
						ApprovalStatus.valueOf(row.getTupleValue("bencoapproval").get(0, String.class)), LocalDateTime
								.ofInstant(row.getTupleValue("bencoapproval").get(1, Instant.class), ZoneOffset.UTC),
						row.getTupleValue("bencoapproval").get(2, String.class)));

		request.setReason(row.getString("reason"));
		request.setFinalGrade(row.getString("finalgrade"));
		request.setIsPassing(row.getBoolean("ispassing"));
		request.setPresFileName(row.getString("presfilename"));
		request.setFinalApproval(
				new Approval(
						ApprovalStatus.valueOf(row.getTupleValue("finalapproval").get(0, String.class)), LocalDateTime
								.ofInstant(row.getTupleValue("finalapproval").get(1, Instant.class), ZoneOffset.UTC),
						row.getTupleValue("finalapproval").get(2, String.class)));
		request.setFinalReimburseAmount(row.getDouble("finalreimburseamount"));
		request.setFinalReimburseAmountReason(row.getString("finalreimburseamountreason"));
		request.setNeedsEmployeeReview(row.getBoolean("needsemployeereview"));
		request.setEmployeeAgrees(row.getBoolean("employeeagrees"));
		return request;
	}

	public List<Request> getRequests() {
		//Instantiate a list to return
		List<Request> requests = new ArrayList<Request>();
		
		//Create the query and execute it
		StringBuilder query = new StringBuilder("SELECT ")
				.append("id, username, status, isurgent, name, firstname, lastname, ")
				.append("deptname, startdate, starttime, location, description, cost, gradingFormat, ")
				.append("type, fileuris, approvalmsgsuris, worktimemissed, reimburseamount, supervisorapproval, ")
				.append("deptheadapproval, bencoapproval, reason, ")
				.append("finalgrade, ispassing, presfilename, finalapproval, finalreimburseamount, ")
				.append("finalreimburseamountreason, needsemployeereview, employeeagrees ").append("FROM request;");
		SimpleStatement s = new SimpleStatementBuilder(query.toString()).build();
		ResultSet rs = session.execute(s);
		
		//Loop through each row, create a request, and add the request to the list
		rs.forEach((row) -> {
			Request request = new ReimbursementRequest();
			request.setId(row.getUuid("id"));
			request.setUsername(row.getString("username"));
			request.setStatus(RequestStatus.valueOf(row.getString("status")));
			request.setIsUrgent(row.getBoolean("isurgent"));
			request.setName(row.getString("name"));
			request.setFirstName(row.getString("firstname"));
			request.setLastName(row.getString("lastname"));
			request.setDeptName(row.getString("deptname"));
			request.setStartDate(row.getLocalDate("startdate"));
			request.setStartTime(row.getLocalTime("starttime"));
			request.setLocation(row.getString("location"));
			request.setDescription(row.getString("description"));
			request.setCost(row.getDouble("cost"));
			request.setGradingFormat(
					new GradingFormat(Format.valueOf(row.getTupleValue("gradingformat").get(0, String.class)),
							row.getTupleValue("gradingformat").get(1, String.class)));
			request.setType(EventType.valueOf(row.getString("type")));
			request.setFileURIs(row.getList("fileuris", String.class));
			request.setApprovalMsgsURIs(row.getList("approvalmsgsuris", String.class));
			request.setWorkTimeMissed(row.getString("worktimemissed"));
			request.setReimburseAmount(row.getDouble("reimburseamount"));
			request.setSupervisorApproval(new Approval(
					ApprovalStatus.valueOf(row.getTupleValue("supervisorapproval").get(0, String.class)), LocalDateTime
							.ofInstant(row.getTupleValue("supervisorapproval").get(1, Instant.class), ZoneOffset.UTC),
					row.getTupleValue("supervisorapproval").get(2, String.class)));
			request.setDeptHeadApproval(new Approval(
					ApprovalStatus.valueOf(row.getTupleValue("deptheadapproval").get(0, String.class)), LocalDateTime
							.ofInstant(row.getTupleValue("deptheadapproval").get(1, Instant.class), ZoneOffset.UTC),
					row.getTupleValue("deptheadapproval").get(2, String.class)));
			request.setBenCoApproval(new Approval(
					ApprovalStatus.valueOf(row.getTupleValue("bencoapproval").get(0, String.class)),
					LocalDateTime.ofInstant(row.getTupleValue("bencoapproval").get(1, Instant.class), ZoneOffset.UTC),
					row.getTupleValue("bencoapproval").get(2, String.class)));

			request.setReason(row.getString("reason"));
			request.setFinalGrade(row.getString("finalgrade"));
			request.setIsPassing(row.getBoolean("ispassing"));
			request.setPresFileName(row.getString("presfilename"));
			request.setFinalApproval(new Approval(
					ApprovalStatus.valueOf(row.getTupleValue("finalapproval").get(0, String.class)),
					LocalDateTime.ofInstant(row.getTupleValue("finalapproval").get(1, Instant.class), ZoneOffset.UTC),
					row.getTupleValue("finalapproval").get(2, String.class)));
			request.setFinalReimburseAmount(row.getDouble("finalreimburseamount"));
			request.setFinalReimburseAmountReason(row.getString("finalreimburseamountreason"));
			request.setNeedsEmployeeReview(row.getBoolean("needsemployeereview"));
			request.setEmployeeAgrees(row.getBoolean("employeeagrees"));
			requests.add(request);
		});

		return requests;
	}

	public void updateRequest(Request request) {
		
		//Create the query 
		StringBuilder query = new StringBuilder("UPDATE request SET ")
				.append("status = ?, isurgent = ?, name = ?, firstname = ?, lastname = ?, ")
				.append("deptname = ?, startdate = ?, starttime = ?, location = ?, description = ?, cost = ?, gradingFormat = ?, ")
				.append("type = ?, fileuris = ?, approvalmsgsuris = ?, worktimemissed = ?, reimburseamount = ?, supervisorapproval = ?, ")
				.append("deptheadapproval = ?, bencoapproval = ?, reason = ?, ")
				.append("finalgrade = ?, ispassing = ?, presfilename = ?, finalapproval = ?, finalreimburseamount = ?, ")
				.append("finalreimburseamountreason = ?, needsemployeereview = ?, employeeagrees = ?")
				.append(" WHERE id = ? AND username = ?;");

		SimpleStatement s = new SimpleStatementBuilder(query.toString())
				.setConsistencyLevel(DefaultConsistencyLevel.LOCAL_QUORUM).build();
		// Set the tuple grade
		TupleValue grade = GRADE_TUPLE.newValue(request.getGradingFormat().getFormat().toString(),
				request.getGradingFormat().getPassingGrade());

		// Set the tuple approvals
		TupleValue supervisorApproval = APPROVAL_TUPLE.newValue(request.getSupervisorApproval().getStatus().toString(),
				request.getSupervisorApproval().getDeadline().toInstant(ZoneOffset.UTC),
				request.getSupervisorApproval().getUsername());

		TupleValue deptHeadApproval = APPROVAL_TUPLE.newValue(request.getDeptHeadApproval().getStatus().toString(),
				request.getDeptHeadApproval().getDeadline().toInstant(ZoneOffset.UTC),
				request.getDeptHeadApproval().getUsername());

		TupleValue benCoApproval = APPROVAL_TUPLE.newValue(request.getBenCoApproval().getStatus().toString(),
				request.getBenCoApproval().getDeadline().toInstant(ZoneOffset.UTC),
				request.getBenCoApproval().getUsername());

		TupleValue finalApproval = APPROVAL_TUPLE.newValue(request.getFinalApproval().getStatus().toString(),
				request.getFinalApproval().getDeadline().toInstant(ZoneOffset.UTC),
				request.getFinalApproval().getUsername());

		//Bind the paramters and execute
		BoundStatement bound = session.prepare(s).bind(request.getStatus().toString(), request.getIsUrgent(),
				request.getName(), request.getFirstName(), request.getLastName(), request.getDeptName(),
				request.getStartDate(), request.getStartTime(), request.getLocation(), request.getDescription(),
				request.getCost(), grade, request.getType().toString(), request.getFileURIs(),
				request.getApprovalMsgsURIs(), request.getWorkTimeMissed(), request.getReimburseAmount(),
				supervisorApproval, deptHeadApproval, benCoApproval, request.getReason(), request.getFinalGrade(),
				request.getIsPassing(), request.getPresFileName(), finalApproval, request.getFinalReimburseAmount(),
				request.getFinalReimburseAmountReason(), request.getNeedsEmployeeReview(), request.getEmployeeAgrees(),
				request.getId(), request.getUsername());

		session.execute(bound);
	}

	public void createRequest(Request request) {
		
		//Create the query
		StringBuilder query = new StringBuilder("INSERT INTO request (")
				.append("id, username, status, isurgent, name, firstname, lastname, ")
				.append("deptname, startdate, starttime, location, description, cost, gradingFormat, ")
				.append("type, fileuris, approvalmsgsuris, worktimemissed, reimburseamount, supervisorapproval, ")
				.append("deptheadapproval, bencoapproval, reason, ")
				.append("finalgrade, ispassing, presfilename, finalapproval, finalreimburseamount, ")
				.append("finalreimburseamountreason, needsemployeereview, employeeagrees")
				.append(") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");

		SimpleStatement s = new SimpleStatementBuilder(query.toString())
				.setConsistencyLevel(DefaultConsistencyLevel.LOCAL_QUORUM).build();
		// Set the tuple grade
		TupleValue grade = GRADE_TUPLE.newValue(request.getGradingFormat().getFormat().toString(),
				request.getGradingFormat().getPassingGrade());

		// Set the tuple approvals
		TupleValue supervisorApproval = APPROVAL_TUPLE.newValue(request.getSupervisorApproval().getStatus().toString(),
				request.getSupervisorApproval().getDeadline().toInstant(ZoneOffset.UTC),
				request.getSupervisorApproval().getUsername());

		TupleValue deptHeadApproval = APPROVAL_TUPLE.newValue(request.getDeptHeadApproval().getStatus().toString(),
				request.getDeptHeadApproval().getDeadline().toInstant(ZoneOffset.UTC),
				request.getDeptHeadApproval().getUsername());

		TupleValue benCoApproval = APPROVAL_TUPLE.newValue(request.getBenCoApproval().getStatus().toString(),
				request.getBenCoApproval().getDeadline().toInstant(ZoneOffset.UTC),
				request.getBenCoApproval().getUsername());

		TupleValue finalApproval = APPROVAL_TUPLE.newValue(request.getFinalApproval().getStatus().toString(),
				request.getFinalApproval().getDeadline().toInstant(ZoneOffset.UTC),
				request.getFinalApproval().getUsername());

		
		//Bind the parameters an execute
		BoundStatement bound = session.prepare(s).bind(request.getId(), request.getUsername(),
				request.getStatus().toString(), request.getIsUrgent(), request.getName(), request.getFirstName(),
				request.getLastName(), request.getDeptName(), request.getStartDate(), request.getStartTime(),
				request.getLocation(), request.getDescription(), request.getCost(), grade, request.getType().toString(),
				request.getFileURIs(), request.getApprovalMsgsURIs(), request.getWorkTimeMissed(),
				request.getReimburseAmount(), supervisorApproval, deptHeadApproval, benCoApproval, request.getReason(),
				request.getFinalGrade(), request.getIsPassing(), request.getPresFileName(), finalApproval,
				request.getFinalReimburseAmount(), request.getFinalReimburseAmountReason(),
				request.getNeedsEmployeeReview(), request.getEmployeeAgrees());

		session.execute(bound);
	}

}
