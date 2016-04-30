package com.gitlab.artismarti.smartsmells.java;

/**
 * @author artur
 */
@SuppressWarnings("ALL")
class CycleDummy {
	MeCycle meCycle = new MeCycle();

	private class MeCycle {
		CycleDummy cycleDummy = new CycleDummy();
	}
}
