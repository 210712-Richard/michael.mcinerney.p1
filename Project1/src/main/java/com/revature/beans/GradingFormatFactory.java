package com.revature.beans;

public class GradingFormatFactory {
	
	public GradingFormat getGradingFormat() {
		return new LetterGradeFormat();
	}
	
	public GradingFormat getGradingFormat(String type) {
		if (type == null) {
			return new LetterGradeFormat();
		}
		type = type.toUpperCase().strip();
		switch(type) {
		case "PASSFAIL": return new PassFailGradeFormat();
		case "PERCENT": return new PercentGradeFormat();
		case "PRESENTATION": return new PresentationGradeFormat();
		default: return new LetterGradeFormat();

		}
	}
	
	public GradingFormat getGradingFormat(String type, String passingGrade) {
		if (type == null || passingGrade == null) {
			return new LetterGradeFormat();
		}
		type = type.toUpperCase().strip();
		switch(type) {
		case "PASSFAIL": return new PassFailGradeFormat();
		case "PERCENT": return new PercentGradeFormat(passingGrade);
		case "PRESENTATION": return new PresentationGradeFormat();
		default: return new LetterGradeFormat(passingGrade);

		}
	}
	
	public String getIdentifier(GradingFormat gradeFormat) {
		if (gradeFormat == null) {
			return null;
		}
		
		switch(gradeFormat.getClass().getSimpleName()) {
		case "PassFailGradeFormat": return "PASSFAIL";
		case "PercentGradeFormat": return "PERCENT";
		case "PresentationGradeFormat": return "PRESENTATION";
		default: return "LETTER";
		}
	}
}
