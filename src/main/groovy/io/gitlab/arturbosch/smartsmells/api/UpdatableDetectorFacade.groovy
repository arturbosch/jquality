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

	UpdatableDetectorFacade(Path root, DetectorFacade detectorFacade) {
		facade = detectorFacade
		storage = JPAL.updatableFromSource(root)
		resolver = new Resolver(storage)
	}

	List<CompilationInfo> addOrUpdate(List<Path> pathsToUpdate) {
		storage.updateCompilationInfo(pathsToUpdate)
	}

	List<CompilationInfo> addOrUpdate(Map<Path, String> pathsToUpdate) {
		storage.updateCompilationInfo(pathsToUpdate)
	}

	List<CompilationInfo> relocate(Map<Path, Path> pathsToRelocate) {
		def infos = new ArrayList<CompilationInfo>()
		pathsToRelocate.each {
			storage.relocateCompilationInfo(it.key, it.value).ifPresent {
				infos.add(it)
			}
		}
		return infos
	}

	List<CompilationInfo> relocateWithContent(Map<Path, Pair<Path, String>> pathsToRelocate) {
		def infos = new ArrayList<CompilationInfo>()
		pathsToRelocate.each {
			storage.relocateCompilationInfo(it.key, it.value).ifPresent {
				infos.add(it)
			}
		}
		return infos
	}

	void remove(List<Path> pathsToRemove) {
		storage.removeCompilationInfo(pathsToRemove)
	}

	SmellResult run(List<CompilationInfo> infos) {
		facade.justRun(infos, resolver)
	}

}
