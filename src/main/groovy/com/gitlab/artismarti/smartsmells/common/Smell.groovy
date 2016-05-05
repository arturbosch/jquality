package com.gitlab.artismarti.smartsmells.common

import com.gitlab.artismarti.smartsmells.comment.CommentDetector
import com.gitlab.artismarti.smartsmells.complexmethod.ComplexMethodDetector
import com.gitlab.artismarti.smartsmells.cycle.CycleDetector
import com.gitlab.artismarti.smartsmells.longmethod.LongMethodDetector
/**
 * @author artur
 */
enum Smell {
	COMMENT, COMPLEX_METHOD, CYCLE, DATA_CLASS, DEAD_CODE, FEATURE_ENVY, GOD_CLASS, LARGE_CLASS, LONG_METHOD,
	LONG_PARAM, MESSAGE_CHAIN, MIDDLE_MAN, UNKNOWN

	String name

	static Smell of(String name) {
		println name
		switch (name) {
			case CommentDetector.simpleName: COMMENT
				break
			case ComplexMethodDetector.simpleName: COMMENT
				break
			case CycleDetector.simpleName: COMMENT
				break
			case LongMethodDetector.simpleName: COMMENT
				break
			default: UNKNOWN
		}
	}
}
