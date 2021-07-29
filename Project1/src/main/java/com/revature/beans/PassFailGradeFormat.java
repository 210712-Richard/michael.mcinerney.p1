package com.revature.beans;

import java.util.Objects;

public class PassFailGradeFormat implements GradingFormat {
	
	private String passingGrade;
	
	public PassFailGradeFormat() {
		passingGrade = "Pass";
	}
	@Override
	public String getPassingGrade() {
		return passingGrade;
	}

	@Override
	public String toString() {
		return "PassFailGradeFormat [passingGrade=" + passingGrade + "]";
	}
	@Override
	public int hashCode() {
		return Objects.hash(passingGrade);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PassFailGradeFormat other = (PassFailGradeFormat) obj;
		return Objects.equals(passingGrade, other.passingGrade);
	}
	@Override
	public Boolean isPassing(String grade) {
		return grade.equals(passingGrade);
	}

}
