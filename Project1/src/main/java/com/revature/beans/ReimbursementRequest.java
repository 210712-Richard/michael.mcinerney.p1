package com.revature.beans;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ReimbursementRequest implements Request {
	// Identifiers (Primary keys in the database)
	private Integer id;
	private String username;
	private RequestStatus status;

	// Required Fields
	private String firstName;
	private String lastName;
	private DepartmentName deptName;
	private LocalDate date;
	private LocalTime time;
	private String location;
	private String description;
	private Double cost;
	private GradingFormat gradingFormat;
	private EventType type;

	// Optional Fields
	// TODO: Do research to see how files should be stored in Java
	private List<String> fileURIs;
	private List<String> approvalMsgsURIs;
	private String workTimeMissed;

	// The approval fields , approver IDs, and reimbursement amount
	// Each Approval will be saved as a tuple in the database
	private Double reimburseAmount;
	private Approval supervisorApproval;
	private UUID supervisorID;
	private Approval deptHeadApproval;
	private UUID deptHeadID;
	private Approval benCoApproval;
	private UUID benCoID;

	// The final step fields
	private String finalGrade;
	private String presFileName;
	private Approval finalApproval;
	private UUID finalApprovalID;
	private Double finalReimburseAmount;
	private Boolean needsEmployeeReview;

	public ReimbursementRequest() {
		super();
		status = RequestStatus.ACTIVE;
	}

	public ReimbursementRequest(Integer id, String username, RequestStatus status, String firstName, String lastName,
			DepartmentName deptName, LocalDate date, LocalTime time, String location, String description, Double cost,
			GradingFormat gradingFormat, EventType type, List<String> fileURIs,
			List<String> approvalMsgsURIs, String workTimeMissed) {
		this();
		this.id = id;
		this.username = username;
		this.status = status;
		this.firstName = firstName;
		this.lastName = lastName;
		this.deptName = deptName;
		this.date = date;
		this.time = time;
		this.location = location;
		this.description = description;
		this.cost = cost;
		this.gradingFormat = gradingFormat;
		this.type = type;
		this.fileURIs = fileURIs;
		this.approvalMsgsURIs = approvalMsgsURIs;
		this.workTimeMissed = workTimeMissed;
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

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public DepartmentName getDeptName() {
		return deptName;
	}

	public void setDeptName(DepartmentName deptName) {
		this.deptName = deptName;
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

	public String getFinalGrade() {
		return finalGrade;
	}

	public void setFinalGrade(String finalGrade) {
		this.finalGrade = finalGrade;
	}

	public Approval getFinalApproval() {
		return finalApproval;
	}

	public void setFinalApproval(Approval finalApproval) {
		this.finalApproval = finalApproval;
	}

	@Override
	public void setFinalApprovalStatus(ApprovalStatus status, String reason) {
		this.finalApproval.setStatus(status);
		this.finalApproval.setReason(reason);
	}

	public UUID getFinalApprovalID() {
		return finalApprovalID;
	}

	public void setFinalApprovalID(UUID finalApprovalID) {
		this.finalApprovalID = finalApprovalID;
	}

	public Double getFinalReimburseAmount() {
		return finalReimburseAmount;
	}

	public void setFinalReimburseAmount(Double finalReimburseAmount) {
		this.finalReimburseAmount = finalReimburseAmount;
	}

	public Boolean getNeedsEmployeeReview() {
		return needsEmployeeReview;
	}

	public void setNeedsEmployeeReview(Boolean needsEmployeeReview) {
		this.needsEmployeeReview = needsEmployeeReview;
	}

	@Override
	public int hashCode() {
		return Objects.hash(approvalMsgsURIs, benCoApproval, benCoID, cost, date, deptHeadApproval, deptHeadID,
				deptName, description, fileURIs, finalApproval, finalApprovalID, finalGrade, finalReimburseAmount,
				firstName, gradingFormat, id, lastName, location, needsEmployeeReview, presFileName, reimburseAmount,
				status, supervisorApproval, supervisorID, time, type, username, workTimeMissed);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReimbursementRequest other = (ReimbursementRequest) obj;
		return Objects.equals(approvalMsgsURIs, other.approvalMsgsURIs)
				&& Objects.equals(benCoApproval, other.benCoApproval) && Objects.equals(benCoID, other.benCoID)
				&& Objects.equals(cost, other.cost) && Objects.equals(date, other.date)
				&& Objects.equals(deptHeadApproval, other.deptHeadApproval)
				&& Objects.equals(deptHeadID, other.deptHeadID) && deptName == other.deptName
				&& Objects.equals(description, other.description) && Objects.equals(fileURIs, other.fileURIs)
				&& Objects.equals(finalApproval, other.finalApproval)
				&& Objects.equals(finalApprovalID, other.finalApprovalID)
				&& Objects.equals(finalGrade, other.finalGrade)
				&& Objects.equals(finalReimburseAmount, other.finalReimburseAmount)
				&& Objects.equals(firstName, other.firstName) && Objects.equals(gradingFormat, other.gradingFormat)
				&& Objects.equals(id, other.id) && Objects.equals(lastName, other.lastName)
				&& Objects.equals(location, other.location)
				&& Objects.equals(needsEmployeeReview, other.needsEmployeeReview)
				&& Objects.equals(presFileName, other.presFileName)
				&& Objects.equals(reimburseAmount, other.reimburseAmount) && status == other.status
				&& Objects.equals(supervisorApproval, other.supervisorApproval)
				&& Objects.equals(supervisorID, other.supervisorID) && Objects.equals(time, other.time)
				&& type == other.type && Objects.equals(username, other.username)
				&& Objects.equals(workTimeMissed, other.workTimeMissed);
	}

	@Override
	public String toString() {
		return "ReimbursementRequest [id=" + id + ", username=" + username + ", status=" + status + ", firstName="
				+ firstName + ", lastName=" + lastName + ", deptName=" + deptName + ", date=" + date + ", time=" + time
				+ ", location=" + location + ", description=" + description + ", cost=" + cost + ", gradingFormat="
				+ gradingFormat + ", type=" + type + ", fileURIs=" + fileURIs + ", approvalMsgsURIs=" + approvalMsgsURIs
				+ ", workTimeMissed=" + workTimeMissed + ", reimburseAmount=" + reimburseAmount
				+ ", supervisorApproval=" + supervisorApproval + ", supervisorID=" + supervisorID
				+ ", deptHeadApproval=" + deptHeadApproval + ", deptHeadID=" + deptHeadID + ", benCoApproval="
				+ benCoApproval + ", benCoID=" + benCoID + ", finalGrade=" + finalGrade + ", finalApproval="
				+ finalApproval + ", finalApprovalID=" + finalApprovalID + ", finalReimburseAmount="
				+ finalReimburseAmount + ", needsEmployeeReview=" + needsEmployeeReview + "]";
	}
}
