package com.revature.beans;

import java.time.LocalDateTime;
import java.util.Objects;

public class Approval {
	private ApprovalStatus status;
	private LocalDateTime deadline;
	private String reason;

	public Approval() {
		super();
		this.status = ApprovalStatus.AWAITING;
		this.deadline = LocalDateTime.now().plus(Request.TIME_LIMIT);
	}

	public Approval(ApprovalStatus status, String reason) {
		this();
		this.status = status;
		this.reason = reason;
	}

	public Approval(ApprovalStatus status, LocalDateTime deadline, String reason) {
		this.status = status;
		this.deadline = deadline;
		this.reason = reason;
	}

	public LocalDateTime getDeadline() {
		return deadline;
	}

	public void setDeadline(LocalDateTime deadline) {
		this.deadline = deadline;
	}

	public ApprovalStatus getStatus() {
		return status;
	}

	public void setStatus(ApprovalStatus status) {
		this.status = status;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	@Override
	public String toString() {
		return "Approval [status=" + status + ", deadline=" + deadline + ", reason=" + reason + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(deadline, reason, status);
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
		return Objects.equals(deadline, other.deadline) && Objects.equals(reason, other.reason)
				&& status == other.status;
	}

}
