package com.gitlab.artismarti.smartsmells.common

import com.github.javaparser.ASTHelper
import com.github.javaparser.JavaParser
import com.github.javaparser.ParseException
import com.github.javaparser.TokenMgrError
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.gitlab.artismarti.smartsmells.common.helper.TypeHelper
import com.gitlab.artismarti.smartsmells.util.SmartCache
import com.gitlab.artismarti.smartsmells.util.Validate
import org.codehaus.groovy.runtime.IOGroovyMethods

import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Stream

/**
 * @author artur
 */
final class CompilationStorage {

	private Path root
	private SmartCache<QualifiedType, CompilationInfo> cache = new SmartCache<>()

	private CompilationStorage() {}

	static CompilationStorage create(Path root) {
		Validate.isTrue(root != null, "Project path must be not null!")
		this.root = root
		def storage = new CompilationStorage()
		storage.run()
		return storage
	}

	void run() {

	}

	private void compileFor(Path path) {
		IOGroovyMethods.withCloseable(Files.newInputStream(path)) {
			try {
				def unit = JavaParser.parse(it)
				TypeHelper.getQualifiedType(getFirstDeclaredClass(unit))
						.ifPresent {
					cache.put(it, CompilationInfo.of(it, unit, path))
				}
			} catch (ParseException | TokenMgrError ignored) {
			}
		}
	}

	private static ClassOrInterfaceDeclaration getFirstDeclaredClass(CompilationUnit compilationUnit) {
		ASTHelper.getNodesByType(compilationUnit, ClassOrInterfaceDeclaration).first()
	}

	private Stream<Path> getJavaFilteredFileStream() {
		Files.walk(root).filter { it.toString().endsWith(".java") }
	}
}
