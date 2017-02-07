package io.gitlab.arturbosch.smartsmells.common.visitor

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.ast.ClassHelper
import io.gitlab.arturbosch.jpal.core.CompilationInfo
import io.gitlab.arturbosch.jpal.resolution.Resolver

import java.nio.file.Path

/**
 * @author Artur Bosch
 */
@CompileStatic
abstract class InternalVisitor extends VoidVisitorAdapter<Resolver> {

	protected Path path
	protected CompilationInfo info

	void initialize(CompilationInfo info) {
		this.info = info
		path = info.relativePath
	}

	protected static boolean isEmpty(ClassOrInterfaceDeclaration n) {
		ClassHelper.isEmptyBody(n) && ClassHelper.hasNoMethods(n)
	}
}
