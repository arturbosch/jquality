package com.gitlab.artismarti.smartsmells

import com.gitlab.artismarti.smartsmells.longparam.LongParameterListDetector

import java.nio.file.Paths
/**
 * @author artur
 */
class Main {

	static void main(String... args) {

		def path = "/home/artur/Repos/quide/Implementierung/DataModel/src"
		def path1 = Paths.get(path)

//		new CommentDetector()
//				.run(path1)
//				.forEach({ println(it) })
//		new LongMethodDetector(15)
//				.run(path1)
//				.stream().count()
//				.forEach({ println(it) })
		new LongParameterListDetector()
				.run(path1)
				.forEach({ println(it) })
//		new DataClassDetector()
//				.run(path1)
//				.forEach({ println(it) })
	}
}
