package io.gitlab.arturbosch.smartsmells.api

import io.gitlab.arturbosch.jpal.core.JPAL
import io.gitlab.arturbosch.smartsmells.metrics.ClassInfo
import io.gitlab.arturbosch.smartsmells.metrics.FileInfo
import io.gitlab.arturbosch.smartsmells.metrics.FileMetricProcessor
import io.gitlab.arturbosch.smartsmells.metrics.Metric
import io.gitlab.arturbosch.smartsmells.util.Validate

import java.nio.file.Path
import java.util.concurrent.ExecutorService
import java.util.concurrent.ForkJoinPool
import java.util.regex.Pattern

/**
 * @author Artur Bosch
 */
class MetricFacade {

	private final ExecutorService executorService

	MetricFacade(final ExecutorService executorService = ForkJoinPool.commonPool()) {
		this.executorService = Validate.notNull(executorService)
	}

	List<ClassInfo> run(Path root, final List<String> filters = Collections.emptyList()) {
		def pathFilters = Validate.notNull(filters)?.collect { Pattern.compile(it) }
		def processor = new FileMetricProcessor()
		def storage = root ? JPAL.initializedUpdatable(root, processor, pathFilters, null, executorService)
				: JPAL.updatable(processor, pathFilters, null, executorService)
		return storage.allCompilationInfo.stream()
				.flatMap { it.getData(FileInfo.KEY).classes.stream() }
				.collect()
	}

	static List<Metric> averageAndDeviation(List<ClassInfo> infos) {
		return infos.stream()
				.flatMap { it.metrics.stream() }
				.collect()
				.groupBy { (it as Metric).type }
				.collect {
			def meanMetric = averageMetric(it)
			def devMetric = deviationMetric(asDouble(meanMetric), it)
			[meanMetric, devMetric]
		}.flatten() as List<Metric>
	}

	private static Metric averageMetric(Map.Entry<String, List<Metric>> it) {
		Metric.of(it.key + "Mean", it.value.inject(0.0d) { result, metric ->
			double value = asDouble(metric)
			result + value
		} / it.value.size())
	}

	private static Metric deviationMetric(double mean, Map.Entry<String, List<Metric>> it) {
		def size = it.value.size()
		Metric.of(it.key + "Deviation",
				Math.sqrt((1 / size) * it.value.stream().mapToDouble {
					Math.pow(asDouble(it) - mean, 2)
				}.sum())
		)
	}

	private static double asDouble(Metric it) {
		return it.isDouble ? it.asDouble() : it.value
	}
}
