package io.gitlab.arturbosch.jpal.core

import com.github.javaparser.utils.Pair

import java.nio.file.Path

/**
 * @author Artur Bosch
 */
interface UpdatableCompilationStorage extends CompilationStorage {

	/**
	 * Allows to update an existing compilation info for which the underlying path was relocated.
	 * This method will delete the old compilation info from both caches and compile a new one.
	 *
	 * @param relocates map of path pairs to relocate
	 * @return maybe the new relocated info if no compilation errors occur
	 */
	List<CompilationInfo> relocateCompilationInfo(Map<Path, Path> relocates)

	/**
	 * Updates all given paths by recompiling the underlying compilation units.
	 *
	 * @param paths list of paths to update
	 * @return a list with all updated info, may be empty if all compilations fail
	 */
	List<CompilationInfo> updateCompilationInfo(List<Path> paths)

	/**
	 * Removes all given paths from the compilation storage caches.
	 *
	 * @param paths a list of paths to remove
	 */
	void removeCompilationInfo(List<Path> paths)

	/**
	 * Allows to update an existing compilation info for which the underlying path was relocated.
	 * This method will delete the old compilation info from both caches and compile a new one.
	 *
	 * @param relocates map of path pairs (with relocated source code) to relocate
	 * @return maybe the new relocated info if no compilation errors occur
	 */
	List<CompilationInfo> relocateCompilationInfoFromSource(Map<Path, Pair<Path, String>> relocates)

	/**
	 * Updates all given paths by recompiling the underlying compilation units.
	 *
	 * @param paths list of paths to update
	 * @return a list with all updated info, may be empty if all compilations fail
	 */
	List<CompilationInfo> updateCompilationInfo(Map<Path, String> pathWithContent)

}