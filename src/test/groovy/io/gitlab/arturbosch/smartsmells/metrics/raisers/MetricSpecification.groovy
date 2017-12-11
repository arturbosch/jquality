package io.gitlab.arturbosch.smartsmells.metrics.raisers

import io.gitlab.arturbosch.jpal.core.CompilationInfo
import io.gitlab.arturbosch.jpal.core.CompilationStorage
import io.gitlab.arturbosch.jpal.core.JPAL
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.metrics.FileInfoAppender
import spock.lang.Specification

import java.nio.file.Paths

/**
 * @author Artur Bosch
 */
abstract class MetricSpecification extends Specification {

	Resolver resolver
	CompilationStorage storage

	List<CompilationInfo> setupCode(String code) {
		storage = JPAL.updatable(new FileInfoAppender())
		def map = new HashMap<>()
		map.put(Paths.get("foo/bar.java"), code)
		resolver = new Resolver(storage)
		return storage.updateCompilationInfo(map)
	}
}
