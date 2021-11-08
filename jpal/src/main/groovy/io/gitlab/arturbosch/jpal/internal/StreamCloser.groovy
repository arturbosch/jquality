package io.gitlab.arturbosch.jpal.internal

import groovy.transform.CompileStatic

import java.util.stream.Stream

/**
 * @author artur
 */
@CompileStatic
final class StreamCloser {

	private StreamCloser() {}

	static <T> void quietly(Stream<T> stream) {
		try {
			stream.close()
		} catch (Exception ignored) {
		}
	}
}
