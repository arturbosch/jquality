package com.gitlab.artismarti.smartsmells.common

import com.github.javaparser.ASTHelper
import com.github.javaparser.JavaParser
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.MethodDeclaration
import org.codehaus.groovy.runtime.IOGroovyMethods

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * @author artur
 */
class Test {
	static Path PATH = Paths.get("./src/test/groovy")
	static Path COMMENT_DUMMY_PATH =
			Paths.get("./src/test/groovy/com/gitlab/artismarti/smartsmells/java/CommentDummy.java")
	static Path CYCLE_DUMMY_PATH =
			Paths.get("./src/test/groovy/com/gitlab/artismarti/smartsmells/java/CycleDummy.java")
	static Path SINGLETON_CYCLE_PATH =
			Paths.get("./src/test/groovy/com/gitlab/artismarti/smartsmells/java/SingletonCycleDummy.java")
	static Path DATA_CLASS_DUMMY_PATH =
			Paths.get("./src/test/groovy/com/gitlab/artismarti/smartsmells/java/DataClassDummy.java")
	static Path EMPTY_DATA_CLASS_DUMMY_PATH =
			Paths.get("./src/test/groovy/com/gitlab/artismarti/smartsmells/java/EmptyDataClassDummy.java")
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
	static Path MIDDLE_MAN_PATH =
			Paths.get("./src/test/groovy/com/gitlab/artismarti/smartsmells/java/MiddleManDummy.java")
	static Path FEATURE_ENVY_PATH =
			Paths.get("./src/test/groovy/com/gitlab/artismarti/smartsmells/java/FeatureEnvyDummy.java")

	static CompilationUnit compile(Path path) {
		return IOGroovyMethods.withCloseable(Files.newInputStream(path)) {
			JavaParser.parse(it)
		}
	}

	static MethodDeclaration nth(CompilationUnit unit, int n) {
		ASTHelper.getNodesByType(unit, MethodDeclaration.class).get(n)
	}
}
