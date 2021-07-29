package com.revature.data;

import com.datastax.oss.driver.api.core.CqlSession;
import com.revature.factory.TraceLog;
import com.revature.util.CassandraUtil;

@TraceLog
public class RequestDaoImpl implements RequestDao {
	private CqlSession session = CassandraUtil.getInstance().getSession();

}
