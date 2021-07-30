package com.revature.data;

import com.revature.beans.Department;
import com.revature.beans.DepartmentName;

public interface DepartmentDao {
	
	/**
	 * Get the department based on the department name
	 * @param deptName The name of the department
	 * @return The department with the same name
	 */
	Department getDepartment(DepartmentName deptName);
	
	/**
	 * Add a new department to the database
	 * @param dept The department being added to the database
	 */
	void createDepartment(Department dept);
}
