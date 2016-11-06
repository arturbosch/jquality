package io.gitlab.arturbosch.smartsmells.smells.featureenvy

import com.github.javaparser.ast.type.PrimitiveType
import io.gitlab.arturbosch.jpal.ast.custom.JpalVariable
import io.gitlab.arturbosch.jpal.resolve.ResolutionData
import io.gitlab.arturbosch.jpal.resolve.Resolver

import java.util.stream.Collectors

/**
 * @author artur
 */
class JavaClassFilter {

	ResolutionData resolutionData

	JavaClassFilter(ResolutionData resolutionData) {
		this.resolutionData = resolutionData
	}

	Set<JpalVariable> forJavaClasses(Set<JpalVariable> variables) {
		return variables.stream().filter { isNoPrimitiveType(it) }
				.filter { !isJavaType(it) }.collect(Collectors.toSet())
	}

	private static boolean isNoPrimitiveType(JpalVariable variable) {
		return !(variable.getType() instanceof PrimitiveType)
	}

	private boolean isJavaType(JpalVariable variable) {
		return Resolver.getQualifiedType(resolutionData, variable.type).isFromJdk()
	}
}
