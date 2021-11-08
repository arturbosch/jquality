package io.gitlab.arturbosch.jpal.resolution.solvers

import com.github.javaparser.ast.expr.SimpleName
import io.gitlab.arturbosch.jpal.Helper
import io.gitlab.arturbosch.jpal.core.JPAL
import io.gitlab.arturbosch.jpal.resolution.symbols.SymbolReference
import io.gitlab.arturbosch.jpal.resolution.symbols.WithPreviousSymbolReference
import spock.lang.Specification

import java.util.stream.Collectors

/**
 * @author Artur Bosch
 */
class SymbolSolverTest extends Specification {

	def storage = JPAL.newInstance(Helper.BASE_PATH)
	def solver = new SymbolSolver(storage)
	def info = storage.getCompilationInfo(Helper.RESOLVING_DUMMY).get()

	def "ResolutionDummy - method 1 - all variables"() {
		given: "compilation info for a class"
		def symbols = Helper.nth(info.unit, 0).body.get().getChildNodesByType(SimpleName.class)

		when: "resolving all symbols"
		def symbolReferences = symbols.collect { solver.resolve(it, info).get() }

		then: "all symbols are resolved to INT - JavaReference"
		symbolReferences.each { assert it.qualifiedType.isPrimitive() }
		symbolReferences.stream().filter { it.asVariable().isParameter() }.count() == 1L
	}

	def "ResolutionDummy - method 2 - all variables same name with this"() {
		given: "compilation info for a class"
		def symbols = Helper.nth(info.unit, 1).body.get().getChildNodesByType(SimpleName.class)

		when: "resolving all symbols"
		def symbolReferences = symbols.collect { solver.resolve(it, info).get() }

		then: "this.x must be a field, all others local"
		symbolReferences.stream().filter { it.asVariable().isField() }.collect().size() == 1
		symbolReferences.stream().filter { !it.asVariable().isField() }.allMatch { it.asVariable().isLocaleVariable() }
	}

	def "ResolutionDummy - method 3 - method calls and field accesses, no chains"() {
		given: "compilation info for a class"
		def symbols = Helper.nth(info.unit, 2).body.get().getChildNodesByType(SimpleName.class)

		when: "resolving all symbols"
		def symbolReferences = symbols.collect { solver.resolve(it, info).get() }

		then: "it must resolve three methods and six fields"
		symbolReferences.stream().filter { it.isMethod() }.collect().size() == 3
		symbolReferences.stream().filter { it.isVariable() }
				.map { it.asVariable() }
				.filter { it.isField() }.collect().size() == 6
		symbolReferences.stream().filter { it.isVariable() }
				.map { it.asVariable() }
				.filter { it.isLocaleVariable() }.collect().size() == 3
	}

	def "ResolutionDummy - method 4 - method/field chaining"() {
		given: "compilation info for a class"
		def symbols = Helper.nth(info.unit, 3).body.get().getChildNodesByType(SimpleName.class)

		when: "resolving all symbols"
		def symbolReferences = symbols.collect { solver.resolve(it, info).get() }

		then: "this.x must be a field, all others local"
		symbolReferences.stream().filter { it.isMethod() }.collect().size() == 5
		symbolReferences.stream().filter { it.isVariable() }.map { it.asVariable() }
				.filter { it.isField() }.collect().size() == 2
		symbolReferences.stream().filter { it.isVariable() }.map { it.asVariable() }
				.filter { it.isLocaleVariable() }.collect().size() == 1
	}

	def "ResolutionDummy - method 5 - very long chains"() {
		given: "compilation info for a class"
		def symbols = Helper.nth(info.unit, 4).body.get().getChildNodesByType(SimpleName.class)

		when: "resolving all symbols"
		def symbolReferences = symbols.collect { solver.resolve(it, info).get() }

		then: "end of the chain must be a method with symbol 'getAnInt'"
		symbolReferences.stream().filter { !it.isMethod() }.count() == 8L
		List<SymbolReference> list = symbolReferences.stream().filter { it.isMethod() }.collect(Collectors.toList())
		list.size() == 1
		list[0].symbol.identifier == "getAnInt"
	}

	def "ResolutionDummy - method 6 - simple inheritance"() {
		given: "compilation info for a class with inheritance"
		def symbols = Helper.nth(info.unit, 5).body.get().getChildNodesByType(SimpleName.class)

		when: "resolving all symbols"
		def symbolReferences = symbols.collect { solver.resolve(it, info).get() }

		then: "parent class and interface methods/fields must be found"
		symbolReferences.stream().filter { it.isMethod() }.count() == 5L
		symbolReferences.stream().filter { it.isVariable() }.count() == 1L
	}

	def "ResolutionDummy - method 7 - builder pattern"() {
		when: "resolving all symbols"
		def symbol = Helper.nth(info.unit, 6).body.get().getChildNodesByType(SimpleName.class)
				.grep { it.identifier == "build" }[0]
		def reference = solver.resolve(symbol, info).get() as WithPreviousSymbolReference
		then:
		reference.isBuilderPattern()
	}

	def "loop through previous symbol references"() {
		when:
		def symbol = Helper.nth(info.unit, 4).body.get().getChildNodesByType(SimpleName.class)
				.grep { it.identifier == "getAnInt" }[0]
		def reference = solver.resolve(symbol, info).get() as WithPreviousSymbolReference
		then:
		!reference.isBuilderPattern()
	}

}
