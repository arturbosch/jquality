package com.gitlab.artismarti.smartsmells.common.source

/**
 * A source position is a tuple of line to column and represent a position inside a file.
 *
 * @author artur
 */
class SourcePosition extends Tuple2<Integer, Integer> {

	SourcePosition(Integer first, Integer second) {
		super(first, second)
	}

	int getLine() {
		return (int) get(0);
	}

	static SourcePosition of(int line, int column) {
		new SourcePosition(line, column)
	}
}
