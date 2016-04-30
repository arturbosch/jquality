package com.gitlab.artismarti.smartsmells

import com.gitlab.artismarti.smartsmells.comment.CommentDetector
import com.gitlab.artismarti.smartsmells.common.Detector
import com.gitlab.artismarti.smartsmells.complexmethod.ComplexMethodDetector
import com.gitlab.artismarti.smartsmells.cycle.CompilationTree
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

	static def run(Path startPath) {

		CompilationTree.registerRoot(startPath)

		def detectors = [new GodClassDetector(), new ComplexMethodDetector(), new CommentDetector(),
		                 new LongMethodDetector(15), new LongParameterListDetector(), new DeadCodeDetector(),
		                 new LargeClassDetector(), new MessageChainDetector(), new MiddleManDetector(),
		                 new FeatureEnvyDetector(), new CycleDetector()]

		Files.walk(startPath)
				.filter { it.fileName.toString().endsWith("java") }
				.forEach { runRun(detectors, it) }

		detectors.each { println it.class.simpleName + " : " + it.smells.size() }

	}

	static def runRun(List<Detector> detectors, Path path) {
		List<CompletableFuture> futures = new ArrayList<>()
		detectors.each { detector ->
			futures.add(CompletableFuture
					.supplyAsync { detector.execute(path) }
					.exceptionally { handle(it) })
		}
		CompletableFuture.allOf(futures.toArray(new CompletableFuture<?>[0])).join()
	}

	static def start(Path path) {

		def gc = CompletableFuture
				.supplyAsync({ new GodClassDetector().run(path) })
				.exceptionally({ handle(it) })
		def cm = CompletableFuture
				.supplyAsync({ new ComplexMethodDetector().run(path) })
				.exceptionally({ handle(it) })
		def cs = CompletableFuture
				.supplyAsync({ new CommentDetector().run(path) })
				.exceptionally({ handle(it) })
		def lm = CompletableFuture
				.supplyAsync({ new LongMethodDetector(15).run(path) })
				.exceptionally({ handle(it) })
		def lpl = CompletableFuture
				.supplyAsync({ new LongParameterListDetector().run(path) })
				.exceptionally({ handle(it) })
		def dc = CompletableFuture
				.supplyAsync({ new DataClassDetector().run(path) })
				.exceptionally({ handle(it) })
		def dcd = CompletableFuture
				.supplyAsync({ new DeadCodeDetector().run(path) })
				.exceptionally({ handle(it) })
		def lc = CompletableFuture
				.supplyAsync({ new LargeClassDetector().run(path) })
				.exceptionally({ handle(it) })
		def mc = CompletableFuture
				.supplyAsync({ new MessageChainDetector().run(path) })
				.exceptionally({ handle(it) })
		def mm = CompletableFuture
				.supplyAsync({ new MiddleManDetector().run(path) })
				.exceptionally({ handle(it) })
		def fe = CompletableFuture
				.supplyAsync({ new FeatureEnvyDetector().run(path) })
				.exceptionally({ handle(it) })
		def c = CompletableFuture
				.supplyAsync({ new CycleDetector().run(path) })
				.exceptionally({ handle(it) })

		CompletableFuture.allOf(gc, cm, cs, lm, lpl, dc, dcd, lc, mc, mm, fe, c).join()

		println "GodClasses: " + gc.get().stream().count()
		println "ComplexMethods: " + cm.get().stream().count()
		println "CommentSmell: " + cs.get().stream().count()
		println "LongMethod: " + lm.get().stream().count()
		println "LongParameterList: " + lpl.get().stream().count()
		println "DataClass: " + dc.get().stream().count()
		println "DeadCode: " + dcd.get().stream().count()
		println "LargeClass: " + lc.get().stream().count()
		println "MessageChain: " + mc.get().stream().count()
		println "MiddleMan: " + mm.get().stream().count()
		println "FeatureEnvy: " + fe.get().stream().count()
		println "Cycles: " + c.get().stream().count()

		c.get().each { println it.toString() }
	}

	private static ArrayList handle(Throwable throwable) {
		println throwable.printStackTrace()
		new ArrayList<>()
	}

	static def syncStart(Path path) {
		println "GodClasses: " + new GodClassDetector().run(path)
				.stream().count()
		println "ComplexMethods: " + new ComplexMethodDetector().run(path)
				.stream().count()
		println "CommentSmell: " + new CommentDetector().run(path)
				.stream().count()
		println "LongMethod: " + new LongMethodDetector(15).run(path)
				.stream().count()
		println "LongParameterList: " + new LongParameterListDetector().run(path)
				.stream().count()
		println "DataClass: " + new DataClassDetector().run(path)
				.stream().count()
		println "DeadCode: " + new DeadCodeDetector().run(path)
				.stream().count()
		println "LargeClass: " + new LargeClassDetector().run(path)
				.stream().count()
		println "MessageChain: " + new MessageChainDetector().run(path)
				.stream().count()
		println "FeatureEnvy: " + new FeatureEnvyDetector().run(path)
				.stream().count()
		println "Cycles: " + new CycleDetector().run(path)
				.stream().count()
	}
}
