package io.gitlab.arturbosch.smartsmells.java;

/**
 * @author artur
 */
@SuppressWarnings("ALL")
public class MiddleManDummy {

	private ManInTheMiddle manInTheMiddle = new ManInTheMiddle();

	public String fakeLogic(String s) {
		return manInTheMiddle.middleLogic(s);
	}

	private class ManInTheMiddle {
		EndMan endMan = new EndMan();

		String middleLogic(String s) {
			return endMan.realLogic(s);
		}

		String middleLogic2(String s) {
			return endMan.realLogic(s);
		}

		String middleLogic3(String s) {
			return endMan.realLogic(s);
		}
	}

	/**
	 * Interfaces with default methods look like Middle Man's but are only specifications
	 * with some sugar for simple cases or as substitute for traits as example given here.
	 * We will not treat them as MM because interfaces only do not justify the definition of MM.
	 */
	private interface InterfaceInTheMiddle {
		EndMan endMan();

		default String middleLogic(String s) {
			return endMan().realLogic(s);
		}

		default String middleLogic2(String s) {
			return endMan().realLogic(s);
		}

		default String middleLogic3(String s) {
			return endMan().realLogic(s);
		}
	}

	private class EndMan {
		String realLogic(String s) {
			return s;
		}
	}
}
