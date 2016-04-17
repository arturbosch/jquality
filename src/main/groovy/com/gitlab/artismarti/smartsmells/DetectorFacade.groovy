package com.gitlab.artismarti.smartsmells

import com.gitlab.artismarti.smartsmells.comment.CommentDetector
import com.gitlab.artismarti.smartsmells.complexmethod.ComplexMethodDetector
import com.gitlab.artismarti.smartsmells.dataclass.DataClassDetector
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

		def gc = CompletableFuture.supplyAsync({ new GodClassDetector().run(path) })
		def cm = CompletableFuture.supplyAsync({ new ComplexMethodDetector().run(path) })
		def cs = CompletableFuture.supplyAsync({ new CommentDetector().run(path) })
		def lm = CompletableFuture.supplyAsync({ new LongMethodDetector().run(path) })
		def lpl = CompletableFuture.supplyAsync({ new LongParameterListDetector().run(path) })
		def dc = CompletableFuture.supplyAsync({ new DataClassDetector().run(path) })

		CompletableFuture.allOf(gc, cm, cs, lm, lpl, dc)

		println "GodClasses: " + gc.get().stream().count()
		println "ComplexMethods: " + cm.get().stream().count()
		println "CommentSmell: " + cs.get().stream().count()
		println "LongMethod: " + lm.get().stream().count()
		println "LongParameterList: " + lpl.get().stream().count()
		println "DataClass: " + dc.get().stream().count()

	}
}
