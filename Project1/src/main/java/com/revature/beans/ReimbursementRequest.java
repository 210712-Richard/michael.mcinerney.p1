package com.revature.beans;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public class ReimbursementRequest implements Request{
	//Identifiers (Primary keys in the database)
	private Integer id;
	private String username;
	private RequestStatus status;
	
	//Required Fields
	//TODO: Add basic user details
	private LocalDate date;
	private LocalTime time;
	private String location;
	private String description;
	private Double cost;
	private GradingFormat gradingFormat;
	private String passingGrade;
	private EventType type;
	
	//Optional Fields
	//TODO: Do research to see how files should be stored in Java
	private List<String> fileURIs;
	private List<String> approvalMsgsURIs;
	private String workTimeMissed;
	
	//The approval fields , approver IDs, and reimbursement amount
	//Each Approval will be saved as a tuple in the database
	private Double reimburseAmount;
	private Approval supervisorApproval;
	private UUID supervisorID;
	private Approval deptHeadApproval;
	private UUID deptHeadID;
	private Approval benCoApproval;
	private UUID benCoID;
	
	//TODO: Add the final step fields
	//TODO: hashCode, equals, and toString overrides
	
	//TODO: Constructors
	public ReimbursementRequest(){
		super();
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public RequestStatus getStatus() {
		return status;
	}
	public void setStatus(RequestStatus status) {
		this.status = status;
	}
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	public LocalTime getTime() {
		return time;
	}
	public void setTime(LocalTime time) {
		this.time = time;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Double getCost() {
		return cost;
	}
	public void setCost(Double cost) {
		this.cost = cost;
	}
	public GradingFormat getGradingFormat() {
		return gradingFormat;
	}
	public void setGradingFormat(GradingFormat gradingFormat) {
		this.gradingFormat = gradingFormat;
	}
	public String getPassingGrade() {
		return passingGrade;
	}
	public void setPassingGrade(String passingGrade) {
		this.passingGrade = passingGrade;
	}
	public EventType getType() {
		return type;
	}
	public void setType(EventType type) {
		this.type = type;
	}
	public List<String> getFileURIs() {
		return fileURIs;
	}
	public void setFileURIs(List<String> fileURIs) {
		this.fileURIs = fileURIs;
	}
	public List<String> getApprovalMsgsURIs() {
		return approvalMsgsURIs;
	}
	public void setApprovalMsgsURIs(List<String> approvalMsgsURIs) {
		this.approvalMsgsURIs = approvalMsgsURIs;
	}
	public String getWorkTimeMissed() {
		return workTimeMissed;
	}
	public void setWorkTimeMissed(String workTimeMissed) {
		this.workTimeMissed = workTimeMissed;
	}
	public Double getReimburseAmount() {
		return reimburseAmount;
	}
	public void setReimburseAmount(Double reimburseAmount) {
		this.reimburseAmount = reimburseAmount;
	}
	public Approval getSupervisorApproval() {
		return supervisorApproval;
	}
	public void setSupervisorApproval(Approval supervisorApproval) {
		this.supervisorApproval = supervisorApproval;
	}
	public UUID getSupervisorID() {
		return supervisorID;
	}
	public void setSupervisorID(UUID supervisorID) {
		this.supervisorID = supervisorID;
	}
	public Approval getDeptHeadApproval() {
		return deptHeadApproval;
	}
	public void setDeptHeadApproval(Approval deptHeadApproval) {
		this.deptHeadApproval = deptHeadApproval;
	}
	public UUID getDeptHeadID() {
		return deptHeadID;
	}
	public void setDeptHeadID(UUID deptHeadID) {
		this.deptHeadID = deptHeadID;
	}
	public Approval getBenCoApproval() {
		return benCoApproval;
	}
	public void setBenCoApproval(Approval benCoApproval) {
		this.benCoApproval = benCoApproval;
	}
	public UUID getBenCoID() {
		return benCoID;
	}
	public void setBenCoID(UUID benCoID) {
		this.benCoID = benCoID;
	}
	@Override
	public void setSupervisorApprovalStatus(ApprovalStatus status, String reason) {
		this.supervisorApproval.setStatus(status);
		this.supervisorApproval.setReason(reason);
	}
	@Override
	public void setDeptHeadApprovalStatus(ApprovalStatus status, String reason) {
		this.deptHeadApproval.setStatus(status);
		this.deptHeadApproval.setReason(reason);
	}
	@Override
	public void setBenCoApprovalStatus(ApprovalStatus status, String reason) {
		this.benCoApproval.setStatus(status);
		this.benCoApproval.setReason(reason);
	}
}
