package com.revature.data;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.DefaultConsistencyLevel;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.cql.SimpleStatementBuilder;
import com.revature.beans.Department;
import com.revature.factory.TraceLog;
import com.revature.util.CassandraUtil;
import com.revature.util.Verifier;

@TraceLog
public class DepartmentDaoImpl implements DepartmentDao {
	private CqlSession session = CassandraUtil.getInstance().getSession();
	private static final Verifier VERIFIER = new Verifier();
	

	@Override
	public Department getDepartment(String deptName) {
		
		//Make sure the deptName is not null
		if (!VERIFIER.verifyNotNull(deptName)) {
			return null;
		}
		//Create the query and bind the parameters
		String query = "SELECT name, deptheadusername FROM department WHERE name = ?;";
		
		SimpleStatement s = new SimpleStatementBuilder(query).build();
		BoundStatement bound = session.prepare(s).bind(deptName);
		
		//Execute the query and get the ResultSet
		ResultSet rs = session.execute(bound);
		Row row = rs.one();
		
		//If the row was null, return null
		if (row == null) {
			return null;
		}
		
		//Set the row's data to a Department and return the Department
		Department dept = new Department();
		dept.setName(row.getString("name"));
		dept.setDeptHeadUsername(row.getString("deptheadusername"));
		return dept;
	}
	
	@Override
	public void createDepartment(Department dept) {
		
		//Create the query and bind to the parameters
		String query = "INSERT INTO department (name, deptheadusername) values (?, ?);";
		SimpleStatement s = new SimpleStatementBuilder(query)
				.setConsistencyLevel(DefaultConsistencyLevel.LOCAL_QUORUM)
				.build();
		BoundStatement bound = session.prepare(s).bind(dept.getName(), dept.getDeptHeadUsername());
		
		//Execute the query
		session.execute(bound);
	}

}
