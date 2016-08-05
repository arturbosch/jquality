package io.gitlab.arturbosch.smartsmells.api

import io.gitlab.arturbosch.smartsmells.common.*
import io.gitlab.arturbosch.smartsmells.config.DetectorConfig
import io.gitlab.arturbosch.smartsmells.config.DetectorInitializer
import io.gitlab.arturbosch.smartsmells.metrics.ClassInfoDetector
import io.gitlab.arturbosch.smartsmells.smells.comment.CommentDetector
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

import java.nio.file.Path
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ForkJoinPool

/**
 * @author artur
 */
class DetectorFacade {

	private List<Detector<Smelly>> detectors = new LinkedList<>()

	private DetectorFacade(List<Detector> detectors) {
		this.detectors = detectors
	}

	static DetectorFacade fullStackFacade() {
		return new DetectorFacadeBuilder().fullStackFacade()
	}

	static DetectorFacade metricFacade() {
		return new DetectorFacadeBuilder().with(new ClassInfoDetector()).build()
	}

	static DetectorFacadeBuilder builder() {
		return new DetectorFacadeBuilder()
	}

	static DetectorFacade fromConfig(final DetectorConfig config) {
		return new DetectorFacade(DetectorInitializer.init(config));
	}

	SmellResult run(Path startPath) {

		CompilationTree.registerRoot(startPath)
		def storage = CompilationStorage.create(startPath)
		def infos = storage.getAllCompilationInfo()

		def forkJoinPool = new ForkJoinPool(
				Runtime.getRuntime().availableProcessors(),
				ForkJoinPool.defaultForkJoinWorkerThreadFactory, null, true)

		List<CompletableFuture> futures = new ArrayList<>(infos.size())

		infos.forEach { info ->
			futures.add(CompletableFuture
					.supplyAsync({ internal(detectors, info) }, forkJoinPool)
					.exceptionally { handle(it) })
		}

		CompletableFuture.allOf(futures.toArray(new CompletableFuture<?>[0])).join()
		forkJoinPool.shutdown()
		return new SmellResult(detectors.collectEntries { [it.type, it.smells] })

	}

	int numberOfDetectors() {
		return detectors.size()
	}

	private static void internal(List<Detector> detectors, CompilationInfo info) {
		List<CompletableFuture> futures = new ArrayList<>()
		detectors.each { detector ->
			futures.add(CompletableFuture
					.supplyAsync { detector.execute(info.unit, info.path) }
					.exceptionally { handle(it) })
		}
		CompletableFuture.allOf(futures.toArray(new CompletableFuture<?>[0])).join()
	}

	private static ArrayList handle(Throwable throwable) {
		println throwable.printStackTrace()
		return new ArrayList<>()
	}

	private static class DetectorFacadeBuilder {

		private List<Detector> detectors = new LinkedList<>()

		DetectorFacadeBuilder with(Detector detector) {
			detectors.add(detector)
			return this
		}

		DetectorFacade fullStackFacade() {
			detectors = [new ComplexMethodDetector(), new CommentDetector(), new LongMethodDetector(),
						 new LongParameterListDetector(), new DeadCodeDetector(), new LargeClassDetector(),
						 new MessageChainDetector(), new MiddleManDetector(), new FeatureEnvyDetector(),
						 new CycleDetector(), new DataClassDetector(), new GodClassDetector()]
			build()
		}

		DetectorFacade build() {
			return new DetectorFacade(detectors)
		}
	}


}
