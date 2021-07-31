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

	// Required Fields
	private String firstName;
	private String lastName;
	private String deptName;
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
	private Boolean needsEmployeeReview;

	public ReimbursementRequest() {
		super();
		status = RequestStatus.ACTIVE;
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

	@Override
	public int hashCode() {
		return Objects.hash(approvalMsgsURIs, benCoApproval, benCoUsername, cost, date, deptHeadApproval,
				deptHeadUsername, deptName, description, fileURIs, finalApproval, finalApprovalUsername, finalGrade,
				finalReimburseAmount, firstName, gradingFormat, id, isPassing, lastName, location, needsEmployeeReview,
				presFileName, reimburseAmount, status, supervisorApproval, supervisorUsername, time, type, username,
				workTimeMissed);
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
				&& Objects.equals(date, other.date) && Objects.equals(deptHeadApproval, other.deptHeadApproval)
				&& Objects.equals(deptHeadUsername, other.deptHeadUsername) && Objects.equals(deptName, other.deptName)
				&& Objects.equals(description, other.description) && Objects.equals(fileURIs, other.fileURIs)
				&& Objects.equals(finalApproval, other.finalApproval)
				&& Objects.equals(finalApprovalUsername, other.finalApprovalUsername)
				&& Objects.equals(finalGrade, other.finalGrade)
				&& Objects.equals(finalReimburseAmount, other.finalReimburseAmount)
				&& Objects.equals(firstName, other.firstName) && Objects.equals(gradingFormat, other.gradingFormat)
				&& Objects.equals(id, other.id) && Objects.equals(isPassing, other.isPassing)
				&& Objects.equals(lastName, other.lastName) && Objects.equals(location, other.location)
				&& Objects.equals(needsEmployeeReview, other.needsEmployeeReview)
				&& Objects.equals(presFileName, other.presFileName)
				&& Objects.equals(reimburseAmount, other.reimburseAmount) && status == other.status
				&& Objects.equals(supervisorApproval, other.supervisorApproval)
				&& Objects.equals(supervisorUsername, other.supervisorUsername) && Objects.equals(time, other.time)
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
				+ ", supervisorApproval=" + supervisorApproval + ", supervisorUsername=" + supervisorUsername
				+ ", deptHeadApproval=" + deptHeadApproval + ", deptHeadUsername=" + deptHeadUsername
				+ ", benCoApproval=" + benCoApproval + ", benCoUsername=" + benCoUsername + ", finalGrade=" + finalGrade
				+ ", isPassing=" + isPassing + ", presFileName=" + presFileName + ", finalApproval=" + finalApproval
				+ ", finalApprovalUsername=" + finalApprovalUsername + ", finalReimburseAmount=" + finalReimburseAmount
				+ ", needsEmployeeReview=" + needsEmployeeReview + "]";
	}

}
