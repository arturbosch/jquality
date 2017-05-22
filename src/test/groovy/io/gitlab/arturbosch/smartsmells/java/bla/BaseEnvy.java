package io.gitlab.arturbosch.smartsmells.java.bla;

/**
 * @author Artur Bosch
 */
@SuppressWarnings("ALL")
public abstract class BaseEnvy implements InterfaceEnvy {
	protected int anInt;
	protected int anInt2;
	protected String anString;
	protected boolean aBoolean;
	protected float aFloat;

	public int getAnInt() {
		return anInt;
	}

	public int getAnInt2() {
		return anInt2;
	}

	public String getAnString() {
		return anString;
	}

	public boolean isaBoolean() {
		return aBoolean;
	}

	public float getaFloat() {
		return aFloat;
	}

	public void copy(BaseEnvy other) {
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
}
