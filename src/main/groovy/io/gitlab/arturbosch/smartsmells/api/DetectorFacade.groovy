package io.gitlab.arturbosch.smartsmells.api

import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import groovy.util.logging.Log
import io.gitlab.arturbosch.jpal.core.CompilationInfo
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

	// Builders

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

	// Internals

	@PackageScope
	final List<Pattern> filters

	private final DetectorConfig config

	private final List<Detector<DetectionResult>> detectors
	private final ExecutorService executorService

	@PackageScope
	DetectorFacade(List<Detector<DetectionResult>> detectors,
				   DetectorConfig config = null,
				   List<String> filters = Collections.emptyList(),
				   ExecutorService executorService = null) {
		this.config = config
		this.filters = filters.collect { Pattern.compile(it) }
		this.detectors = detectors
		this.executorService = executorService ?: Executors.newFixedThreadPool(
				Runtime.runtime.availableProcessors(),
				new PrefixedThreadFactory("SmartSmells")).with { ExecutorService service ->
			Runtime.runtime.addShutdownHook { service.shutdown() }
			service
		}
		detectors*.setConfig(config)
	}

	SmellResult run(Path startPath) {
		Validate.notNull(startPath)

		def storage = JPAL.newInstance(startPath, null, filters, null, executorService)
		def resolver = new Resolver(storage)
		def infos = storage.getAllCompilationInfo()

		return run(infos, resolver)
	}

	SmellResult run(Collection<CompilationInfo> infos, Resolver resolver) {
		if (infos.empty) return new SmellResult(Collections.emptyMap())

		List<CompletableFuture<Void>> futures = infos.collect { CompilationInfo info ->
			CompletableFuture.runAsync({
				for (Detector detector : detectors) {
					detector.execute(info, resolver)
				}
			}, executorService).exceptionally { handle(it) }
		}

		CompletableFuture.allOf(futures.toArray(new CompletableFuture<?>[0])).join()
		def entries = detectors.collectEntries { [it.type, it.smells] }
		return new SmellResult(entries)
	}

	private static List handle(Throwable throwable) {
		log.log(Level.WARNING, throwable) { throwable.message }
		return Collections.emptyList()
	}

	int numberOfDetectors() {
		return detectors.size()
	}

	/**
	 * Clears all code smell findings from the detectors.
	 */
	void reset() {
		detectors.each { it.clear() }
	}

}
