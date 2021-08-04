package com.revature.beans;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.List;
import java.util.UUID;

public interface Request {
	// The maximum amount that can be reimbursed for the user
	Double MAX_REIMBURSEMENT = 1000.00;
	// The amount of time each approver has to respond to the Request
	TemporalAmount TIME_LIMIT = Duration.of(30, ChronoUnit.MINUTES);
	//Used to set a placeholder value to the Approval deadline
	LocalDateTime PLACEHOLDER = LocalDateTime.of(LocalDate.of(2020, 12, 31), LocalTime.of(0, 0));

	UUID getId();

	void setId(UUID id);

	String getUsername();

	void setUsername(String username);

	String getName();

	void setName(String name);

	String getFirstName();

	void setFirstName(String firstName);

	String getLastName();

	void setLastName(String lastName);

	String getDeptName();

	void setDeptName(String deptName);

	RequestStatus getStatus();

	void setStatus(RequestStatus status);

	Boolean getIsUrgent();

	void setIsUrgent(Boolean isUrgent);

	Boolean getEmployeeAgrees();

	void setEmployeeAgrees(Boolean employeeAgrees);

	LocalDate getStartDate();

	void setStartDate(LocalDate date);

	LocalTime getStartTime();

	void setStartTime(LocalTime time);

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

	Approval getDeptHeadApproval();

	void setDeptHeadApproval(Approval supervisorApproval);

	Approval getBenCoApproval();

	void setBenCoApproval(Approval benCoApproval);
	
	String getReason();
	void setReason(String reason);

	String getFinalGrade();

	void setFinalGrade(String finalGrade);

	Boolean getIsPassing();

	void setIsPassing(Boolean isPassing);

	Approval getFinalApproval();

	void setFinalApproval(Approval finalApproval);

	void setFinalApprovalStatus(ApprovalStatus status, String reason);

	Double getFinalReimburseAmount();

	void setFinalReimburseAmount(Double finalReimburseAmount);

	Boolean getNeedsEmployeeReview();

	void setNeedsEmployeeReview(Boolean needsEmployeeReview);

	String getPresFileName();

	void setPresFileName(String presFileName);

	String getFinalReimburseAmountReason();

	void setFinalReimburseAmountReason(String finalReimburseAmountReason);

}
