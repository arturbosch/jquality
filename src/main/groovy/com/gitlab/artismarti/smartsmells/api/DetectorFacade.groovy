package com.gitlab.artismarti.smartsmells.api

import com.gitlab.artismarti.smartsmells.common.CompilationInfo
import com.gitlab.artismarti.smartsmells.common.CompilationStorage
import com.gitlab.artismarti.smartsmells.common.CompilationTree
import com.gitlab.artismarti.smartsmells.common.Detector
import com.gitlab.artismarti.smartsmells.common.Smelly
import com.gitlab.artismarti.smartsmells.config.DetectorConfig
import com.gitlab.artismarti.smartsmells.config.DetectorInitializer
import com.gitlab.artismarti.smartsmells.metrics.ClassInfoDetector
import com.gitlab.artismarti.smartsmells.smells.comment.CommentDetector
import com.gitlab.artismarti.smartsmells.smells.complexmethod.ComplexMethodDetector
import com.gitlab.artismarti.smartsmells.smells.cycle.CycleDetector
import com.gitlab.artismarti.smartsmells.smells.dataclass.DataClassDetector
import com.gitlab.artismarti.smartsmells.smells.deadcode.DeadCodeDetector
import com.gitlab.artismarti.smartsmells.smells.featureenvy.FeatureEnvyDetector
import com.gitlab.artismarti.smartsmells.smells.godclass.GodClassDetector
import com.gitlab.artismarti.smartsmells.smells.largeclass.LargeClassDetector
import com.gitlab.artismarti.smartsmells.smells.longmethod.LongMethodDetector
import com.gitlab.artismarti.smartsmells.smells.longparam.LongParameterListDetector
import com.gitlab.artismarti.smartsmells.smells.messagechain.MessageChainDetector
import com.gitlab.artismarti.smartsmells.smells.middleman.MiddleManDetector

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
