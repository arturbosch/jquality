package io.gitlab.arturbosch.jpal.core

import io.gitlab.arturbosch.jpal.resolution.QualifiedType

import java.nio.file.Path

/**
 * @author Artur Bosch
 */
interface CompilationStorage {

	/**
	 * Initializes this compilation storage with java files found inside the given root package.
	 *
	 * @param root the top directory to start compiling java files
	 * @return this storage
	 */
	CompilationStorage initialize(Path root)

	/**
	 * @return retrieves all stored qualified type keys
	 */
	Set<QualifiedType> getAllQualifiedTypes()

	/**
	 * @return retrieves all stores compilation info's
	 */
	Collection<CompilationInfo> getAllCompilationInfo()

	/**
	 * Maybe a compilation unit for given path is found.
	 *
	 * @param path path for which the info is asked
	 * @return optional of compilation unit
	 */
	Optional<CompilationInfo> getCompilationInfo(Path path)

	/**
	 * Maybe a compilation unit for given qualified type is found.
	 *
	 * @param qualifiedType type for which the info is asked
	 * @return optional of compilation unit
	 */
	Optional<CompilationInfo> getCompilationInfo(QualifiedType qualifiedType)

	/**
	 * A set of package names, describing the package structure.
	 * Run contains checks on it if you need to make sure the analyzed file
	 * really from the analyzed project.
	 *
	 * @return an unmodifiable set of package names
	 */
	Set<String> getStoredPackageNames()
}
