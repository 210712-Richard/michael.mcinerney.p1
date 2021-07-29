package com.revature.beans;

import java.util.Objects;

public class LetterGradeFormat implements GradingFormat {
	
	private LetterGrade passingGrade;
	
	public LetterGradeFormat() {
		super();
		passingGrade = LetterGrade.getDefaultPassingGrade();
	}
	
	public LetterGradeFormat(String passingGrade) {
		super();
		LetterGrade pass = null;
		try {
			pass = LetterGrade.valueOf(passingGrade);
		} catch (Exception e) {
			pass = LetterGrade.getDefaultPassingGrade();
		}
		this.passingGrade = pass;
		
	}
	
	@Override
	public String getPassingGrade() {
		return passingGrade.toString();
	}

	@Override
	public Boolean isPassing(String grade) {
		LetterGrade actualGrade = null;
		try {
			actualGrade = LetterGrade.valueOf(grade);
		} catch (Exception e) {
			return false;
		}
		
		return (actualGrade.getValue() >= passingGrade.getValue()) ? true : false;
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
		LetterGradeFormat other = (LetterGradeFormat) obj;
		return passingGrade == other.passingGrade;
	}

	@Override
	public String toString() {
		return "LetterGradeFormat [passingGrade=" + passingGrade + "]";
	}

}

enum LetterGrade{
	A(5), B(4), C(3), D(2), F(1);
	
	private Integer value;
	
	LetterGrade(Integer value) {
		this.value = value;
	}
	
	public static LetterGrade getDefaultPassingGrade() {
		return C;
	}
	
	public Integer getValue() {
		return value;
	}
}