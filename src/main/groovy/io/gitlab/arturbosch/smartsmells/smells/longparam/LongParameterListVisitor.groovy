package io.gitlab.arturbosch.smartsmells.smells.longparam

import com.github.javaparser.ast.body.BodyDeclaration
import com.github.javaparser.ast.body.Parameter
import io.gitlab.arturbosch.jpal.ast.MethodHelper
import io.gitlab.arturbosch.jpal.internal.Printer
import io.gitlab.arturbosch.smartsmells.common.visitor.MethodMetricVisitor

/**
 * @author artur
 */
class LongParameterListVisitor extends MethodMetricVisitor<LongParameterList> {

	List<Parameter> parameters

	LongParameterListVisitor(int threshold) {
		super(threshold)
	}

	@Override
	protected byThreshold(BodyDeclaration n) {
		parameters = MethodHelper.extractParameters(n)
		return parameters.size() > threshold
	}

	@Override
	protected addSmell(BodyDeclaration n) {
		def size = parameters.size()
		def lm = newLongMethod(n, size)
		smells.add(new LongParameterList(lm.name, lm.signature, lm.size,
				lm.threshold, parameters.collect { it.toString(Printer.NO_COMMENTS) },
				lm.sourceRange, lm.sourcePath, lm.elementTarget))
	}
}
