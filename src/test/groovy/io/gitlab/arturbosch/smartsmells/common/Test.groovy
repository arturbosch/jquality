package io.gitlab.arturbosch.smartsmells.common

import com.github.javaparser.JavaParser
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.IOGroovyMethods

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * @author artur
 */
@CompileStatic
class Test {
	static Path PATH = Paths.get("./src/test/groovy/io/gitlab/arturbosch/smartsmells/java/")
	static Path BASE_PATH = Paths.get("./src/test/groovy/io/gitlab/arturbosch/smartsmells/java/")
	static Path CYCLES_PATH = BASE_PATH.resolve("cycles")
	static Path COMMENT_DUMMY_PATH = BASE_PATH.resolve("CommentDummy.java")
	static Path JAVADOC_DUMMY_PATH = BASE_PATH.resolve("JavadocDummy.java")
	static Path CYCLE_DUMMY_PATH = BASE_PATH.resolve("cycles/CycleDummy.java")
	static Path SINGLETON_CYCLE_PATH = BASE_PATH.resolve("cycles/SingletonCycleDummy.java")
	static Path DATA_CLASS_DUMMY_PATH = BASE_PATH.resolve("DataClassDummy.java")
	static Path EMPTY_DATA_CLASS_DUMMY_PATH = BASE_PATH.resolve("EmptyDataClassDummy.java")
	static Path COMPLEX_METHOD_DUMMY_PATH = BASE_PATH.resolve("ComplexMethodDummy.java")
	static Path COMPLEX_CONDITION_DUMMY_PATH = BASE_PATH.resolve("ComplexConditionDummy.java")
	static Path LONG_METHOD_DUMMY_PATH = BASE_PATH.resolve("LongMethodDummy.java")
	static Path GOD_CLASS_DUMMY_PATH = BASE_PATH.resolve("GodClassDummy.java")
	static Path DEAD_CODE_PATH = BASE_PATH.resolve("DeadCodeDummy.java")
	static Path DEAD_CODE_CONDITIONAL_PATH = BASE_PATH.resolve("ConditionalDeadCodeDummy.java")
	static Path MESSAGE_CHAIN_PATH = BASE_PATH.resolve("MessageChainDummy.java")
	static Path MIDDLE_MAN_PATH = BASE_PATH.resolve("MiddleManDummy.java")
	static Path FEATURE_ENVY_PATH = BASE_PATH.resolve("FeatureEnvyDummy.java")
	static Path STATE_CHECKING_PATH = BASE_PATH.resolve("StateCheckingDummy.java")

	static CompilationUnit compileContent(String content) {
		return JavaParser.parse(content)
	}

	static CompilationUnit compile(Path path) {
		return IOGroovyMethods.withCloseable(Files.newInputStream(path)) {
			JavaParser.parse(it)
		}
	}

	static MethodDeclaration nth(CompilationUnit unit, int n) {
		unit.getChildNodesByType(MethodDeclaration.class).get(n)
	}

	static ClassOrInterfaceDeclaration firstClass(CompilationUnit unit) {
		unit.getChildNodesByType(ClassOrInterfaceDeclaration.class).first()
	}
}
