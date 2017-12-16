package io.gitlab.arturbosch.smartsmells.metrics

import com.github.javaparser.ast.body.CallableDeclaration
import groovy.transform.Canonical
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange

/**
 * @author Artur Bosch
 */
@CompileStatic
@Canonical
class MethodInfo implements HasMetrics {

	final String name
	final String declarationString
	final String signature
	final CallableDeclaration declaration

	@Delegate
	SourcePath sourcePath
	@Delegate
	SourceRange sourceRange

	@PackageScope
	MethodInfo(String name,
			   String declarationString,
			   String signature,
			   Map<String, Metric> metrics,
			   SourcePath sourcePath,
			   SourceRange sourceRange,
			   CallableDeclaration declaration) {
		this.name = name
		this.declarationString = declarationString
		this.signature = signature
		this.metrics = metrics
		this.sourcePath = sourcePath
		this.sourceRange = sourceRange
		this.declaration = declaration
	}

	static MethodInfo of(CallableDeclaration decl, ClassInfo info, List<Metric> metrics = Collections.emptyList()) {
		def asString = decl.declarationAsString
		Map<String, Metric> allMetrics = metrics.collectEntries { [it.type, it] }
		return new MethodInfo(decl.nameAsString, asString, asString, allMetrics,
				info.sourcePath, SourceRange.fromNode(decl), decl)
	}
}
