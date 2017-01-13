package io.gitlab.arturbosch.smartsmells.java.cycles;

/**
 * @author artur
 */
@SuppressWarnings("ALL")
class SingletonCycleDummy {
	static SingletonCycleDummy me = new SingletonCycleDummy();
	private SingletonCycleDummy() {}
}
