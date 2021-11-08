package io.gitlab.arturbosch.jpal.dummies.resolving;

/**
 * @author Artur Bosch
 */
@SuppressWarnings("ALL")
public class SolveTypeDummy {
	public String s;
	public String method(int x) { return s; };
	public static int instanceField;
	public static SolveTypeDummy instance() {
		return new SolveTypeDummy();
	}
}
