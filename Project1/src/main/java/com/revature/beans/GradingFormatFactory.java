package com.revature.beans;

public class GradingFormatFactory {
	
	/**
	 * Gets the default GradingFormat
	 * @return A LetterGradeFormat
	 */
	public GradingFormat getGradingFormat() {
		return new LetterGradeFormat();
	}
	
	/**
	 * Get the GradingFormat based on the string passed in.
	 * @param type The name of the GradingFormat. Can be:
	 * PASSFAIL - new PassFailGradeFormat
	 * PERCENT - new PercentGradeFormat
	 * PRESENTATION	- new PresentationGradeFormat
	 * All other inputs will return LetterGradeFormat
	 * @return The GradingFormat the user typed in
	 */
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
	
	/**
	 * Get the GradingFormat based on the string passed in and also sets the passing grade.
	 * @param type The name of the GradingFormat. Can be:
	 * PASSFAIL - new PassFailGradeFormat
	 * PERCENT - new PercentGradeFormat
	 * PRESENTATION	- new PresentationGradeFormat
	 * All other inputs will return LetterGradeFormat
	 * @param passingGrade The minimum grade required to pass the class
	 * @return The GradingFormat the user typed in with the passing grade
	 */
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
	
	/**
	 * Get the GradingFormat based on the string passed in.
	 * @param gradeFormat The GradeFormat 
	 * @return The Identifying String of the gradingFormat
	 */
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
