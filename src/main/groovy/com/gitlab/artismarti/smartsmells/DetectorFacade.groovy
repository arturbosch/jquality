package com.gitlab.artismarti.smartsmells

import com.gitlab.artismarti.smartsmells.comment.CommentDetector
import com.gitlab.artismarti.smartsmells.complexmethod.ComplexMethodDetector
import com.gitlab.artismarti.smartsmells.dataclass.DataClassDetector
import com.gitlab.artismarti.smartsmells.deadcode.DeadCodeDetector
import com.gitlab.artismarti.smartsmells.godclass.GodClassDetector
import com.gitlab.artismarti.smartsmells.longmethod.LongMethodDetector
import com.gitlab.artismarti.smartsmells.longparam.LongParameterListDetector

import java.nio.file.Path
import java.util.concurrent.CompletableFuture

/**
 * @author artur
 */
class DetectorFacade {

	static def start(Path path) {

		def gc = CompletableFuture
				.supplyAsync({ new GodClassDetector().run(path) })
				.exceptionally({ new ArrayList<>() })
		def cm = CompletableFuture
				.supplyAsync({ new ComplexMethodDetector().run(path) })
				.exceptionally({ new ArrayList<>() })
		def cs = CompletableFuture
				.supplyAsync({ new CommentDetector().run(path) })
				.exceptionally({ new ArrayList<>() })
		def lm = CompletableFuture
				.supplyAsync({ new LongMethodDetector().run(path) })
				.exceptionally({ new ArrayList<>() })
		def lpl = CompletableFuture
				.supplyAsync({ new LongParameterListDetector().run(path) })
				.exceptionally({ new ArrayList<>() })
		def dc = CompletableFuture
				.supplyAsync({ new DataClassDetector().run(path) })
				.exceptionally({ new ArrayList<>() })
		def dcd = CompletableFuture
				.supplyAsync({ new DeadCodeDetector().run(path) })
				.exceptionally({ new ArrayList<>() })

		CompletableFuture.allOf(gc, cm, cs, lm, lpl, dc)

		println "GodClasses: " + gc.get().stream().count()
		println "ComplexMethods: " + cm.get().stream().count()
		println "CommentSmell: " + cs.get().stream().count()
		println "LongMethod: " + lm.get().stream().count()
		println "LongParameterList: " + lpl.get().stream().count()
		println "DataClass: " + dc.get().stream().count()
		println "DeadCode: " + dcd.get().stream().count()

	}

}
