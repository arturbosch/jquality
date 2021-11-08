package io.gitlab.arturbosch.jpal.dummies;

import io.gitlab.arturbosch.jpal.Helper;
import io.gitlab.arturbosch.jpal.dummies.test.*;

/**
 * Don't touch this class, tests depend on exact token structure!
 *
 * @author artur
 */
@SuppressWarnings("ALL")
public class CycleDummy {

	public void compute() {
		Math.abs(100);
	}

	private void meah() {
		TestReference reference = new TestReference();
		Helper helper = new Helper();

		new InnerClassesDummy();
		new InnerClassesDummy.InnerClass();
		new InnerClassesDummy.InnerClass.InnerInnerClass();
	}

	public static class InnerCycleOne {
		InnerCycleTwo cycleTwo = new InnerCycleTwo();
	}

	public static class InnerCycleTwo {
		InnerCycleOne cycleOne = new InnerCycleOne();
	}
}
