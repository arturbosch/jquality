package io.gitlab.arturbosch.smartsmells.smells.longparam

import com.github.javaparser.ast.body.CallableDeclaration
import com.github.javaparser.ast.body.Parameter
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.ast.MethodHelper
import io.gitlab.arturbosch.jpal.internal.Printer
import io.gitlab.arturbosch.smartsmells.common.visitor.MethodMetricVisitor

/**
 * @author Artur Bosch
 */
@CompileStatic
class LongParameterListVisitor extends MethodMetricVisitor<LongParameterList> {

	List<Parameter> parameters

	LongParameterListVisitor(int threshold) {
		super(threshold)
	}

	@Override
	protected byThreshold(CallableDeclaration n) {
		parameters = MethodHelper.extractParameters(n)
		return parameters.size() > threshold
	}

	@Override
	protected addSmell(CallableDeclaration n) {
		def size = parameters.size()
		def lm = newLongMethod(n, size)
		smells.add(new LongParameterList(lm.name, lm.signature, lm.size,
				lm.threshold, parameters.collect { it.toString(Printer.NO_COMMENTS) },
				lm.sourceRange, lm.sourcePath, lm.elementTarget))
	}
}
