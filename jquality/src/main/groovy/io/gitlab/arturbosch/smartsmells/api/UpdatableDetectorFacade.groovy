package io.gitlab.arturbosch.smartsmells.api

import com.github.javaparser.utils.Pair
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.core.CompilationInfo
import io.gitlab.arturbosch.jpal.core.UpdatableCompilationStorage
import io.gitlab.arturbosch.jpal.resolution.Resolver

import java.nio.file.Path

/**
 * @author Artur Bosch
 */
@CompileStatic
class UpdatableDetectorFacade {

	private final UpdatableCompilationStorage storage
	private final Resolver resolver
	private final DetectorFacade facade

	private final Set<CompilationInfo> infos = new HashSet<>()

	UpdatableDetectorFacade(DetectorFacade detectorFacade,
							UpdatableCompilationStorage storage) {
		facade = detectorFacade
		this.storage = storage
		resolver = new Resolver(storage)
	}

	void addOrUpdate(List<Path> pathsToUpdate) {
		infos.addAll(storage.updateCompilationInfo(pathsToUpdate))
	}

	void addOrUpdateWithContent(Map<Path, String> pathsToUpdate) {
		infos.addAll(storage.updateCompilationInfo(pathsToUpdate))
	}

	void relocate(Map<Path, Path> pathsToRelocate) {
		infos.addAll(storage.relocateCompilationInfo(pathsToRelocate))
	}

	void relocateWithContent(Map<Path, Pair<Path, String>> pathsToRelocate) {
		infos.addAll(storage.relocateCompilationInfoFromSource(pathsToRelocate))
	}

	void remove(List<Path> pathsToRemove) {
		storage.removeCompilationInfo(pathsToRemove)
	}

	SmellResult executeOnUpdated() {
		def result = facade.run(infos, resolver)
		facade.reset()
		infos.clear()
		return result
	}

}
