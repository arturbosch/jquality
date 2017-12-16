package io.gitlab.arturbosch.smartsmells.smells.classdatashouldbeprivate

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.api.Detector
import io.gitlab.arturbosch.smartsmells.common.Visitor
import io.gitlab.arturbosch.smartsmells.config.Smell
import io.gitlab.arturbosch.smartsmells.metrics.raisers.NOA
import io.gitlab.arturbosch.smartsmells.smells.ElementTarget

/**
 * @author artur
 */
@CompileStatic
class ClassDataShouldBePrivateDetector extends Detector<ClassDataShouldBePrivate> {

	@Override
	protected ClassDataShouldBePrivateVisitor getVisitor() {
		return new ClassDataShouldBePrivateVisitor()
	}

	@Override
	Smell getType() {
		return Smell.ClASS_DATA_SHOULD_BE_PRIVATE
	}
}

@CompileStatic
class ClassDataShouldBePrivateVisitor extends Visitor<ClassDataShouldBePrivate> {

	@Override
	void visit(ClassOrInterfaceDeclaration n, Resolver resolver) {
		def clazz = infoForClass(n)
		if (!n.isStatic() && clazz) {
			def nopa = clazz.getMetric(NOA.NUMBER_OF_PUBLIC_ATTRIBUTES)?.value ?: 0
			if (nopa > 0) {
				report(new ClassDataShouldBePrivate(clazz.name, clazz.signature,
						clazz.sourceRange, clazz.sourcePath, ElementTarget.CLASS))
			}
		}
		super.visit(n, resolver)
	}
}
