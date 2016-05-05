package com.gitlab.artismarti.smartsmells.java;

/**
 * @author artur
 */
@SuppressWarnings("ALL")
class CycleDummy {
	OtherCycle meCycle = new OtherCycle();

	public void compute() {
		meCycle.cycleDummy.meah();
	}

	private void meah() {
	}

	class OtherCycle {
		CycleDummy cycleDummy = new CycleDummy();

		public void compute() {
			cycleDummy.meCycle.meah();
		}

		private void meah() {
		}
	}
}
