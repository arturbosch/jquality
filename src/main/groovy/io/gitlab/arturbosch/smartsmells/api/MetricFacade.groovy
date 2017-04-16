package io.gitlab.arturbosch.smartsmells.api

import io.gitlab.arturbosch.jpal.core.JPAL
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.config.DetectorConfig
import io.gitlab.arturbosch.smartsmells.metrics.ClassInfo
import io.gitlab.arturbosch.smartsmells.metrics.FileMetricProcessor
import io.gitlab.arturbosch.smartsmells.metrics.Metric

import java.nio.file.Path
import java.util.regex.Pattern
import java.util.stream.Collectors

/**
 * @author Artur Bosch
 */
class MetricFacade {

	private List<Pattern> filters
	private DetectorConfig config

	MetricFacade(DetectorConfig config = null, List<String> filters = Collections.emptyList()) {
		this.config = config // TODO configre ClassInfoVisitor and pass it to FileMetricProcessor
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

	static List<Metric> average(List<ClassInfo> infos) {
		return infos.stream()
				.flatMap { it.metrics.stream() }
				.collect()
				.groupBy { (it as Metric).type }
				.collect { averageMetric(it) }
	}

	private static Metric averageMetric(Map.Entry<String, List<Metric>> it) {
		Metric.of(it.key, it.value.inject(0.0) { r, m ->
			Metric metric = m as Metric
			BigDecimal result = r as BigDecimal
			if (metric.isDouble) {
				result.add(BigDecimal.valueOf(metric.asDouble()))
			} else {
				result.add(BigDecimal.valueOf(metric.value))
			}
		} / it.value.size())
	}
}
