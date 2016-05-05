package com.gitlab.artismarti.smartsmells

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
//		def path = Paths.get("/home/artur/Repos/quide/Implementierung/QuideService/src")
		def path = Paths.get("/home/artur/Repos/ollijasmus/ManagePSP")
//		def path = Paths.get("/home/artur/Arbeit/pooka-co/trunk/pooka/src")
//		def path = Paths.get("/home/artur/Arbeit/tools/ismell/src")

		(0..10).each { asyncTest(path) }
	}

	static asyncTest(Path path) {
		println "\n Async Duration: " + benchmark { DetectorFacade.builder().fullStackFacade().run(path) } / 1000
	}

}
