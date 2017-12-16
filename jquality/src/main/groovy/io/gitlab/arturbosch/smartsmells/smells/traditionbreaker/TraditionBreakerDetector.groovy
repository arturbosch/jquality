package io.gitlab.arturbosch.smartsmells.smells.traditionbreaker

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.TypeDeclaration
import groovy.transform.Canonical
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.core.CompilationInfo
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.api.Detector
import io.gitlab.arturbosch.smartsmells.common.Visitor
import io.gitlab.arturbosch.smartsmells.config.Smell
import io.gitlab.arturbosch.smartsmells.metrics.ClassInfo
import io.gitlab.arturbosch.smartsmells.metrics.raisers.AMW
import io.gitlab.arturbosch.smartsmells.metrics.raisers.NAS
import io.gitlab.arturbosch.smartsmells.metrics.raisers.NOM
import io.gitlab.arturbosch.smartsmells.metrics.raisers.WMC
import io.gitlab.arturbosch.smartsmells.smells.ElementTarget
import io.gitlab.arturbosch.smartsmells.util.Validate

/**
 * @author Artur Bosch
 */
@CompileStatic
class TraditionBreakerDetector extends Detector<TraditionBreaker> {

	private final TBConfig tbConfig

	TraditionBreakerDetector(TBConfig config = new TBConfig()) {
		this.tbConfig = config
	}

	@Override
	protected Visitor<TraditionBreaker> getVisitor() {
		return new TraditionBreakerVisitor(tbConfig)
	}

	@Override
	Smell getType() {
		return Smell.TRADITION_BREAKER
	}
}

/**
 * ((NAS >= Average NOM per class) AND (TWO_THIRDS >= Two Thirds)) AND
 * (((AMW > Average) OR (WMC >= Very High)) AND (NOM >= High)) AND
 * ((Parent’s AMW > Average) AND (Parent’s NOM > High/2) AND (Parent’s WMC >= Very High/2))
 *
 * http://www.simpleorientedarchitecture.com/how-to-identify-a-tradition-breaker-using-ndepend/
 */
@Canonical
class TBConfig {
	int averageNOMPerClass = 7
	double TWO_THIRDS = 0.66d
	double AMW = 2.0d
	int WMC = 47
	int HIGH = 10
}

@CompileStatic
class TraditionBreakerVisitor extends Visitor<TraditionBreaker> {

	private final TBConfig config

	TraditionBreakerVisitor(TBConfig config = new TBConfig()) {
		this.config = config
	}

	@Override
	void visit(ClassOrInterfaceDeclaration n, Resolver resolver) {
		if (n.interface) {
			return
		}

		def extendedTypes = n.extendedTypes
		if (extendedTypes.size() != 1) {
			return // a class must have one super class
		}

		def superClass = extendedTypes[0]
		def qualifiedSuperType = resolver.resolveType(superClass, info) // is super class in code base?
		resolver.find(qualifiedSuperType).ifPresent { CompilationInfo superInfo ->
			superInfo.getTypeDeclarationByQualifier(qualifiedSuperType).ifPresent { TypeDeclaration superDecl ->
				Validate.isTrue(superDecl instanceof ClassOrInterfaceDeclaration, "Unexpected non class parent.")
				def classInfo = infoForClass(n)
				def parentInfo = infoForClass(superDecl as ClassOrInterfaceDeclaration, superInfo)
				if (classInfo && parentInfo) {
					raiseMetrics(classInfo, parentInfo)
				}
			}
		}
	}

	private void raiseMetrics(ClassInfo clazz, ClassInfo parent) {
		def nas = clazz.getMetric(NAS.NUMBER_OF_ADDED_SERVICES)?.value ?: 0
		def pnas = clazz.getMetric(NAS.PERCENTAGE_OF_NEWLY_ADDED_SERVICES)?.asDouble() ?: 0.0d
		def nom = clazz.getMetric(NOM.NUMBER_OF_METHODS)?.value ?: 0
		def nomParent = parent.getMetric(NOM.NUMBER_OF_METHODS)?.value ?: 0
		def wmc = clazz.getMetric(WMC.WEIGHTED_METHOD_COUNT)?.value ?: 0
		def wmcParent = parent.getMetric(WMC.WEIGHTED_METHOD_COUNT)?.value ?: 0
		def amw = clazz.getMetric(AMW.AVERAGE_METHOD_WEIGHT)?.value ?: 0
		def amwParent = parent.getMetric(AMW.AVERAGE_METHOD_WEIGHT)?.value ?: 0

		if ((nas >= config.averageNOMPerClass && pnas >= config.TWO_THIRDS) &&
				((amw > config.AMW || wmc >= config.WMC) && nom >= config.HIGH) &&
				(amwParent > config.AMW && (nomParent as double) > config.HIGH / 2 &&
						(wmcParent as double) >= config.WMC / 2)) {

			report(new TraditionBreaker(clazz.name, clazz.signature, clazz.sourceRange,
					clazz.sourcePath, ElementTarget.CLASS))
		}
	}
}
