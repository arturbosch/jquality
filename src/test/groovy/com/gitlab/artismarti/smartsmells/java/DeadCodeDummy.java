package com.gitlab.artismarti.smartsmells.java;

/**
 * @author artur
 */
@SuppressWarnings("ALL") public class DeadCodeDummy {

	private int deadField = 5;
	private int usedField = 5;
	private String usedString = "Hello";

	private int deadMethod(int deadParameter) {
		int deadLocaleVariable = 5;
		usedMethod("" + usedField++);
		usedField = usedString.length();
		return 5;
	}

	public int usedMethod(String usedParameter) {
		usedField = usedParameter.length() + usedString.length();
		int usedLocaleVariable = 4;
		return Integer.valueOf(usedField).compareTo(usedLocaleVariable);
	}


}
