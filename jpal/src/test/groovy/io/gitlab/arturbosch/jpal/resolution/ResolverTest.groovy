package io.gitlab.arturbosch.jpal.resolution

import com.github.javaparser.ast.expr.SimpleName
import com.github.javaparser.ast.type.ClassOrInterfaceType
import io.gitlab.arturbosch.jpal.Helper
import io.gitlab.arturbosch.jpal.core.JPAL
import spock.lang.Ignore
import spock.lang.Specification

/**
 * @author Artur Bosch
 */
class ResolverTest extends Specification {

	private path = Helper.BASE_PATH.resolve("resolving/SolveTypeDummy.java")
	private storage = JPAL.newInstance(path)
	private info = storage.getCompilationInfo(path).get()
	private resolver = new Resolver(storage)

	def "Resolve"() {
		when: "resolving a type"
		def type = resolver.resolveType(new ClassOrInterfaceType("SolveTypeDummy"), info)
		then: "its the qualified type of dummy"
		type.name == "io.gitlab.arturbosch.jpal.dummies.resolving.SolveTypeDummy"
	}

	def "ResolveType"() {
		given: "all symbols of dummy class"
		def symbols = info.unit.getChildNodesByType(SimpleName.class)
		println(symbols)
		when: "resolving the symbols"
		def references = symbols.collect { resolver.resolve(it, info) }
		references.withIndex().each {println("$it.second - ${it.first.isPresent()}")}
		println(references)
		then: "all must be resolved"
		references.stream().allMatch { it.isPresent() }
	}

	@SuppressWarnings("GrUnnecessaryDefModifier")
	@Ignore
	def "performance"() {
		given: "symbols"
		def symbols = info.unit.getChildNodesByType(SimpleName)

		when: "running collect symbols 100 times"
		def times = 100
		def time = (0..times).collect {
			benchmark {
				symbols.collect { resolver.resolve(it, info) }
			}
		}.sum() / times
		println "$time ms"
		then: "it should be faster with symbol table"
		true
	}

	def benchmark = { Closure block ->
		def start = System.currentTimeMillis()
		block()
		System.currentTimeMillis() - start
	}

}
