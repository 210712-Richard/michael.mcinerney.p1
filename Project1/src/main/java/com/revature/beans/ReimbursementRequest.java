package com.revature.beans;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

public class ReimbursementRequest implements Request {
	// Identifiers (Primary keys in the database)
	private Integer id;
	private String username;
	private RequestStatus status;
	
	//Fields set by the service
	private Boolean isUrgent;


	// Required Fields
	private String name;
	private String firstName;
	private String lastName;
	private String deptName;
	private LocalDate startDate;
	private LocalTime startTime;
	private String location;
	private String description;
	private Double cost;
	private GradingFormat gradingFormat;
	private EventType type;

	// Optional Fields
	private List<String> fileURIs;
	private List<String> approvalMsgsURIs;
	private String workTimeMissed;

	// The approval fields , approver IDs, and reimbursement amount
	// Each Approval will be saved as a tuple in the database
	private Double reimburseAmount;
	private Approval supervisorApproval;
	private String supervisorUsername;
	private Approval deptHeadApproval;
	private String deptHeadUsername;
	private Approval benCoApproval;
	private String benCoUsername;

	// The final step fields
	private String finalGrade;
	private Boolean isPassing;
	private String presFileName;
	private Approval finalApproval;
	private String finalApprovalUsername;
	private Double finalReimburseAmount;
	private String finalReimburseAmountReason;
	private Boolean needsEmployeeReview;
	private Boolean employeeAgrees;



	public ReimbursementRequest() {
		super();
		status = RequestStatus.ACTIVE;
		isUrgent = false;
	}

