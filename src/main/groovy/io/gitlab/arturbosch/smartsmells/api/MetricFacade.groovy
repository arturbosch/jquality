package io.gitlab.arturbosch.smartsmells.api

import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.core.JPAL
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.metrics.ClassInfo
import io.gitlab.arturbosch.smartsmells.metrics.FileMetricProcessor

import java.nio.file.Path
import java.util.regex.Pattern
import java.util.stream.Collectors

/**
 * @author Artur Bosch
 */
@CompileStatic
class MetricFacade {

	private List<Pattern> filters

	MetricFacade(List<String> filters = Collections.emptyList()) {
		this.filters = filters?.collect { Pattern.compile(it) }
	}

	List<ClassInfo> run(Path root) {
		def storage = root ? JPAL.initializedUpdatable(root, null, filters) : JPAL.updatable(null, filters)
		def resolver = new Resolver(storage)
		def processor = new FileMetricProcessor(resolver)
		def classInfos = storage.allCompilationInfo
				.parallelStream()
				.map { processor.process(it) }
				.flatMap { it.classes.stream() }
				.collect(Collectors.toList())
		return classInfos as List<ClassInfo>
	}
}
