package com.gitlab.artismarti.smartsmells.java;

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
	}

	private class EndMan {
		String realLogic(String s) {
			return s;
		}
	}
}
