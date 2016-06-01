package com.gitlab.artismarti.smartsmells.util

/**
 * @author artur
 */
class Looper {
	private Closure code

	static Looper loop(Closure code) {
		new Looper(code: code)
	}

	void until(Closure test) {
		code()
		while (!test()) {
			code()
		}
	}
}
