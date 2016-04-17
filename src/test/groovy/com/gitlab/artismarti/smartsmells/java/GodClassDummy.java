package com.gitlab.artismarti.smartsmells.java;

/**
 * Sum complexity of 21
 *
 * @author artur
 */
@SuppressWarnings("ALL") public class GodClassDummy {

	int i = 5;
	String s = "hello";
	ComplexMethodDummy dummy = new ComplexMethodDummy();

	public String calc() {
		dummy.complexMethod(true, true, i);
		dummy.noComplexMethod(true, true, i);
		return s + i;
	}

	public int getI() {
		return i;
	}

	public String getS() {
		return s;
	}

	/**
	 * Complexity of 9
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
							System.out.println(i);
						}
				}
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
							System.out.println(i);
						}
				}
			}
		}
	}
}
