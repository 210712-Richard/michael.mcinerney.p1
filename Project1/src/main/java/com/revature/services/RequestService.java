package com.revature.services;

import java.time.LocalDate;
import java.time.LocalTime;

import com.revature.beans.EventType;
import com.revature.beans.GradingFormat;
import com.revature.beans.Request;

public interface RequestService {
	public Request createRequest(String username, String firstName, String lastName, String name, LocalDate startDate,
			LocalTime startTime, String location, String description, Double cost, GradingFormat gradingFormat,
			EventType type);
}
