package io.gitlab.arturbosch.smartsmells.api

import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import groovy.util.logging.Log
import io.gitlab.arturbosch.jpal.core.CompilationInfo
import io.gitlab.arturbosch.jpal.core.CompilationInfoProcessor
import io.gitlab.arturbosch.jpal.core.CompilationStorage
import io.gitlab.arturbosch.jpal.core.JPAL
import io.gitlab.arturbosch.jpal.internal.PrefixedThreadFactory
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.common.Detector
import io.gitlab.arturbosch.smartsmells.config.DetectorConfig
import io.gitlab.arturbosch.smartsmells.config.DetectorInitializer
import io.gitlab.arturbosch.smartsmells.metrics.ClassInfoDetector
import io.gitlab.arturbosch.smartsmells.smells.DetectionResult
import io.gitlab.arturbosch.smartsmells.smells.comment.CommentDetector
import io.gitlab.arturbosch.smartsmells.smells.comment.JavadocDetector
import io.gitlab.arturbosch.smartsmells.smells.complexmethod.ComplexMethodDetector
import io.gitlab.arturbosch.smartsmells.smells.cycle.CycleDetector
import io.gitlab.arturbosch.smartsmells.smells.dataclass.DataClassDetector
import io.gitlab.arturbosch.smartsmells.smells.deadcode.DeadCodeDetector
import io.gitlab.arturbosch.smartsmells.smells.featureenvy.FeatureEnvyDetector
import io.gitlab.arturbosch.smartsmells.smells.godclass.GodClassDetector
import io.gitlab.arturbosch.smartsmells.smells.largeclass.LargeClassDetector
import io.gitlab.arturbosch.smartsmells.smells.longmethod.LongMethodDetector
import io.gitlab.arturbosch.smartsmells.smells.longparam.LongParameterListDetector
import io.gitlab.arturbosch.smartsmells.smells.messagechain.MessageChainDetector
import io.gitlab.arturbosch.smartsmells.smells.middleman.MiddleManDetector
import io.gitlab.arturbosch.smartsmells.smells.nestedblockdepth.NestedBlockDepthDetector
import io.gitlab.arturbosch.smartsmells.smells.shotgunsurgery.ShotgunSurgeryDetector
import io.gitlab.arturbosch.smartsmells.smells.statechecking.StateCheckingDetector
import io.gitlab.arturbosch.smartsmells.util.Validate

import java.nio.file.Path
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.logging.Level
import java.util.regex.Pattern

/**
 * @author artur
 */
@CompileStatic
@Log
class DetectorFacade {

	private List<Detector<DetectionResult>> detectors
	private List<Pattern> filters

	private ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.runtime.availableProcessors(),
			new PrefixedThreadFactory("SmartSmells"))

	@PackageScope
	DetectorFacade(List<Detector> detectors, List<String> filters = Collections.emptyList()) {
		this.filters = filters.collect { Pattern.compile(it) }
		this.detectors = detectors
		Runtime.runtime.addShutdownHook { threadPool.shutdown() }
	}

	static DetectorFacadeBuilder builder() {
		return new DetectorFacadeBuilder()
	}

	static DetectorFacade fullStackFacade() {
		return new DetectorFacadeBuilder().fullStackFacade()
	}

	static DetectorFacade fromConfig(final DetectorConfig config) {
		Validate.notNull(config, "Configuration must not be null!")
		return new DetectorFacade(DetectorInitializer.init(config))
	}

	SmellResult run(Path startPath) {
		return internalRun(startPath) { JPAL.new(startPath) }
	}

	def <T> SmellResult runWithProcessor(Path startPath, CompilationInfoProcessor<T> processor) {
		return internalRun(startPath) { JPAL.new(startPath, processor) }
	}

	private SmellResult internalRun(Path startPath, Closure<CompilationStorage> create) {
		Validate.notNull(startPath)

		def storage = create()
		def resolver = new Resolver(storage)
		def infos = storage.getAllCompilationInfo()

		return justRun(infos, resolver)
	}

	SmellResult justRun(List<CompilationInfo> infos, Resolver resolver) {
		if (infos.empty) return new SmellResult(Collections.emptyMap())

		List<CompletableFuture> futures = new ArrayList<>(infos.size())

		infos.stream()
				.filter { CompilationInfo info -> !filters.any { it.asPredicate().test(info.path.toString()) } }
				.forEach { CompilationInfo info ->
			futures.add(CompletableFuture.runAsync({
				for (Detector detector : detectors) {
					detector.execute(info, resolver)
				}
			}, threadPool).exceptionally { handle(it) })
		}

		CompletableFuture.allOf(futures.toArray(new CompletableFuture<?>[0])).join()
		def entries = detectors.collectEntries { [it.type, it.smells] }
		return new SmellResult(entries)
	}

	private static ArrayList handle(Throwable throwable) {
		log.log(Level.WARNING, throwable) { throwable.message }
		return new ArrayList<>()
	}

	int numberOfDetectors() {
		return detectors.size()
	}

	void reset() {
		detectors.each { it.clear() }
	}

	private static class DetectorFacadeBuilder {

		private List<Detector> detectors = new LinkedList<>()
		private List<String> filters = new ArrayList<String>()

		DetectorFacadeBuilder with(Detector detector) {
			Validate.notNull(detector)
			detectors.add(detector)
			return this
		}

		DetectorFacade fullStackFacade() {
			detectors = [new CommentDetector(), new JavadocDetector(), new DeadCodeDetector(),
						 new LongMethodDetector(), new LongParameterListDetector(), new ComplexMethodDetector(),
						 new LargeClassDetector(), new DataClassDetector(),
						 new CycleDetector(), new FeatureEnvyDetector(), new MiddleManDetector(),
						 new ShotgunSurgeryDetector(), new MessageChainDetector(), new GodClassDetector(),
						 new StateCheckingDetector(), new NestedBlockDepthDetector()]
			build()
		}

		DetectorFacadeBuilder withFilters(String filterString) {
			Validate.notNull(filterString)
			filters = filterString.split(",").toList()
			return this
		}

		static DetectorFacade metricFacade() {
			return new DetectorFacadeBuilder().with(new ClassInfoDetector()).build()
		}

		DetectorFacadeBuilder fromConfig(final DetectorConfig config) {
			Validate.notNull(config, "Configuration must not be null!")
			detectors = DetectorInitializer.init(config)
			return this
		}

		DetectorFacade build() {
			return new DetectorFacade(detectors, filters)
		}
	}

}
