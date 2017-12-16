package io.gitlab.arturbosch.smartsmells.smells.largeclass

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.common.Visitor
import io.gitlab.arturbosch.smartsmells.metrics.raisers.LOC
import io.gitlab.arturbosch.smartsmells.smells.ElementTarget

/**
 * @author Artur Bosche
 */
@CompileStatic
class LargeClassVisitor extends Visitor<LargeClass> {

	private int sizeThreshold

	LargeClassVisitor(int sizeThreshold) {
		this.sizeThreshold = sizeThreshold
	}

	@Override
	void visit(ClassOrInterfaceDeclaration n, Resolver resolver) {
		if (n.interface) return

		def classInfo = infoForClass(n)
		if (classInfo) {
			def sum = classInfo.getMetric(LOC.SLOC)?.value ?: 0

			if (sum >= sizeThreshold)
				report(new LargeClass(classInfo.name, classInfo.signature,
						sum, sizeThreshold, classInfo.sourcePath, classInfo.sourceRange, ElementTarget.CLASS))
		}
	}
}
