package io.gitlab.arturbosch.jpal.core

import io.gitlab.arturbosch.jpal.internal.Validate

import java.nio.file.Path
import java.util.concurrent.ExecutorService
import java.util.regex.Pattern

/**
 * Key class to create a compilation storage.
 *
 * @author Artur Bosch
 */
final class JPAL {

	private JPAL() {}

	/**
	 * Initialize the compilation storage and compiles all sub paths of
	 * given root path. All created compilation info instances will
	 * execute the given processor if processor is provided.
	 *
	 * @param root project path
	 * @param processor compilation info processor, can be null
	 * @param pathFilters filters to use when compiling java files
	 * @param parser the parser to use for java files
	 * @param executorService the executor to use for compilation
	 * @return the storage
	 */
	static CompilationStorage newInstance(Path root,
										  CompilationInfoProcessor processor = null,
										  List<Pattern> pathFilters = Collections.emptyList(),
										  JavaCompilationParser parser = null,
										  ExecutorService executorService = null) {
		Validate.isTrue(root != null, "Project path must not be null!")
		return new DefaultCompilationStorage(processor, pathFilters, parser, executorService).initialize(root)
	}

	/**
	 * Creates a compilation storage without initializing or compiling any sources.
	 * An updatable storage can compile additional or replace existing classes whenever needed.
	 * Specified processor will also run on each additional compilation info.
	 *
	 * @param processor compilation info processor, can be null
	 * @param pathFilters filters to use when compiling java files
	 * @param parser the parser to use for java files
	 * @param executorService the executor to use for compilation
	 * @return the only reference to this compilation unit
	 */
	static UpdatableCompilationStorage updatable(CompilationInfoProcessor processor = null,
												 List<Pattern> pathFilters = Collections.emptyList(),
												 JavaCompilationParser parser = null,
												 ExecutorService executorService = null) {
		return new UpdatableDefaultCompilationStorage(processor, pathFilters, parser, executorService)
	}

	/**
	 * Creates a compilation storage and compiles any sources found down the root path.
	 * An updatable storage can compile additional or replace existing classes whenever needed.
	 * Specified processor will also run on each additional compilation info.
	 *
	 * @param root project root path
	 * @param processor compilation info processor, can be null
	 * @param pathFilters filters to use when compiling java files
	 * @param parser the parser to use for java files
	 * @param executorService the executor to use for compilation
	 * @return the only reference to this compilation unit
	 */
	static UpdatableCompilationStorage initializedUpdatable(Path root,
															CompilationInfoProcessor processor = null,
															List<Pattern> pathFilters = Collections.emptyList(),
															JavaCompilationParser parser = null,
															ExecutorService executorService = null) {
		Validate.isTrue(root != null, "Project path must not be null!")
		return new UpdatableDefaultCompilationStorage(processor, pathFilters, parser, executorService)
				.initialize(root) as UpdatableCompilationStorage
	}

	/**
	 * Creates a configuration builder for compilation storage's.
	 *
	 * @return builder for compilation storage's
	 */
	static CompilationStorageBuilder builder() {
		return new CompilationStorageBuilder()
	}

}
