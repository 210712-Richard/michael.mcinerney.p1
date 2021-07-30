package com.revature.beans;

import java.util.Objects;
import java.util.UUID;

public class Department {
	/**
	 * The name of the department
	 */
	private String name;
	/**
	 * The User ID of the department head
	 */
	private UUID deptHeadId;

	public Department() {
		super();
	}

	public Department(String name, UUID deptHeadId) {
		super();
		this.name = name;
		this.deptHeadId = deptHeadId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public UUID getDeptHeadId() {
		return deptHeadId;
	}

	public void setDeptHeadId(UUID deptHeadId) {
		this.deptHeadId = deptHeadId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(deptHeadId, name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Department other = (Department) obj;
		return Objects.equals(deptHeadId, other.deptHeadId) && name == other.name;
	}

	@Override
	public String toString() {
		return "Department [name=" + name + ", departmentHeadId=" + deptHeadId + "]";
	}

}
