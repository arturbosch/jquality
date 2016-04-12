package com.gitlab.artismarti.smartsmells

import com.gitlab.artismarti.smartsmells.comment.CommentDetector

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
