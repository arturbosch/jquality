package com.gitlab.artismarti.smartsmells

import com.gitlab.artismarti.smartsmells.comment.CommentDetector
import com.gitlab.artismarti.smartsmells.complexmethod.ComplexMethodDetector
import com.gitlab.artismarti.smartsmells.dataclass.DataClassDetector
import com.gitlab.artismarti.smartsmells.deadcode.DeadCodeDetector
import com.gitlab.artismarti.smartsmells.godclass.GodClassDetector
import com.gitlab.artismarti.smartsmells.largeclass.LargeClassDetector
import com.gitlab.artismarti.smartsmells.longmethod.LongMethodDetector
import com.gitlab.artismarti.smartsmells.longparam.LongParameterListDetector
import com.gitlab.artismarti.smartsmells.messagechain.MessageChainDetector
import com.gitlab.artismarti.smartsmells.middleman.MiddleManDetector

import java.nio.file.Path
import java.util.concurrent.CompletableFuture

/**
 * @author artur
 */
class DetectorFacade {

	static def start(Path path) {

		def gc = CompletableFuture
				.supplyAsync({ new GodClassDetector().run(path) })
				.exceptionally({ handle() })
		def cm = CompletableFuture
				.supplyAsync({ new ComplexMethodDetector().run(path) })
				.exceptionally({ handle() })
		def cs = CompletableFuture
				.supplyAsync({ new CommentDetector().run(path) })
				.exceptionally({ handle() })
		def lm = CompletableFuture
				.supplyAsync({ new LongMethodDetector(15).run(path) })
				.exceptionally({ handle() })
		def lpl = CompletableFuture
				.supplyAsync({ new LongParameterListDetector().run(path) })
				.exceptionally({ handle() })
		def dc = CompletableFuture
				.supplyAsync({ new DataClassDetector().run(path) })
				.exceptionally({ handle() })
		def dcd = CompletableFuture
				.supplyAsync({ new DeadCodeDetector().run(path) })
				.exceptionally({ handle() })
		def lc = CompletableFuture
				.supplyAsync({ new LargeClassDetector().run(path) })
				.exceptionally({ handle() })
		def mc = CompletableFuture
				.supplyAsync({ new MessageChainDetector().run(path) })
				.exceptionally({ handle() })
		def mm = CompletableFuture
				.supplyAsync({ new MiddleManDetector().run(path) })
				.exceptionally({ handle() })

		CompletableFuture.allOf(gc, cm, cs, lm, lpl, dc, dcd, lc, mc, mm).join()

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

//		mm.get().forEach { println it.toString() }

	}

	private static ArrayList handle() {
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
	}
}
