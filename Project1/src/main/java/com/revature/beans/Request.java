package com.revature.beans;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.List;
import java.util.UUID;

public interface Request {
	//The maximum amount that can be reimbursed for the user
	Double MAX_REIMBURSEMENT = 1000.00;
	//The amount of time each approver has to respond to the Request
	TemporalAmount TIME_LIMIT = Duration.of(30, ChronoUnit.MINUTES);
	
	UUID getId();
	void setId(UUID id);
	
	//TODO: Add basic user details getters and setters
	LocalDate getDate();
	void setDate(LocalDate date);
	
	LocalTime getTime();
	void setTime(LocalTime time);
	
	String getLocation();
	void setLocation(String location);
	
	String getDescription();
	void setDescription(String description);
	
	Double getCost();
	void setCost(Double cost);
	
	GradingFormat getGradingFormat();
	void setGradingFormat(GradingFormat gradingFormat);
	
	String getPassingGrade();
	void setPassingGrade(String passingGrade);
	
	EventType getType();
	void setType(EventType type);
	
	List<String> getFileURIs();
	void setFileURIs(List<String> fileURIs);
	
	List<String> getApprovalMsgsURIs();
	void setApprovalMsgsURIs(List<String> approvalMsgsURIs);
	
	String getWorkTimeMissed();
	void setWorkTimeMissed(String workTimeMissed);
	
	Double getReimburseAmount();
	void setReimburseAmount(Double reimburseAmount);
	
	Approval getSupervisorApproval();
	void setSupervisorApproval(Approval supervisorApproval);
	void setSupervisorApprovalStatus(ApprovalStatus status, String reason);
	
	UUID getSupervisorID();
	void setSupervisorID(UUID supervisorID);
	
	Approval getDeptHeadApproval();
	void setDeptHeadApproval(Approval supervisorApproval);
	void setDeptHeadApprovalStatus(ApprovalStatus status, String reason);
	
	UUID getDeptHeadID();
	void setDeptHeadID(UUID deptHeadID);
	
	Approval getBenCoApproval();
	void setBenCoApproval(Approval supervisorApproval);
	void setBenCoApprovalStatus(ApprovalStatus status, String reason);
	
	UUID getBenCoID();
	void setBenCoID(UUID benCoID);
}
