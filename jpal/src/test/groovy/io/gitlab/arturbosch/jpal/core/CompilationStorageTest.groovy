package io.gitlab.arturbosch.jpal.core

import com.github.javaparser.ast.DataKey
import io.gitlab.arturbosch.jpal.Helper
import io.gitlab.arturbosch.jpal.resolution.QualifiedType
import io.gitlab.arturbosch.jpal.resolution.Resolver
import spock.lang.Specification

/**
 * @author artur
 */
class CompilationStorageTest extends Specification {

	private QualifiedType cycleType = new QualifiedType("io.gitlab.arturbosch.jpal.dummies.CycleDummy",
			QualifiedType.TypeToken.REFERENCE)

	private QualifiedType innerCycleType = new QualifiedType(cycleType.name + ".InnerCycleOne",
			QualifiedType.TypeToken.REFERENCE)

	def "domain tests"() {
		given:
		def storage = JPAL.newInstance(Helper.BASE_PATH)

		when: "retrieving stored package names"
		def packageNames = storage.getStoredPackageNames()
		println(packageNames)

		then: "it must contain the dummies package"
		packageNames.contains("io.gitlab.arturbosch.jpal.dummies")

		when: "retrieving all compilation info"
		def info = storage.allCompilationInfo

		then: "its size must be greater than 1 as more than 2 dummies are known)"
		info.size() > 1

		when: "retrieving a specific type (cycle)"
		def cycleInfo = storage.getCompilationInfo(cycleType).get()
		def cycleInfoFromPath = storage.getCompilationInfo(Helper.CYCLE_DUMMY).get()

		then: "it should have 2 inner classes"
		cycleInfo.innerClasses.size() == 2
		cycleInfoFromPath.innerClasses.size() == 2

		when: "retrieving info for a inner class"
		def infoFromInnerClass = storage.getCompilationInfo(innerCycleType).get()

		then: "it should return info of outer class"
		infoFromInnerClass.qualifiedType == cycleType

		when: "getting all qualified types which are already stored"
		def types = storage.getAllQualifiedTypes()

		then: "it must be greater or equals the amount of classes in dummies package (now 3)"
		types.size() >= 3
	}

	def "compilation storage with processor test"() {
		given: "compilation storage with processor"
		def key = new DataKey<String>() {}
		def storage = JPAL.newInstance(Helper.BASE_PATH, new CompilationInfoProcessor() {
			@Override
			void setup(CompilationInfo info, Resolver resolver) {
			}

			@Override
			void process(CompilationInfo info, Resolver resolver) {
				info.setData(key, "nice")
			}

			@Override
			void cleanup(CompilationInfo info, Resolver resolver) {
			}
		})

		when: "retrieving all compilation info"
		def instances = storage.allCompilationInfo

		then: "every instance should have the string 'nice'"
		instances.stream().allMatch { it.getData(key) == "nice" }
	}

}
