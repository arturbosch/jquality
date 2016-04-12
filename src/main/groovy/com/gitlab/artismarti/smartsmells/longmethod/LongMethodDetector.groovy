package com.gitlab.artismarti.smartsmells.longmethod

import com.gitlab.artismarti.smartsmells.common.Detector
import com.gitlab.artismarti.smartsmells.common.Visitor

import java.nio.file.Path

/**
 * @author artur
 */
class LongMethodDetector extends Detector<LongMethod> {

	@Override
	protected Visitor getVisitor(Path path) {
		new LongMethodVisitor(20)
	}

}
