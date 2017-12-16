package io.gitlab.arturbosch.smartsmells.java;

/**
 * @author Artur Bosch
 */
@SuppressWarnings("ALL")
public class ComplexConditionDummy {

	public void method() {
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
