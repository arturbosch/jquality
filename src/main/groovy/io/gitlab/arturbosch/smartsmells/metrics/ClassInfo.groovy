package io.gitlab.arturbosch.smartsmells.metrics

import groovy.transform.Canonical
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.jpal.resolution.QualifiedType

/**
 * @author Artur Bosch
 */
@CompileStatic
@Canonical
class ClassInfo implements HasMetrics {

	final QualifiedType qualifiedType
	final String signature

	final Set<MethodInfo> methods

	@Delegate
	final SourcePath sourcePath
	@Delegate
	final SourceRange sourceRange

	ClassInfo(QualifiedType qualifiedType,
			  String signature,
			  Set<MethodInfo> methods,
			  Map<String, Metric> metrics,
			  SourcePath sourcePath,
			  SourceRange sourceRange) {
		this.qualifiedType = qualifiedType
		this.signature = signature
		this.metrics = metrics
		this.sourcePath = sourcePath
		this.sourceRange = sourceRange
		this.methods = methods
	}

	/**
	 * @return just the simple class name, use qualifiedType.name for full name
	 */
	String getName() {
		return qualifiedType.shortName()
	}

	MethodInfo getMethodByName(String name) {
		return methods.find { it.name == name }
	}

	MethodInfo getMethodByDeclarationString(String declaration) {
		return methods.find { it.declarationString == declaration }
	}

	@Override
	String toString() {
		return "ClassInfo{name=$qualifiedType.name, signature=$signature\n\t\t" +
				metrics.collect { it.toString() }.join("\n\t\t") +
				"}"
	}
}
