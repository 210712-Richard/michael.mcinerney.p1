package com.revature.beans;

public class PresentationGradeFormat implements GradingFormat {

	@Override
	public String toString() {
		return "PresentationGradeFormat []";
	}

	@Override
	public String getPassingGrade() {
		return Boolean.toString(true);
	}

	@Override
	public Boolean isPassing(String grade) {
		return Boolean.parseBoolean(grade);
	}

}
