package io.gitlab.arturbosch.smartsmells.java;

/**
 * @author artur
 */
@SuppressWarnings("ALL") public class DataClassDummy {

	String test;
	int i;

	public DataClassDummy() {
	}

	public DataClassDummy(String test, int i) {
		this.test = test;
		this.i = i;
	}

	public String getTest() {
		return test;
	}

	public void setTest(String test) {
		this.test = test;
	}

	public int getI() {
		return i;
	}

	public void setI(int i) {
		this.i = i;
	}

}
