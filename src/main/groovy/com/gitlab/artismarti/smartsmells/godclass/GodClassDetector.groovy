package com.gitlab.artismarti.smartsmells.godclass

import com.gitlab.artismarti.smartsmells.common.Detector
import com.gitlab.artismarti.smartsmells.common.Visitor

import java.nio.file.Path

/**
 * @author artur
 */
class GodClassDetector extends Detector<GodClass> {
	@Override
	protected Visitor getVisitor(Path path) {
		return new GodClassVisitor(path)
	}
}
