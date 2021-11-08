package io.gitlab.arturbosch.jpal.resolution.solvers

import com.github.javaparser.ast.expr.SimpleName
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.core.CompilationInfo
import io.gitlab.arturbosch.jpal.resolution.symbols.SymbolReference

/**
 * @author Artur Bosch
 */
@CompileStatic
interface Solver {
	Optional<? extends SymbolReference> resolve(SimpleName symbol, CompilationInfo info)
}