	public ReimbursementRequest(Integer id, String username, RequestStatus status, String firstName, String lastName,
			String deptName, LocalDate date, LocalTime time, String location, String description, Double cost,
			GradingFormat gradingFormat, EventType type, List<String> fileURIs, List<String> approvalMsgsURIs,
			String workTimeMissed) {
		this();
		this.id = id;
		this.username = username;
		this.status = status;
		this.firstName = firstName;
		this.lastName = lastName;
		this.deptName = deptName;
		this.startDate = date;
		this.startTime = time;
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
	
	public Boolean getEmployeeAgrees() {
		return employeeAgrees;
	}

	public void setEmployeeAgrees(Boolean employeeAgrees) {
		this.employeeAgrees = employeeAgrees;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public Boolean getIsUrgent() {
		return isUrgent;
	}

	public void setIsUrgent(Boolean isUrgent) {
		this.isUrgent = isUrgent;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public RequestStatus getStatus() {
		return status;
	}

	public void setStatus(RequestStatus status) {
		this.status = status;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate date) {
		this.startDate = date;
	}

	public LocalTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalTime time) {
		this.startTime = time;
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

	public Approval getDeptHeadApproval() {
		return deptHeadApproval;
	}

	public void setDeptHeadApproval(Approval deptHeadApproval) {
		this.deptHeadApproval = deptHeadApproval;
	}

	public Approval getBenCoApproval() {
		return benCoApproval;
	}

	public void setBenCoApproval(Approval benCoApproval) {
		this.benCoApproval = benCoApproval;
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

	public Boolean getIsPassing() {
		return isPassing;
	}

	public void setIsPassing(Boolean isPassing) {
		this.isPassing = isPassing;
	}

	public String getPresFileName() {
		return presFileName;
	}

	public void setPresFileName(String presFileName) {
		this.presFileName = presFileName;
	}

	public String getSupervisorUsername() {
		return supervisorUsername;
	}

	public void setSupervisorUsername(String supervisorUsername) {
		this.supervisorUsername = supervisorUsername;
	}

	public String getDeptHeadUsername() {
		return deptHeadUsername;
	}

	public void setDeptHeadUsername(String deptHeadUsername) {
		this.deptHeadUsername = deptHeadUsername;
	}

	public String getBenCoUsername() {
		return benCoUsername;
	}

	public void setBenCoUsername(String benCoUsername) {
		this.benCoUsername = benCoUsername;
	}

	public String getFinalApprovalUsername() {
		return finalApprovalUsername;
	}

	public void setFinalApprovalUsername(String finalApprovalUsername) {
		this.finalApprovalUsername = finalApprovalUsername;
	}

	public String getFinalReimburseAmountReason() {
		return finalReimburseAmountReason;
	}

	public void setFinalReimburseAmountReason(String finalReimburseAmountReason) {
		this.finalReimburseAmountReason = finalReimburseAmountReason;
	}

	@Override
	public int hashCode() {
		return Objects.hash(approvalMsgsURIs, benCoApproval, benCoUsername, cost, deptHeadApproval, deptHeadUsername,
				deptName, description, employeeAgrees, fileURIs, finalApproval, finalApprovalUsername, finalGrade,
				finalReimburseAmount, finalReimburseAmountReason, firstName, gradingFormat, id, isPassing, isUrgent,
				lastName, location, name, needsEmployeeReview, presFileName, reimburseAmount, startDate, startTime,
				status, supervisorApproval, supervisorUsername, type, username, workTimeMissed);
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
				&& Objects.equals(benCoApproval, other.benCoApproval)
				&& Objects.equals(benCoUsername, other.benCoUsername) && Objects.equals(cost, other.cost)
				&& Objects.equals(deptHeadApproval, other.deptHeadApproval)
				&& Objects.equals(deptHeadUsername, other.deptHeadUsername) && Objects.equals(deptName, other.deptName)
				&& Objects.equals(description, other.description)
				&& Objects.equals(employeeAgrees, other.employeeAgrees) && Objects.equals(fileURIs, other.fileURIs)
				&& Objects.equals(finalApproval, other.finalApproval)
				&& Objects.equals(finalApprovalUsername, other.finalApprovalUsername)
				&& Objects.equals(finalGrade, other.finalGrade)
				&& Objects.equals(finalReimburseAmount, other.finalReimburseAmount)
				&& Objects.equals(finalReimburseAmountReason, other.finalReimburseAmountReason)
				&& Objects.equals(firstName, other.firstName) && Objects.equals(gradingFormat, other.gradingFormat)
				&& Objects.equals(id, other.id) && Objects.equals(isPassing, other.isPassing)
				&& Objects.equals(isUrgent, other.isUrgent) && Objects.equals(lastName, other.lastName)
				&& Objects.equals(location, other.location) && Objects.equals(name, other.name)
				&& Objects.equals(needsEmployeeReview, other.needsEmployeeReview)
				&& Objects.equals(presFileName, other.presFileName)
				&& Objects.equals(reimburseAmount, other.reimburseAmount) && Objects.equals(startDate, other.startDate)
				&& Objects.equals(startTime, other.startTime) && status == other.status
				&& Objects.equals(supervisorApproval, other.supervisorApproval)
				&& Objects.equals(supervisorUsername, other.supervisorUsername) && type == other.type
				&& Objects.equals(username, other.username) && Objects.equals(workTimeMissed, other.workTimeMissed);
	}

	@Override
	public String toString() {
		return "ReimbursementRequest [id=" + id + ", username=" + username + ", status=" + status + ", isUrgent="
				+ isUrgent + ", firstName=" + firstName + ", lastName=" + lastName + ", deptName=" + deptName
				+ ", date=" + startDate + ", time=" + startTime + ", location=" + location + ", description=" + description
				+ ", cost=" + cost + ", gradingFormat=" + gradingFormat + ", type=" + type + ", fileURIs=" + fileURIs
				+ ", approvalMsgsURIs=" + approvalMsgsURIs + ", workTimeMissed=" + workTimeMissed + ", reimburseAmount="
				+ reimburseAmount + ", supervisorApproval=" + supervisorApproval + ", supervisorUsername="
				+ supervisorUsername + ", deptHeadApproval=" + deptHeadApproval + ", deptHeadUsername="
				+ deptHeadUsername + ", benCoApproval=" + benCoApproval + ", benCoUsername=" + benCoUsername
				+ ", finalGrade=" + finalGrade + ", isPassing=" + isPassing + ", presFileName=" + presFileName
				+ ", finalApproval=" + finalApproval + ", finalApprovalUsername=" + finalApprovalUsername
				+ ", finalReimburseAmount=" + finalReimburseAmount + ", finalReimburseAmountReason="
				+ finalReimburseAmountReason + ", needsEmployeeReview=" + needsEmployeeReview + ", employeeAgrees="
				+ employeeAgrees + "]";
	}

}
