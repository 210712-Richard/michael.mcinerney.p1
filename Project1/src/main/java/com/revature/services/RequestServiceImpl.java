package com.revature.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revature.data.RequestDao;
import com.revature.data.RequestDaoImpl;
import com.revature.factory.BeanFactory;
import com.revature.factory.TraceLog;

@TraceLog
public class RequestServiceImpl implements RequestService {
	RequestDao reqDao = (RequestDao) BeanFactory.getFactory().getObject(RequestDao.class,
			RequestDaoImpl.class);
	private static Logger log = LogManager.getLogger(RequestServiceImpl.class);
}
