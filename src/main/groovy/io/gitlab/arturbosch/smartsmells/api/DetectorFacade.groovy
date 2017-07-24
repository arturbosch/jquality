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
import io.gitlab.arturbosch.smartsmells.config.DetectorConfig
import io.gitlab.arturbosch.smartsmells.config.DetectorInitializer
import io.gitlab.arturbosch.smartsmells.smells.DetectionResult
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

	final List<Pattern> filters

	private final DetectorConfig config

	private final List<Detector<DetectionResult>> detectors
	private ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.runtime.availableProcessors(),
			new PrefixedThreadFactory("SmartSmells"))

	@PackageScope
	DetectorFacade(List<Detector> detectors, DetectorConfig config = null, List<String> filters = Collections.emptyList()) {
		this.config = config
		this.filters = filters.collect { Pattern.compile(it) }
		this.detectors = detectors
		detectors*.setConfig(config)
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
		return new DetectorFacade(DetectorInitializer.init(config), config)
	}

	SmellResult run(Path startPath) {
		return internalRun(startPath) { JPAL.newInstance(startPath, null, filters) }
	}

	def <T> SmellResult runWithProcessor(Path startPath, CompilationInfoProcessor<T> processor) {
		return internalRun(startPath) { JPAL.newInstance(startPath, processor, filters) }
	}

	private SmellResult internalRun(Path startPath, Closure<CompilationStorage> create) {
		Validate.notNull(startPath)

		def storage = create()
		def resolver = new Resolver(storage)
		def infos = storage.getAllCompilationInfo()

		return justRun(infos, resolver)
	}

	SmellResult justRun(Collection<CompilationInfo> infos, Resolver resolver) {
		if (infos.empty) return new SmellResult(Collections.emptyMap())

		List<CompletableFuture> futures = infos.collect { CompilationInfo info ->
			CompletableFuture.runAsync({
				for (Detector detector : detectors) {
					detector.execute(info, resolver)
				}
			}, threadPool).exceptionally { handle(it) }
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

}
