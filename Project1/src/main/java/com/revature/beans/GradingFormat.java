package com.revature.beans;

public interface GradingFormat{
	
	String getPassingGrade();
	
	Boolean isPassing(String grade);
	
	@Override
	boolean equals(Object obj);
	
	@Override
	String toString();
	
	@Override
	int hashCode();
}
