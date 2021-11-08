package io.gitlab.arturbosch.jpal.ast

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import io.gitlab.arturbosch.jpal.Helper
import io.gitlab.arturbosch.jpal.core.JPAL
import io.gitlab.arturbosch.jpal.resolution.Resolver
import spock.lang.Specification

import java.nio.file.Paths

/**
 * @author Artur Bosch
 */
class TypeHelperAncestorTest extends Specification {

	def "find all ancestors of a class"() {
		given: "a class with ancestors"
		def path = Helper.BASE_PATH.resolve("resolving/AncestorResolveType.java")
		def storage = JPAL.newInstance(Helper.BASE_PATH.resolve("resolving"))
		def info = storage.getCompilationInfo(path).get()
		def resolver = new Resolver(storage)
		def clazz = info.mainType as ClassOrInterfaceDeclaration
		when: "searching for ancestors"
		def qualifiedTypes = TypeHelper.findAllAncestors(clazz, resolver)
		then: "three ancestors are found"
		qualifiedTypes.size() == 5
		qualifiedTypes.find { it.shortName == "SubSolveTypeDummy" }
		qualifiedTypes.find { it.shortName == "SolveType" }
		qualifiedTypes.find { it.shortName == "SolveTypeDummy" }
		qualifiedTypes.find { it.shortName == "Ancestor" }
		qualifiedTypes.find { it.shortName == "SubAncestorType" }
	}

	def "false positive cyclic inheritance"() {
		given: "class which extends class with same name: ZipEntry extends java.util.ZipEntry"
		def path = Paths.get(getClass().getResource("/invalid").path)
		def file = path.resolve("ZipEntry.java")
		def storage = JPAL.newInstance(path)
		def info = storage.getCompilationInfo(file).get()
		def resolver = new Resolver(storage)
		def clazz = info.mainType as ClassOrInterfaceDeclaration
		when: "resolving"
		def qualifiedTypes = TypeHelper.findAllAncestors(clazz, resolver)
		then: "cyclic names are not resolved"
		qualifiedTypes.size() == 1
		qualifiedTypes.find { it.shortName == "Cloneable" }
	}

	def "searching for qualified type in inner classes should map to declaration after optional"() {
		given:
		def path = Paths.get(getClass().getResource("/invalid").path)
		def file = path.resolve("ErrorWhenNotEqualFactory.java")
		def storage = JPAL.newInstance(path)
		def info = storage.getCompilationInfo(file).get()
		def resolver = new Resolver(storage)
		def clazz = info.mainType as ClassOrInterfaceDeclaration
		when: "resolving"
		def qualifiedTypes = TypeHelper.findAllAncestors(clazz, resolver, info)
		then: "no NPE appears"
		qualifiedTypes.size() == 1
		qualifiedTypes.find { it.shortName == "AssertionErrorFactory" }
	}
}
