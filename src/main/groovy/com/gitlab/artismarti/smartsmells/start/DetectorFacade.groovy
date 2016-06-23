package com.gitlab.artismarti.smartsmells.start

import com.gitlab.artismarti.smartsmells.common.*
import com.gitlab.artismarti.smartsmells.config.DetectorConfig
import com.gitlab.artismarti.smartsmells.config.DetectorInitializer
import com.gitlab.artismarti.smartsmells.metrics.ClassInfoDetector
import com.gitlab.artismarti.smartsmells.smells.comment.CommentDetector
import com.gitlab.artismarti.smartsmells.smells.complexmethod.ComplexMethodDetector
import com.gitlab.artismarti.smartsmells.smells.dataclass.DataClassDetector
import com.gitlab.artismarti.smartsmells.smells.deadcode.DeadCodeDetector
import com.gitlab.artismarti.smartsmells.smells.featureenvy.FeatureEnvyDetector
import com.gitlab.artismarti.smartsmells.smells.godclass.GodClassDetector
import com.gitlab.artismarti.smartsmells.smells.largeclass.LargeClassDetector
import com.gitlab.artismarti.smartsmells.smells.longmethod.LongMethodDetector
import com.gitlab.artismarti.smartsmells.smells.longparam.LongParameterListDetector
import com.gitlab.artismarti.smartsmells.smells.messagechain.MessageChainDetector
import com.gitlab.artismarti.smartsmells.smells.middleman.MiddleManDetector
import com.gitlab.artismarti.smartsmells.util.StreamCloser

import java.nio.file.Files
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

	static def fullStackFacade() {
		return new DetectorFacadeBuilder().fullStackFacade()
	}

	static def metricFacade() {
		return new DetectorFacadeBuilder().with(new ClassInfoDetector()).build()
	}

	static def builder() {
		return new DetectorFacadeBuilder()
	}

	static def fromConfig(final DetectorConfig config) {
		return new DetectorFacade(DetectorInitializer.init(config));
	}

	def run(Path startPath) {

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
		new SmellResult(detectors.collectEntries { [it.type, it.smells] })

//		def walker = Files.walk(startPath)
//		walker.filter { it.fileName.toString().endsWith("java") }
//				.forEach { internal(detectors, it) }
//		StreamCloser.quietly(walker)
//
//		new SmellResult(detectors.collectEntries { [it.type, it.smells] })
	}

	def runMetrics(Path startPath) {

		def forkJoinPool = new ForkJoinPool(
				Runtime.getRuntime().availableProcessors(),
				ForkJoinPool.defaultForkJoinWorkerThreadFactory, null, true)

		List<CompletableFuture> futures = new ArrayList<>()
		CompilationTree.registerRoot(startPath)

		def walker = Files.walk(startPath)
		walker.filter { it.fileName.toString().endsWith(".java") }
				.forEach { path ->
			futures.add(CompletableFuture
					.supplyAsync({ detectors[0].execute(null) }, forkJoinPool)
					.exceptionally { handle(it) })
		}

		CompletableFuture.allOf(futures.toArray(new CompletableFuture<?>[0])).join()
		forkJoinPool.shutdown()
		StreamCloser.quietly(walker)
		new SmellResult(detectors.collectEntries { [it.type, it.smells] })
	}

	def numberOfDetectors() {
		return detectors.size()
	}

	private static def internal(List<Detector> detectors, CompilationInfo info) {
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
		new ArrayList<>()
	}

	private static class DetectorFacadeBuilder {

		private List<Detector> detectors = new LinkedList<>()

		def with(Detector detector) {
			detectors.add(detector)
			return this
		}

		def fullStackFacade() {
			detectors = [new ComplexMethodDetector(), new CommentDetector(), new LongMethodDetector(),
			             new LongParameterListDetector(), new DeadCodeDetector(), new LargeClassDetector(),
			             new MessageChainDetector(), new MiddleManDetector(), new FeatureEnvyDetector(),
			             /*new CycleDetector(),*/ new DataClassDetector(), new GodClassDetector()]
			build()
		}

		def build() {
			return new DetectorFacade(detectors)
		}
	}


}
