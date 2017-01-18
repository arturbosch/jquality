package io.gitlab.arturbosch.smartsmells.java;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Artur Bosch
 */
@SuppressWarnings("ALL")
public class StateCheckingDummy {

	private static final int MATCH = 0;
	private static final int BIGGER = 1;
	private static final int SMALLER = -1;

	public boolean isGreaterOrEquals(int number) {
		switch (number) {
			case MATCH:
				return true;
			case BIGGER:
				return true;
			case SMALLER:
				return false;
			default:
				return false;
		}
	}

	// No state checking as it is a common case to check variables in two directions
	// eg. greaterThan and lesserThan
	public void doStuff(int number) {
		if (number > 0) {
			doHeavyStuff();
		} else if (number < 0) {
			doComplicatedStuff();
		} else {
			System.out.println(number);
		}
	}

	public int replaceWithPolymorphism(Staty staty) {
		if (staty instanceof BigStaty) {
			return BIGGER;
		} else if (staty instanceof SmallStaty) {
			return SMALLER;
		} else if (staty instanceof StuffStaty) {
			return MATCH;
		}
		throw new IllegalStateException();
	}

	public void doHeavyStuff() {
		int type = 0;
		int i = 5;
		InputStream in = System.in;
		Object[] sortValues = new Object[100];
		if (type == 0) {
			sortValues[i] = null;
		} else if (type == 1) {
			sortValues[i] = 5;
		} else if (type == 2) {
			sortValues[i] = Long.getLong("2");
		} else if (type == 3) {
			sortValues[i] = 3;
		} else if (type == 4) {
			sortValues[i] = 5;
		} else if (type == 5) {
			sortValues[i] = 5;
		} else if (type == 6) {
			sortValues[i] = 6;
		} else if (type == 7) {
			sortValues[i] = 7;
		} else if (type == 8) {
			sortValues[i] = 9;
		} else {
			try {
				throw new IOException("Can't match type [" + type + "]");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public void doComplicatedStuff() {
	}

	class Staty {
	}

	class BigStaty extends Staty {
	}

	class SmallStaty extends Staty {
	}

	class StuffStaty extends Staty {
	}
}
