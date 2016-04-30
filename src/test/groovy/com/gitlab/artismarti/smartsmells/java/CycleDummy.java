package com.gitlab.artismarti.smartsmells.java;

/**
 * @author artur
 */
@SuppressWarnings("ALL")
class CycleDummy {
	MeCycle meCycle = new MeCycle();

	public void compute() {
		meCycle.cycleDummy.meah();
	}

	private void meah() {
	}

	private class MeCycle {
		CycleDummy cycleDummy = new CycleDummy();
	}
}
