package io.gitlab.arturbosch.smartsmells.common.visitor

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.ast.ClassHelper
import io.gitlab.arturbosch.jpal.core.CompilationInfo
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.metrics.ClassInfo
import io.gitlab.arturbosch.smartsmells.metrics.FileInfo

import java.nio.file.Path

/**
 * @author Artur Bosch
 */
@CompileStatic
abstract class InternalVisitor extends VoidVisitorAdapter<Resolver> {

	protected Path relativePath
	protected CompilationInfo info

	void visit(CompilationInfo info, Resolver resolver) {
		this.info = info
		relativePath = info.path
		visit(info.unit, resolver)
	}

	protected static boolean isEmpty(ClassOrInterfaceDeclaration n) {
		ClassHelper.isEmptyBody(n) && ClassHelper.hasNoMethods(n)
	}

	protected final ClassInfo infoForClass(ClassOrInterfaceDeclaration clazz) {
		return statistics()?.findClassByName(clazz.nameAsString)
	}

	protected static final ClassInfo infoForClass(ClassOrInterfaceDeclaration clazz, CompilationInfo cinfo) {
		return cinfo.getData(FileInfo.KEY)?.findClassByName(clazz.nameAsString)
	}

	protected final FileInfo statistics() {
		return info.getData(FileInfo.KEY)
	}
}
