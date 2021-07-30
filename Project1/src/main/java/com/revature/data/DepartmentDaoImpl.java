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

@TraceLog
public class DepartmentDaoImpl implements DepartmentDao {
	private CqlSession session = CassandraUtil.getInstance().getSession();
	

	@Override
	public Department getDepartment(String deptName) {
		
		if (deptName == null) {
			return null;
		}
		String query = "SELECT name, deptheadid FROM department WHERE name = ?;";
		
		SimpleStatement s = new SimpleStatementBuilder(query).build();
		BoundStatement bound = session.prepare(s).bind(deptName);
		ResultSet rs = session.execute(bound);
		Row row = rs.one();
		
		//This means the user was not found
		if (row == null) {
			return null;
		}
		Department dept = new Department();
		dept.setName(row.getString("name"));
		dept.setDeptHeadId(row.getUuid("deptheadid"));
		return dept;
	}

	public void createDepartment(Department dept) {
		String query = "INSERT INTO department (name, deptHeadId) values (?, ?);";

		SimpleStatement s = new SimpleStatementBuilder(query)
				.setConsistencyLevel(DefaultConsistencyLevel.LOCAL_QUORUM)
				.build();
		BoundStatement bound = session.prepare(s).bind(dept.getName(), dept.getDeptHeadId());
		session.execute(bound);
	}

}
