package io.gitlab.arturbosch.jpal.core

import com.github.javaparser.utils.Pair
import io.gitlab.arturbosch.jpal.Helper
import spock.lang.Specification

import java.nio.file.Files
import java.util.regex.Pattern
import java.util.stream.Collectors

/**
 * @author Artur Bosch
 */
class UpdatableCompilationStorageTest extends Specification {

	def "domain update tests"() {
		given: "an updatable storage"
		def storage = JPAL.initializedUpdatable(Helper.BASE_PATH)

		when: "adding a new path to the compilation storage"
		def pathToAdd = Helper.BASE_PATH.resolve("test/TestReference.java")
		def updatedCU = storage.updateCompilationInfo([pathToAdd])[0]

		then: "a new compilation info is added"
		updatedCU.qualifiedType.shortName == "TestReference"

		when: "a file is relocated"
		def pathToRelocate = Helper.BASE_PATH.resolve("test/InnerClassesDummy.java")
		def relocatedCU = storage.relocateCompilationInfo([pathToAdd, pathToRelocate].toSpreadMap())[0]
		def removedCU = storage.getCompilationInfo(pathToAdd)

		then: "old path is absent and new present"
		relocatedCU.qualifiedType.shortName == "InnerClassesDummy"
		!removedCU.isPresent()

		when: "removing a path"
		storage.removeCompilationInfo([relocatedCU.path])

		then: "there is no info anymore for this path"
		!storage.getCompilationInfo(relocatedCU.path).isPresent()
	}

	def "update package names domain test"() {
		given: "an updatable storage"
		def storage = JPAL.initializedUpdatable(Helper.BASE_PATH)
		
		when: "testing the packages"
		def packageNames = storage.getStoredPackageNames()
		
		then: "is must contain all for used packages from base path"
		packageNames.contains("io.gitlab.arturbosch.jpal.dummies")
		packageNames.contains("io.gitlab.arturbosch.jpal.dummies.resolving")
		packageNames.contains("io.gitlab.arturbosch.jpal.dummies.test")
		packageNames.contains("io.gitlab.arturbosch.jpal.dummies.filtered")

		when: "removing the FilteredDummy"
		storage.removeCompilationInfo([Helper.FILTERED_DUMMY])

		then: "stored packages must be 3"
		packageNames.size() == 3

		when: "removing one dummy from resolving"
		storage.removeCompilationInfo([Helper.RESOLVING_DUMMY])

		then: "the package is still there"
		packageNames.contains("io.gitlab.arturbosch.jpal.dummies.resolving")
	}

	def "updatable from source code domain test"() {
		given: "an updatable storage"
		def storage = JPAL.updatable()

		when: "adding a new path/content pair to the compilation storage"
		def path = Helper.BASE_PATH.resolve("test/TestReference.java")
		def content = Files.lines(path)
				.collect(Collectors.joining("\n"))
		def info = storage.updateCompilationInfo([path, content].toSpreadMap())[0]

		then: "info has right path and class name"
		info.path == path
		info.mainType.nameAsString == "TestReference"

		when: "relocating path with new content"
		content = Files.lines(Helper.DUMMY).collect(Collectors.joining("\n"))
		def relocatedInfo = storage.relocateCompilationInfoFromSource(
				[path, new Pair(Helper.DUMMY, content)].toSpreadMap())[0]

		then: "relocated info's name and path has changed"
		relocatedInfo.path == Helper.DUMMY
		relocatedInfo.mainType.nameAsString == "Dummy"
	}

	def "filtered dummy is not in the storage"() {
		given: "an updatable storage with filters"
		def storage = JPAL.updatable(null, [Pattern.compile(".*/filtered/.*")])
		when: "updating storage with a filtered path"
		def infos = storage.updateCompilationInfo([Helper.FILTERED_DUMMY])
		then: "the path should not be compiled"
		infos.isEmpty()

	}

	def "updatable postprocessing of used types"() {
		expect:
		!info.usedTypes.isEmpty()
		where:
		info = JPAL.updatable().updateCompilationInfo([Helper.RESOLVING_DUMMY])[0]
	}
}
