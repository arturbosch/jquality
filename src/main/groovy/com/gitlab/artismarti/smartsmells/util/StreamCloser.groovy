package com.gitlab.artismarti.smartsmells.util

import java.util.stream.Stream

/**
 * @author artur
 */
final class StreamCloser {

	static <T> void quietly(Stream<T> stream) {
		try {
			stream.close()
		} catch (Exception ignored) {
		}
	}
}
