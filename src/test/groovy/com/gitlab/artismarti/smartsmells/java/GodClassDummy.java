package com.gitlab.artismarti.smartsmells.java;

/**
 * Sum complexity of 22
 *
 * @author artur
 */
@SuppressWarnings("ALL") public class GodClassDummy {

	int i = 5;
	String s = "hello";
	ComplexMethodDummy dummy, dummy1, dummy2, dummy3, dummy4 = new ComplexMethodDummy();

	public String calc() {
		dummy.complexMethod(true, true, i);
		dummy2.noComplexMethod(true, true, i);
		return s + i;
	}

	public int getI() {
		return i;
	}

	public String getS() {
		return s;
	}

	/**
	 * Complexity of 10
	 */
	public void complexMethod1(boolean b1, boolean b2, int number) {
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
							dummy1.complexMethod(true, true, i);
							dummy3.complexMethod(true, true, i);
						}
				}
			}
		} else {
			// to test else block
			if (true) {
			}
		}
	}

	/**
	 * Complexity of 9
	 */
	public void complexMethod2(boolean b1, boolean b2, int number) {
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
							dummy4.complexMethod(true, true, i);
							dummy4.complexMethod(true, true, i);
						}
				}
			}
		}
	}
}
