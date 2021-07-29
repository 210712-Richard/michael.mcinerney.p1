package com.revature.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class User {
	// The unique ID of the user
	private UUID id;
	// The username of the user
	private String username;
	// The password of the user
	private String password;
	// The email of the user
	private String email;
	// The first name of the user
	private String firstName;
	// The last name of the user
	private String lastName;
	// The user's role in the organization
	private UserType type;
	// The name of the department the user belongs to
	private DepartmentName departmentName;
	// The ID of the user's direct supervisor
	private UUID supervisorID;
	// The balance the user is waiting to get approved
	private Double pendingBalance;
	// The balance the user has already been awarded
	private Double awardedBalance;
	// The List of Requests the user has made
	// In the database, this is a list of ints of the Requests
	private List<Request> requests;
	// The List of Requests the user has to approve
	// For Employees, this is for their requests they need to review if the balance
	// changed by BenCo
	// In the database, this is a list of UUIDs of the Requests
	private List<Request> reviewRequests;

	/**
	 * Generates a random id and sets the balances to 0 and the Request Lists to a empty
	 * Lists
	 */
	public User() {
		super();
		this.id = UUID.randomUUID();
		this.pendingBalance = 0.00;
		this.awardedBalance = 0.00;
		this.requests = new ArrayList<>();
		this.reviewRequests = new ArrayList<>();
	}

	public User(String username, String password, String email, String firstName, String lastName, UserType type,
			DepartmentName departmentName, UUID supervisorID) {
		this();
		this.username = username;
		this.password = password;
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
		this.type = type;
		this.departmentName = departmentName;
		this.supervisorID = supervisorID;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	public UserType getType() {
		return type;
	}

	public void setType(UserType type) {
		this.type = type;
	}

	public DepartmentName getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(DepartmentName departmentName) {
		this.departmentName = departmentName;
	}

	public UUID getSupervisorID() {
		return supervisorID;
	}

	public void setSupervisorID(UUID supervisorID) {
		this.supervisorID = supervisorID;
	}

	public Double getPendingBalance() {
		return pendingBalance;
	}

	public void setPendingBalance(Double pendingBalance) {
		this.pendingBalance = pendingBalance;
	}

	public Double getAwardedBalance() {
		return awardedBalance;
	}

	public void setAwardedBalance(Double awardedBalance) {
		this.awardedBalance = awardedBalance;
	}
	
	public Double getTotalBalance() {
		return pendingBalance + awardedBalance;
	}

	public List<Request> getRequests() {
		return requests;
	}

	public void setRequests(List<Request> requests) {
		this.requests = requests;
	}

	public List<Request> getReviewRequests() {
		return reviewRequests;
	}

	public void setReviewRequests(List<Request> reviewRequests) {
		this.reviewRequests = reviewRequests;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", password=" + password + ", email=" + email
				+ ", firstName=" + firstName + ", lastName=" + lastName + ", departmentName=" + departmentName
				+ ", supervisorID=" + supervisorID + ", pendingBalance=" + pendingBalance + ", awardedBalance="
				+ awardedBalance + ", requests=" + requests + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(awardedBalance, departmentName, email, firstName, id, lastName, password, pendingBalance,
				requests, supervisorID, username);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		return Objects.equals(awardedBalance, other.awardedBalance) && departmentName == other.departmentName
				&& Objects.equals(email, other.email) && Objects.equals(firstName, other.firstName)
				&& Objects.equals(id, other.id) && Objects.equals(lastName, other.lastName)
				&& Objects.equals(password, other.password) && Objects.equals(pendingBalance, other.pendingBalance)
				&& Objects.equals(requests, other.requests) && Objects.equals(supervisorID, other.supervisorID)
				&& Objects.equals(username, other.username);
	}

}
