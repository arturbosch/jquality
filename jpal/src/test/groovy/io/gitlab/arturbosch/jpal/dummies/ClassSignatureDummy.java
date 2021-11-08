package io.gitlab.arturbosch.jpal.dummies;

import java.util.List;

/**
 * @author Artur Bosch
 */
@SuppressWarnings("ALL")
public class ClassSignatureDummy {

	static class VeryComplexInnerClass<T extends String, B extends List<T>>
			extends ClassSignatureDummy implements Cloneable, Runnable {

		enum EnumSignatureDummy implements Runnable {
			;

			@Override
			public void run() {

			}
		}

		@Override
		public void run() {

		}
	}
}
