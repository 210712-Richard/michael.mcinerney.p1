package com.revature.beans;

import java.util.Objects;
import java.util.UUID;

public class Department {
	/**
	 * The name of the department
	 */
	private DepartmentName name;
	/**
	 * The User ID of the department head
	 */
	private UUID deptHeadId;

	public Department() {
		super();
	}

	public Department(DepartmentName name, UUID deptHeadId) {
		super();
		this.name = name;
		this.deptHeadId = deptHeadId;
	}

	public DepartmentName getName() {
		return name;
	}

	public void setName(DepartmentName name) {
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
