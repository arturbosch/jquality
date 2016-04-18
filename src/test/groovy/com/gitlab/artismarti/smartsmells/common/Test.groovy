package com.gitlab.artismarti.smartsmells.common

import java.nio.file.Path
import java.nio.file.Paths

/**
 * @author artur
 */
class Test {
	static Path PATH = Paths.get("./src/test/groovy")
	static Path COMPLEX_METHOD_DUMMY_PATH =
			Paths.get("./src/test/groovy/com/gitlab/artismarti/smartsmells/java/ComplexMethodDummy.java")
	static Path GOD_CLASS_DUMMY_PATH =
			Paths.get("./src/test/groovy/com/gitlab/artismarti/smartsmells/java/GodClassDummy.java")
	static Path DEAD_CODE_PATH =
			Paths.get("./src/test/groovy/com/gitlab/artismarti/smartsmells/java/DeadCodeDummy.java")
}
