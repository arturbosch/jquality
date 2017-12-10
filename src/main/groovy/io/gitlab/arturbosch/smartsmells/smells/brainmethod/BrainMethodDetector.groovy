package io.gitlab.arturbosch.smartsmells.smells.brainmethod

import com.github.javaparser.ast.body.CallableDeclaration
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.ConstructorDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import groovy.transform.Canonical
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.api.Detector
import io.gitlab.arturbosch.smartsmells.common.Visitor
import io.gitlab.arturbosch.smartsmells.config.Smell
import io.gitlab.arturbosch.smartsmells.metrics.ClassInfo
import io.gitlab.arturbosch.smartsmells.metrics.internal.LOC
import io.gitlab.arturbosch.smartsmells.metrics.raisers.CYCLO
import io.gitlab.arturbosch.smartsmells.metrics.raisers.MAXNESTING
import io.gitlab.arturbosch.smartsmells.metrics.raisers.NOAV

/**
 * @author Artur Bosch
 */
@CompileStatic
class BrainMethodDetector extends Detector<BrainMethod> {

	private BrainMethodThreshold threshold

	BrainMethodDetector(BrainMethodThreshold threshold = new BrainMethodThreshold()) {
		this.threshold = threshold
	}

	@Override
	protected Visitor<BrainMethod> getVisitor() {
		return new BrainMethodVisitor(threshold)
	}

	@Override
	Smell getType() {
		return Smell.BRAIN_METHOD
	}
}

/**
 * (LOC > HighLocForClass/2) AND (CYCLO >= High) AND (MAXNESTING >= Several) AND (NOAV > Many)
 * Source: http://www.simpleorientedarchitecture.com/how-to-identify-brain-method-using-ndepend/
 */
@Canonical
@CompileStatic
class BrainMethodThreshold {
	int loc = 65
	double cyclo = 3.1d
	int maxnesting = 3
	int noav = 7
}

@CompileStatic
class BrainMethodVisitor extends Visitor<BrainMethod> {

	private BrainMethodThreshold threshold
	private ClassInfo currentClass = ClassInfo.NOP

	BrainMethodVisitor(BrainMethodThreshold threshold) {
		this.threshold = threshold
	}

	@Override
	void visit(ClassOrInterfaceDeclaration n, Resolver arg) {
		currentClass = infoForClass(n)
		super.visit(n, arg)
	}

	@Override
	void visit(MethodDeclaration n, Resolver arg) {
		fromCallable(n)
		super.visit(n, arg)
	}

	private void fromCallable(CallableDeclaration n) {
		def method = currentClass.getMethodByDeclaration(n)
		def sloc = method?.getMetric(LOC.SLOC)?.value
		def cyclo = method?.getMetric(CYCLO.CYCLOMATIC_COMPLEXITY)?.value
		def nesting = method?.getMetric(MAXNESTING.MAXNESTING)?.value
		def noav = method?.getMetric(NOAV.NUMBER_OF_ACCESSED_VARIABLES)?.value
		if (sloc && cyclo && nesting && noav &&
				sloc > threshold.loc &&
				((double) cyclo) >= threshold.cyclo &&
				nesting >= threshold.maxnesting &&
				noav > threshold.noav) {
			report((BrainMethod) BrainMethod.fromInfo(method))
		}
	}

	@Override
	void visit(ConstructorDeclaration n, Resolver arg) {
		fromCallable(n)
		super.visit(n, arg)
	}
}
