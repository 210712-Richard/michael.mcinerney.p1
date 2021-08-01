package com.revature.data;

import java.time.ZoneOffset;
import java.util.List;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.DefaultConsistencyLevel;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.cql.SimpleStatementBuilder;
import com.datastax.oss.driver.api.core.data.TupleValue;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.core.type.TupleType;
import com.revature.beans.Request;
import com.revature.factory.TraceLog;
import com.revature.util.CassandraUtil;

@TraceLog
public class RequestDaoImpl implements RequestDao {
	private CqlSession session = CassandraUtil.getInstance().getSession();

	// Tuple types
	private static final TupleType GRADE_TUPLE = DataTypes.tupleOf(DataTypes.TEXT, DataTypes.TEXT);
	private static final TupleType APPROVAL_TUPLE = DataTypes.tupleOf(DataTypes.TEXT, DataTypes.TIMESTAMP,
			DataTypes.TEXT);

	public Request getRequest(Integer id) {
		return null;
	}

	public List<Request> getRequests() {
		return null;
	}

	public void updateRequest(Request request) {
		request.getApprovalMsgsURIs();
	}

	public void createRequest(Request request) {
		StringBuilder query = new StringBuilder("INSERT INTO request (")
				.append("id, username, status, isurgent, name, firstname, lastname, ")
				.append("deptname, startdate, starttime, location, description, cost, gradingFormat, ")
				.append("type, fileuris, approvalmsgsuris, worktimemissed, reimburseamount, supervisorapproval, ")
				.append("supervisorusername, deptheadapproval, deptheadusername, bencoapproval, bencousername, ")
				.append("finalgrade, ispassing, presfilename, finalapproval, finalapprovalusername, finalreimburseamount, ")
				.append("finalreimburseamountreason, needsemployeereview, employeeagrees")
				.append(") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");

		SimpleStatement s = new SimpleStatementBuilder(query.toString())
				.setConsistencyLevel(DefaultConsistencyLevel.LOCAL_QUORUM).build();
		//Set the tuple grade
		TupleValue grade = GRADE_TUPLE.newValue(request.getGradingFormat().toString(),
				request.getGradingFormat().getPassingGrade());
		
		//Set the tuple approvals
		TupleValue supervisorApproval = APPROVAL_TUPLE.newValue(request.getSupervisorApproval().getStatus().toString(),
				request.getSupervisorApproval().getDeadline().toInstant(ZoneOffset.UTC), request.getSupervisorApproval().getReason());
		
		TupleValue deptHeadApproval = APPROVAL_TUPLE.newValue(request.getDeptHeadApproval().getStatus().toString(),
				request.getDeptHeadApproval().getDeadline().toInstant(ZoneOffset.UTC), request.getDeptHeadApproval().getReason());
		
		TupleValue benCoApproval = APPROVAL_TUPLE.newValue(request.getBenCoApproval().getStatus().toString(),
				request.getBenCoApproval().getDeadline().toInstant(ZoneOffset.UTC), request.getBenCoApproval().getReason());
		
		TupleValue finalApproval = APPROVAL_TUPLE.newValue(request.getFinalApproval().getStatus().toString(),
				request.getFinalApproval().getDeadline().toInstant(ZoneOffset.UTC), request.getFinalApproval().getReason());
		
		
		BoundStatement bound = session.prepare(s).bind(request.getId(), request.getUsername(),
				request.getStatus().toString(), request.getIsUrgent(), request.getName(), request.getFirstName(),
				request.getLastName(), request.getDeptName(), request.getStartDate(), request.getStartTime(),
				request.getLocation(), request.getDescription(), request.getCost(), grade,
				request.getType().toString(), request.getFileURIs(), request.getApprovalMsgsURIs(),
				request.getWorkTimeMissed(), request.getReimburseAmount(), supervisorApproval, request.getSupervisorUsername(),
				deptHeadApproval, request.getDeptHeadUsername(), benCoApproval, request.getBenCoUsername(), request.getFinalGrade(), 
				request.getIsPassing(), request.getPresFileName(), finalApproval, request.getFinalApprovalUsername(), request.getFinalReimburseAmount(), 
				request.getFinalReimburseAmountReason(), request.getNeedsEmployeeReview(), request.getEmployeeAgrees());
		
		session.execute(bound);
	}

}
