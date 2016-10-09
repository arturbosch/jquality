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

	@Override
	public String toString() {
		return "DataClassDummy{" +
				"test='" + test + '\'' +
				", i=" + i +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		DataClassDummy that = (DataClassDummy) o;

		if (i != that.i) return false;
		if (test != null ? !test.equals(that.test) : that.test != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = test != null ? test.hashCode() : 0;
		result = 31 * result + i;
		return result;
	}
}
