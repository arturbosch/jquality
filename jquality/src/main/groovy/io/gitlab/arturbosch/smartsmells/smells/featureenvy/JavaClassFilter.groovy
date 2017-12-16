package io.gitlab.arturbosch.smartsmells.smells.featureenvy

import com.github.javaparser.ast.type.PrimitiveType
import io.gitlab.arturbosch.jpal.ast.custom.JpalVariable
import io.gitlab.arturbosch.jpal.core.CompilationInfo
import io.gitlab.arturbosch.jpal.resolution.Resolver

import java.util.stream.Collectors

/**
 * @author artur
 */
class JavaClassFilter {

	private CompilationInfo info
	private Resolver resolver

	JavaClassFilter(CompilationInfo info, Resolver resolver) {
		this.resolver = resolver
		this.info = info
	}

	Set<JpalVariable> filter(Set<JpalVariable> variables) {
		return variables.stream().filter { isNoPrimitiveType(it) }
				.filter { !isJavaType(it) }.collect(Collectors.toSet())
	}

	private static boolean isNoPrimitiveType(JpalVariable variable) {
		return !(variable.getType() instanceof PrimitiveType)
	}

	private boolean isJavaType(JpalVariable variable) {
		return resolver.resolveType(variable.type, info).isFromJdk()
	}
}
