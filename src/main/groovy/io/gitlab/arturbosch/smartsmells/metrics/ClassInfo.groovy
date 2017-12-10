package io.gitlab.arturbosch.smartsmells.metrics

import com.github.javaparser.ast.body.CallableDeclaration
import groovy.transform.Canonical
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.jpal.resolution.QualifiedType
import io.gitlab.arturbosch.smartsmells.util.Validate

/**
 * @author Artur Bosch
 */
@CompileStatic
@Canonical
class ClassInfo implements HasMetrics {

	static final ClassInfo NOP = new ClassInfo(QualifiedType.UNKNOWN, "NOP",
			(Map<String, Metric>) Collections.emptyMap(), (SourcePath) null, (SourceRange) null)

	final QualifiedType qualifiedType
	final String signature

	Set<MethodInfo> methods

	@Delegate
	final SourcePath sourcePath
	@Delegate
	final SourceRange sourceRange

	ClassInfo(QualifiedType qualifiedType,
			  String signature,
			  Map<String, Metric> metrics,
			  SourcePath sourcePath,
			  SourceRange sourceRange,
			  Set<MethodInfo> methods = new HashSet<>()) {
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

	MethodInfo getMethodByDeclaration(CallableDeclaration declaration) {
		return methods.find { it.declarationString == declaration.declarationAsString }
	}

	void addMethodInfo(MethodInfo method) {
		methods.add(Validate.notNull(method))
	}

	@Override
	String toString() {
		return "ClassInfo{name=$qualifiedType.name, signature=$signature\n\t\t" +
				metrics.collect { it.toString() }.join("\n\t\t") +
				"}"
	}
}
