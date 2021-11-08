package io.gitlab.arturbosch.jpal.resolution.solvers

import com.github.javaparser.ast.expr.SimpleName
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.core.CompilationInfo
import io.gitlab.arturbosch.jpal.core.CompilationStorage
import io.gitlab.arturbosch.jpal.resolution.symbols.SymbolReference

/**
 * @author Artur Bosch
 */
@CompileStatic
final class SymbolSolver implements Solver {

	private CompilationStorage storage
	private TypeSolver resolver
	private MethodLevelVariableSymbolSolver variableSolver
	private GlobalClassLevelSymbolSolver globalSolver
	private LocalClassLevelSymbolSolver classLevelSolver
	private DeclarationLevelSymbolSolver declarationSolver
	private NameLevelSymbolSolver nameLevelSolver

	SymbolSolver(CompilationStorage storage) {
		this.storage = storage
		this.resolver = new TypeSolver(storage)
		this.variableSolver = new MethodLevelVariableSymbolSolver(resolver)
		this.classLevelSolver = new LocalClassLevelSymbolSolver(resolver)
		this.declarationSolver = new DeclarationLevelSymbolSolver(storage, resolver)
		this.nameLevelSolver = new NameLevelSymbolSolver(storage, resolver, variableSolver)
		this.globalSolver = new GlobalClassLevelSymbolSolver(storage, resolver, nameLevelSolver, classLevelSolver)
	}

	@Override
	Optional<? extends SymbolReference> resolve(SimpleName symbol, CompilationInfo info) {
		// if symbol is in a declaration, solving is trivial
		def symbolReference = declarationSolver.resolve(symbol, info)
		if (symbolReference.isPresent()) return symbolReference

		// Is it a static access?
		symbolReference = nameLevelSolver.resolve(symbol, info)
		if (symbolReference.isPresent()) return symbolReference

		// if the calls/accesses are chained it is still possible for the symbol to refer to a local class
		// so a global solver has also a local class solver. A global solver furthermore can use the
		// compilation storage to navigate through called/accessed classes by resolving the whole message chain
		symbolReference = globalSolver.resolve(symbol, info)
		if (symbolReference.isPresent()) return symbolReference

		// Ok, the symbol is located inside this resolution data
		return variableSolver.resolve(symbol, info)
	}

}
