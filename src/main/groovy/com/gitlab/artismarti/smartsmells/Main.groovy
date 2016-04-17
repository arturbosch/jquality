package com.gitlab.artismarti.smartsmells

import com.gitlab.artismarti.smartsmells.comment.CommentDetector
import com.gitlab.artismarti.smartsmells.complexmethod.ComplexMethodDetector
import com.gitlab.artismarti.smartsmells.dataclass.DataClassDetector
import com.gitlab.artismarti.smartsmells.godclass.GodClassDetector
import com.gitlab.artismarti.smartsmells.longmethod.LongMethodDetector
import com.gitlab.artismarti.smartsmells.longparam.LongParameterListDetector

import java.nio.file.Paths

/**
 * @author artur
 */
class Main {

	static void main(String... args) {

		def path = Paths.get("/home/artur/Repos/quide/Implementierung/QuideService/src")

		println "GodClasses: " + new GodClassDetector().run(path)
				.stream().count()
		println "ComplexMethods: " + new ComplexMethodDetector().run(path)
				.stream().count()
		println "CommentSmell: " + new CommentDetector().run(path)
				.stream().count()
		println "LongMethod: " + new LongMethodDetector(15)
				.run(path)
				.stream().count()
		println "LongParameterList: " + new LongParameterListDetector()
				.run(path)
				.stream().count()
		println "DataClass: " + new DataClassDetector()
				.run(path)
				.stream().count()
	}
}
