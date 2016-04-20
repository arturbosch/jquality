package com.gitlab.artismarti.smartsmells

import com.gitlab.artismarti.smartsmells.comment.CommentDetector
import com.gitlab.artismarti.smartsmells.complexmethod.ComplexMethodDetector
import com.gitlab.artismarti.smartsmells.dataclass.DataClassDetector
import com.gitlab.artismarti.smartsmells.deadcode.DeadCodeDetector
import com.gitlab.artismarti.smartsmells.godclass.GodClassDetector
import com.gitlab.artismarti.smartsmells.longmethod.LongMethodDetector
import com.gitlab.artismarti.smartsmells.longparam.LongParameterListDetector

import java.nio.file.Path
import java.nio.file.Paths

/**
 * @author artur
 */
class Main {

	def static benchmark = { closure ->
		def start = System.currentTimeMillis()
		closure.call()
		def now = System.currentTimeMillis()
		now - start
	}

	static void main(String... args) {

		// /main/java/de/uni_bremen/st/quide/detectors/tools/file_metrics/impl/LOC.java
		def path = Paths.get("/home/artur/Repos/quide/Implementierung/QuideService/src")

		for (i in 0..10) syncTest(path)
		println()
		for (i in 0..10) asyncTest(path)

	}

	private static asyncTest(Path path) {
		println "\n Async Duration: " + benchmark { new DetectorFacade().start(path) } / 1000
	}

	private static void syncTest(Path path) {
		println "\n Sync Duration: " + benchmark {

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
		} / 1000
	}
}
