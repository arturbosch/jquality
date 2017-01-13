package io.gitlab.arturbosch.smartsmells.java.cycles;

/**
 * @author artur
 */
@SuppressWarnings("ALL")
class OtherCycle {
	CycleDummy cycleDummy = new CycleDummy();

	public void compute() {
		cycleDummy.meCycle.hashCode();
	}

}
