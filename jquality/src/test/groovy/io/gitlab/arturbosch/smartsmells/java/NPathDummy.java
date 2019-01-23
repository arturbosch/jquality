package io.gitlab.arturbosch.smartsmells.java;

/**
 * @author Artur Bosch
 */
@SuppressWarnings("ALL")
public class NPathDummy {

	public void m1(int a) {
		if (a == 5 || a == 0) {

		} else {

		}
	}

	public void m2(int a, int b) {
		if (a == 5) {

		} else {

		}

		if (a == 3) {

		} else if (b == 3) {
		}
	}

	public void m3() {
		if (5 > 4 && 4 < 6 || (3 < 5 || 2 < 5)) {
			System.out.println();
		}
		while (5 > 4 && 4 < 6 || (3 < 5 || 2 < 5)) {
			break;
		}
		do {
			System.out.println();
		} while (5 > 4 && 4 < 6 || (3 < 5 || 2 < 5));
	}
}
