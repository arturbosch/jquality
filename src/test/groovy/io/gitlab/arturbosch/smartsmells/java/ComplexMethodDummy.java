package io.gitlab.arturbosch.smartsmells.java;

/**
 * Default in switch is like else of if so no extra count.
 *
 * @author artur
 */
@SuppressWarnings("ALL") public class ComplexMethodDummy {

	/**
	 * Complexity of 10
	 */
	public void noComplexMethod(boolean b1, boolean b2, int number) {
		if (b1) {
			while (b2) {
				switch (number) {
					case 4:
						break;
					case 3:
						break;
					case 2:
						break;
					case 1:
						break;
					case 0:
						break;
					default:
						for (int i : new int[] {1, 2, 3, 4, 5}) {
							System.out.println(i);
						}
				}
			}
		}
	}

	/**
	 * Complexity of 11
	 */
	public void complexMethod(boolean b1, boolean b2, int number) {
		if (b1) {
			while (b2) {
				switch (number) {
					case 5:
						if(5 == 5) {
							System.out.println(5);
						} else {
							System.out.println(5);
						};
					case 4:
						break;
					case 3:
						break;
					case 2:
						break;
					case 1:
						break;
					case 0:
						break;
					default:
						for (int i : new int[] {1, 2, 3, 4, 5}) {
							System.out.println(i);
						}
				}
			}
		}
	}
}
