package io.gitlab.arturbosch.smartsmells.java;

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
