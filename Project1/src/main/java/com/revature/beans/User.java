package com.revature.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class User {
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
	private String departmentName;
	// The ID of the user's direct supervisor
	private String supervisorUsername;
	// The balance the user is waiting to get approved
	private Double pendingBalance;
	// The balance the user has already been awarded
	private Double awardedBalance;
	// The List of Request IDs from requests the user has made
	private List<UUID> requests;
	// The List of Notifications the user has to read
	private List<Notification> notifications;

	/**
	 * Generates a random id and sets the balances to 0 and the Request Lists to a
	 * empty Lists
	 */
	public User() {
		super();
		this.pendingBalance = 0.00;
		this.awardedBalance = 0.00;
		this.requests = new ArrayList<>();
	}

	public User(String username, String password, String email, String firstName, String lastName, UserType type,
			String departmentName, String supervisorUsername) {
		this();
		this.username = username;
		this.password = password;
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
		this.type = type;
		this.departmentName = departmentName;
		this.supervisorUsername = supervisorUsername;
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

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public String getSupervisorUsername() {
		return supervisorUsername;
	}

	public void setSupervisorUsername(String supervisorUsername) {
		this.supervisorUsername = supervisorUsername;
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

	public List<UUID> getRequests() {
		return requests;
	}

	public void setRequests(List<UUID> requests) {
		this.requests = requests;
	}

	public List<Notification> getNotifications() {
		return notifications;
	}

	public void setNotifications(List<Notification> notifications) {
		this.notifications = notifications;
	}

	@Override
	public String toString() {
		return "User [username=" + username + ", password=" + password + ", email=" + email + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", type=" + type + ", departmentName=" + departmentName
				+ ", supervisorUsername=" + supervisorUsername + ", pendingBalance=" + pendingBalance
				+ ", awardedBalance=" + awardedBalance + ", requests=" + requests + ", notifications=" + notifications
				+ "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(awardedBalance, departmentName, email, firstName, lastName, notifications, password,
				pendingBalance, requests, supervisorUsername, type, username);
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
		return Objects.equals(awardedBalance, other.awardedBalance)
				&& Objects.equals(departmentName, other.departmentName) && Objects.equals(email, other.email)
				&& Objects.equals(firstName, other.firstName) && Objects.equals(lastName, other.lastName)
				&& Objects.equals(notifications, other.notifications) && Objects.equals(password, other.password)
				&& Objects.equals(pendingBalance, other.pendingBalance) && Objects.equals(requests, other.requests)
				&& Objects.equals(supervisorUsername, other.supervisorUsername) && type == other.type
				&& Objects.equals(username, other.username);
	}

	public void alterPendingBalance(Double cost) {
		this.pendingBalance += cost;
	}

	public void alterAwardedBalance(Double cost) {
		this.awardedBalance += cost;
	}

}
