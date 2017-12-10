package io.gitlab.arturbosch.smartsmells.metrics.raisers

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.core.CompilationInfo
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.metrics.ClassInfo
import io.gitlab.arturbosch.smartsmells.metrics.FileInfo

/**
 * @author Artur Bosch
 */
@CompileStatic
trait MetricPreListener {

	int priority() {
		return 0
	}

	abstract void raise(ClassOrInterfaceDeclaration aClass, CompilationInfo info, Resolver resolver)

	ClassInfo findClassInfo(ClassOrInterfaceDeclaration aClass, CompilationInfo info) {
		return info.getData(FileInfo.KEY).findClassByName(aClass.nameAsString)
	}
}
