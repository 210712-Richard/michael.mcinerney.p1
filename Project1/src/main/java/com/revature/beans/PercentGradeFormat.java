package com.revature.beans;

import java.util.Objects;

public class PercentGradeFormat implements GradingFormat {
	
	private final Float defaultPassingGrade = 80.0f;
	private Float passingGrade;
	
	public PercentGradeFormat() {
		this.passingGrade = this.defaultPassingGrade;
	}
	
	public PercentGradeFormat(String grade) {
		Float passGrade = 0f;
		try {
			passGrade = Float.parseFloat(grade);
		} catch (Exception e) {
			passGrade = this.defaultPassingGrade;
		}
		this.passingGrade = passGrade;
	}
	@Override
	public String getPassingGrade() {
		return passingGrade.toString();
	}
	@Override
	public Boolean isPassing(String grade) {
		Float actualGrade = 0f;
		try {
			actualGrade = Float.parseFloat(grade);
		} catch (Exception e) {
			return false;
		}
		
		return (actualGrade >= passingGrade) ? true : false;
	}

	@Override
	public String toString() {
		return "PercentGradeFormat [defaultPassingGrade=" + defaultPassingGrade + ", passingGrade=" + passingGrade
				+ "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(defaultPassingGrade, passingGrade);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PercentGradeFormat other = (PercentGradeFormat) obj;
		return Objects.equals(defaultPassingGrade, other.defaultPassingGrade)
				&& Objects.equals(passingGrade, other.passingGrade);
	}

}
