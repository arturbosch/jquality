package io.gitlab.arturbosch.smartsmells.common.visitor

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.ast.ClassHelper

import java.nio.file.Path

/**
 * @author Artur Bosch
 */
@CompileStatic
abstract class InternalVisitor extends VoidVisitorAdapter<Object> {

	protected Path thePath

	InternalVisitor(Path thePath) {
		this.thePath = thePath
	}

	protected abstract void visit(ClassOrInterfaceDeclaration n)

	protected static boolean isEmpty(ClassOrInterfaceDeclaration n) {
		ClassHelper.isEmptyBody(n) && ClassHelper.hasNoMethods(n)
	}
}
