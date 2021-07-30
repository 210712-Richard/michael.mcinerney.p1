package com.revature.data;

import com.datastax.oss.driver.api.core.CqlSession;
import com.revature.beans.Department;
import com.revature.factory.TraceLog;
import com.revature.util.CassandraUtil;

@TraceLog
public class DepartmentDaoImpl implements DepartmentDao{
	private CqlSession session = CassandraUtil.getInstance().getSession();

	@Override
	public Department getDepartment(String deptName) {
		return null;
	}

	@Override
	public void createDepartment(Department dept) {
	}

}
