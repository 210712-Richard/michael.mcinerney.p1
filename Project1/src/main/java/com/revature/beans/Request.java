package com.revature.beans;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.List;

public interface Request {
	//The maximum amount that can be reimbursed for the user
	Double MAX_REIMBURSEMENT = 1000.00;
	//The amount of time each approver has to respond to the Request
	TemporalAmount TIME_LIMIT = Duration.of(30, ChronoUnit.MINUTES);
	
	Integer getId();
	void setId(Integer id);
	
	String getUsername();
	void setUsername(String username);
	
	String getFirstName();
	void setFirstName(String firstName);
	
	String getLastName();
	void setLastName(String lastName);
	
	String getDeptName();
	void setDeptName(String deptName);
	
	RequestStatus getStatus();
	void setStatus(RequestStatus status);
	
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
	
	String getSupervisorUsername();
	void setSupervisorUsername(String supervisorUsername);
	
	Approval getDeptHeadApproval();
	void setDeptHeadApproval(Approval supervisorApproval);
	void setDeptHeadApprovalStatus(ApprovalStatus status, String reason);
	
	String getDeptHeadUsername();
	void setDeptHeadUsername(String deptHeadUsername);
	
	Approval getBenCoApproval();
	void setBenCoApproval(Approval benCoApproval);
	void setBenCoApprovalStatus(ApprovalStatus status, String reason);
	
	String getBenCoUsername();
	void setBenCoUsername(String benCoUsername);
	
	String getFinalGrade();
	void setFinalGrade(String finalGrade);
	
	Boolean getIsPassing();
	void setIsPassing(Boolean isPassing);
	
	Approval getFinalApproval();
	void setFinalApproval(Approval finalApproval);
	void setFinalApprovalStatus(ApprovalStatus status, String reason);
	
	String getFinalApprovalUsername();
	void setFinalApprovalUsername(String finalApprovalUsername);
	
	Double getFinalReimburseAmount();
	void setFinalReimburseAmount(Double finalReimburseAmount);
	
	Boolean getNeedsEmployeeReview();
	void setNeedsEmployeeReview(Boolean needsEmployeeReview);
	
}
