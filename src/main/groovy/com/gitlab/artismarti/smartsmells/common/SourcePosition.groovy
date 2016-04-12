package com.gitlab.artismarti.smartsmells.common

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

	int getColumn() {
		return (int) get(1);
	}

}
