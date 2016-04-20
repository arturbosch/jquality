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
	static Path LONG_METHOD_DUMMY_PATH =
			Paths.get("./src/test/groovy/com/gitlab/artismarti/smartsmells/java/LongMethodDummy.java")
	static Path GOD_CLASS_DUMMY_PATH =
			Paths.get("./src/test/groovy/com/gitlab/artismarti/smartsmells/java/GodClassDummy.java")
	static Path DEAD_CODE_PATH =
			Paths.get("./src/test/groovy/com/gitlab/artismarti/smartsmells/java/DeadCodeDummy.java")
	static Path DEAD_CODE_CONDITIONAL_PATH =
			Paths.get("./src/test/groovy/com/gitlab/artismarti/smartsmells/java/ConditionalDeadCodeDummy.java")
	static Path MESSAGE_CHAIN_PATH =
			Paths.get("./src/test/groovy/com/gitlab/artismarti/smartsmells/java/MessageChainDummy.java")
}
