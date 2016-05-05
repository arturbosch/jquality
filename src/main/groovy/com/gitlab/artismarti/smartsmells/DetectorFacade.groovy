package com.gitlab.artismarti.smartsmells

import com.gitlab.artismarti.smartsmells.comment.CommentDetector
import com.gitlab.artismarti.smartsmells.common.CompilationTree
import com.gitlab.artismarti.smartsmells.common.Detector
import com.gitlab.artismarti.smartsmells.common.Smelly
import com.gitlab.artismarti.smartsmells.complexmethod.ComplexMethodDetector
import com.gitlab.artismarti.smartsmells.cycle.CycleDetector
import com.gitlab.artismarti.smartsmells.dataclass.DataClassDetector
import com.gitlab.artismarti.smartsmells.deadcode.DeadCodeDetector
import com.gitlab.artismarti.smartsmells.featureenvy.FeatureEnvyDetector
import com.gitlab.artismarti.smartsmells.godclass.GodClassDetector
import com.gitlab.artismarti.smartsmells.largeclass.LargeClassDetector
import com.gitlab.artismarti.smartsmells.longmethod.LongMethodDetector
import com.gitlab.artismarti.smartsmells.longparam.LongParameterListDetector
import com.gitlab.artismarti.smartsmells.messagechain.MessageChainDetector
import com.gitlab.artismarti.smartsmells.middleman.MiddleManDetector

import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.CompletableFuture

/**
 * @author artur
 */
class DetectorFacade {

	private List<Detector<Smelly>> detectors = new LinkedList<>()

	private DetectorFacade(List<Detector> detectors) {
		this.detectors = detectors
	}

	static def builder() {
		return new DetectorFacadeBuilder()
	}

	def run(Path startPath) {

		CompilationTree.registerRoot(startPath)

		Files.walk(startPath)
				.filter { it.fileName.toString().endsWith("java") }
				.forEach { internal(detectors, it) }

		new SmellResult(detectors.collectEntries { [it.type, it.smells] })
	}

	def numberOfDetectors() {
		return detectors.size()
	}

	private static def internal(List<Detector> detectors, Path path) {
		List<CompletableFuture> futures = new ArrayList<>()
		detectors.each { detector ->
			futures.add(CompletableFuture
					.supplyAsync { detector.execute(path) }
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
			detectors = [new GodClassDetector(), new ComplexMethodDetector(), new CommentDetector(),
			             new LongMethodDetector(15), new LongParameterListDetector(), new DeadCodeDetector(),
			             new LargeClassDetector(), new MessageChainDetector(), new MiddleManDetector(),
			             new FeatureEnvyDetector(), new CycleDetector(), new DataClassDetector()]
			build()
		}

		def build() {
			return new DetectorFacade(detectors)
		}
	}


}
