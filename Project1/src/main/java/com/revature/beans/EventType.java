package com.revature.beans;

public enum EventType {
	UNIVERSITY_COURSE(0.8), 
	SEMINAR(0.6), 
	CERTIFICATION_PREPERATION_CLASS(0.75),
	CERTIFICATION(1.0), 
	TECHNICAL_TRAINING(0.9), 
	OTHER(0.3);
	
	//How much of the total fee this course can be reimbursed for
	private Double percent;
	
	EventType(Double percent){
		this.percent = percent;
	}
	
	public Double getPercent() {
		return percent;
	}
}
