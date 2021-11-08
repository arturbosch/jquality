package io.gitlab.arturbosch.jpal.resolution.solvers

import com.github.javaparser.ast.type.ClassOrInterfaceType
import com.github.javaparser.ast.type.PrimitiveType
import com.github.javaparser.ast.type.UnknownType
import io.gitlab.arturbosch.jpal.Helper
import io.gitlab.arturbosch.jpal.core.CompilationStorage
import io.gitlab.arturbosch.jpal.core.JPAL
import io.gitlab.arturbosch.jpal.resolution.QualifiedType
import io.gitlab.arturbosch.jpal.resolution.ResolutionData
import io.gitlab.arturbosch.jpal.resolution.Resolver
import spock.lang.Specification

/**
 * @author artur
 */
class TypeSolverTest extends Specification {

	CompilationStorage storage
	TypeSolver resolver

	def setup() {
		storage = JPAL.newInstance(Helper.BASE_PATH)
		resolver = new TypeSolver(storage)
	}

	def "get qualified type from imports"() {
		given: "resolution data for cycle dummy with asterisk imports and initialized compilation storage"
		def data = ResolutionData.of(Helper.compile(Helper.CYCLE_DUMMY))
		when: "retrieving qualified type for two types"
		def helper = resolver.getQualifiedType(data, new ClassOrInterfaceType("Helper"))
		def testReference = resolver.getQualifiedType(data, new ClassOrInterfaceType("TestReference"))
		def innerClasses = resolver.getQualifiedType(data,
				new ClassOrInterfaceType("InnerClassesDummy.InnerClass.InnerInnerClass"))
		then: "Helper type is retrieved from qualified import and TestReference from asterisk"
		helper.name == "io.gitlab.arturbosch.jpal.Helper"
		testReference.name == "io.gitlab.arturbosch.jpal.dummies.test.TestReference"
		innerClasses.name == "io.gitlab.arturbosch.jpal.dummies.test.InnerClassesDummy.InnerClass.InnerInnerClass"
	}

	def "domain tests"() {
		expect: "the right qualified types"
		resolver.getQualifiedType(data, importType).isReference()
		resolver.getQualifiedType(data, cycleType).isReference()
		resolver.getQualifiedType(data, innerCycleType).isReference()
		resolver.getQualifiedType(data, javaType).isFromJdk()
		resolver.getQualifiedType(data, primitiveType).isPrimitive()
		resolver.getQualifiedType(data, boxedType).isPrimitive()
		resolver.getQualifiedType(data, unknownType).typeToken == QualifiedType.TypeToken.UNKNOWN

		where: "resolution data from the cycle class and different kinds of class types"
		unit = Helper.compile(Helper.CYCLE_DUMMY)
		data = ResolutionData.of(unit)
		importType = new ClassOrInterfaceType("Helper")
		cycleType = new ClassOrInterfaceType("CycleDummy")
		innerCycleType = new ClassOrInterfaceType("CycleDummy.InnerCycleOne")
		javaType = new ClassOrInterfaceType("ArrayList")
		primitiveType = new PrimitiveType(PrimitiveType.Primitive.BOOLEAN)
		boxedType = primitiveType.toBoxedType()
		unknownType = new UnknownType()
	}

	def "nested classes"() {
		given:
		def path = Helper.BASE_PATH.resolve("resolving").resolve("LongChainResolving.java")
		when:
		def info = new Resolver(storage).find(path)
		then:
		info.get().innerClasses.find {
			it.key.name == "io.gitlab.arturbosch.jpal.dummies.resolving.LongChainResolving.FourthChain.FifthChain"
		} != null
	}

}
