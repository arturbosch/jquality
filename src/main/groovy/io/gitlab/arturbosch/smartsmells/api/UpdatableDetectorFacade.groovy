package io.gitlab.arturbosch.smartsmells.api

import com.github.javaparser.utils.Pair
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.core.CompilationInfo
import io.gitlab.arturbosch.jpal.core.JPAL
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

	private final List<CompilationInfo> infos = new ArrayList<>()

	UpdatableDetectorFacade(Path root = null, DetectorFacade detectorFacade) {
		facade = detectorFacade
		storage = root ? JPAL.initializedUpdatable(root, null, facade.filters) : JPAL.updatable(null, facade.filters)
		resolver = new Resolver(storage)
	}

	void addOrUpdate(List<Path> pathsToUpdate) {
		infos.addAll(storage.updateCompilationInfo(pathsToUpdate))
	}

	void addOrUpdate(Map<Path, String> pathsToUpdate) {
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

	SmellResult run() {
		def result = facade.justRun(infos, resolver)
		facade.reset()
		infos.clear()
		return result
	}

}
