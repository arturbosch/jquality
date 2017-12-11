package io.gitlab.arturbosch.smartsmells.java.bla;

/**
 * @author Artur Bosch
 */
@SuppressWarnings("ALL")
public class SelfEnvy extends BaseEnvy {

	public void copy(InterfaceEnvy other) {
		anInt = other.getAnInt();
		anInt = other.getAnInt2();
		anString = other.getAnString();
		aBoolean = other.isaBoolean();
		aFloat = other.getaFloat();
		aFloat = other.getaFloat();
		aFloat = other.getaFloat();
		aFloat = other.getaFloat();
		aFloat = other.getaFloat();
		aFloat = other.getaFloat();
	}

	public void addedService() {
	}
}

