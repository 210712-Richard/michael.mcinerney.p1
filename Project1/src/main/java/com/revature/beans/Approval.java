package com.revature.beans;

import java.time.LocalDateTime;
import java.util.Objects;

public class Approval {
	private ApprovalStatus status;
	private LocalDateTime deadline;
	private String username;

	public Approval() {
		super();
		this.status = ApprovalStatus.AWAITING;
		this.deadline = Request.PLACEHOLDER;
	}

	public Approval(ApprovalStatus status, String username) {
		this();
		this.status = status;
		this.username = username;
	}

	public Approval(ApprovalStatus status, LocalDateTime deadline, String reason) {
		this.status = status;
		this.deadline = deadline;
		this.username = reason;
	}

	public LocalDateTime getDeadline() {
		return deadline;
	}

	public void setDeadline(LocalDateTime deadline) {
		this.deadline = deadline;
	}
	
	public void startDeadline() {
		this.deadline = LocalDateTime.now().plus(Request.TIME_LIMIT);
	}

	public ApprovalStatus getStatus() {
		return status;
	}

	public void setStatus(ApprovalStatus status) {
		this.status = status;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String toString() {
		return "Approval [status=" + status + ", deadline=" + deadline + ", username=" + username + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(deadline, username, status);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Approval other = (Approval) obj;
		return Objects.equals(deadline, other.deadline) && Objects.equals(username, other.username)
				&& status == other.status;
	}

}
