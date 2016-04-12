package com.gitlab.artismarti.ast

import java.nio.file.Paths

/**
 * @author artur
 */
class Main {

	static void main(String... args) {

		new CommentDetector()
				.run(Paths.get("./src/main/groovy"))
				.forEach({ println(it) })

	}
}
