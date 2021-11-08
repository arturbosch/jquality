package io.gitlab.arturbosch.jpal

import com.github.javaparser.JavaParser
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.FieldDeclaration
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
class Helper {

	static String QUALIFIED_TYPE_DUMMY = "io.gitlab.arturbosch.jpal.dummies.Dummy"

	static Path BASE_PATH = Paths.get("./src/test/groovy/io/gitlab/arturbosch/jpal/dummies")
	static Path DUMMY = BASE_PATH.resolve("Dummy.java")
	static Path EMPTY_DUMMY = BASE_PATH.resolve("EmptyDummy.java")
	static Path CYCLE_DUMMY = BASE_PATH.resolve("CycleDummy.java")
	static Path ANONYMOUS_DUMMY = BASE_PATH.resolve("AnonymousDummy.java")
	static Path NO_CONTENT_DUMMY = BASE_PATH.resolve("NoContent.java")
	static Path RESOLVING_DUMMY = BASE_PATH.resolve("ResolvingDummy.java")
	static Path FILTERED_DUMMY = BASE_PATH.resolve("filtered/FilteredDummy.java")
	static Path NO_PACKAGE_DUMMY = Paths.get("./src/test/resources/NoPackage.java")

	static CompilationUnit compile(Path path) {
		return IOGroovyMethods.withCloseable(Files.newInputStream(path)) {
			JavaParser.parse(it)
		}
	}

	static MethodDeclaration nth(CompilationUnit unit, int n) {
		return unit.getChildNodesByType(MethodDeclaration.class).get(n)
	}

	static ClassOrInterfaceDeclaration firstClass(CompilationUnit unit) {
		return unit.getChildNodesByType(ClassOrInterfaceDeclaration.class).first()
	}

	static ClassOrInterfaceDeclaration nthInnerClass(CompilationUnit unit, int n) {
		return firstClass(unit).getChildNodesByType(ClassOrInterfaceDeclaration.class)[n]
	}

	static FieldDeclaration nth(ClassOrInterfaceDeclaration clazz, int n) {
		return clazz.getChildNodesByType(FieldDeclaration.class).get(n)
	}
}
