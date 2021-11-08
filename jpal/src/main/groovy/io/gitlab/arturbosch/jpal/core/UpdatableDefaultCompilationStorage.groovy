package io.gitlab.arturbosch.jpal.core

import com.github.javaparser.utils.Pair
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import io.gitlab.arturbosch.jpal.internal.Validate

import java.nio.file.Path
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.regex.Pattern
import java.util.stream.Collectors

/**
 * @author Artur Bosch
 */
@CompileStatic
class UpdatableDefaultCompilationStorage extends DefaultCompilationStorage implements UpdatableCompilationStorage {

	@PackageScope
	UpdatableDefaultCompilationStorage(CompilationInfoProcessor processor = null,
									   List<Pattern> pathFilters = new ArrayList<>(),
									   JavaCompilationParser parser = null,
									   ExecutorService executor = null) {
		super(processor, pathFilters, parser, executor)
	}

	@Override
	List<CompilationInfo> relocateCompilationInfo(Map<Path, Path> relocates) {
		Validate.notNull(relocates)

		def futures = relocates.collect { oldPath, newPath ->
			CompletableFuture.supplyAsync({
				getCompilationInfo(oldPath).ifPresent {
					pathCache.remove(oldPath)
					typeCache.remove(it.qualifiedType)
				}
				createCompilationInfo(newPath)
			}, executor)
		}

		return awaitAll(futures)
	}

	@Override
	List<CompilationInfo> updateCompilationInfo(List<Path> paths) {
		def futures = paths.collect { path ->
			CompletableFuture.supplyAsync({
				Validate.notNull(path)
				createCompilationInfo(path)
			}, executor)
		}
		return awaitAll(futures)
	}

	@Override
	void removeCompilationInfo(List<Path> paths) {
		paths.each { path ->
			getCompilationInfo(path).ifPresent { CompilationInfo info ->
				pathCache.remove(path)
				typeCache.remove(info.qualifiedType)
				info.innerClasses.keySet().each {
					typeCache.remove(it)
				}
				removePackageName(info.qualifiedType.onlyPackageName)
			}
		}
	}

	@Override
	List<CompilationInfo> relocateCompilationInfoFromSource(Map<Path, Pair<Path, String>> relocates) {
		Validate.notNull(relocates)

		def futures = relocates.collect { oldPath, newContent ->
			CompletableFuture.supplyAsync({
				getCompilationInfo(oldPath).ifPresent {
					pathCache.remove(oldPath)
					typeCache.remove(it.qualifiedType)
				}
				createCompilationInfo(newContent.a, newContent.b)
			}, executor)
		}

		return awaitAll(futures)
	}

	@Override
	List<CompilationInfo> updateCompilationInfo(Map<Path, String> pathWithContent) {
		Validate.notNull(pathWithContent)

		def futures = pathWithContent.collect { path, content ->
			CompletableFuture.supplyAsync({
				createCompilationInfo(path, content)
			}, executor)
		}
		return awaitAll(futures)
	}

	private List<CompilationInfo> awaitAll(List<CompletableFuture<CompilationInfo>> futures) {
		CompletableFuture.allOf(futures.toArray(new CompletableFuture<?>[0])).join()
		List<CompilationInfo> infos = futures.stream()
				.map { it.get() }
				.filter { it != null }
				.collect(Collectors.toList())

		def arrayOfFutures = infos.collect { CompilationInfo info ->
			CompletableFuture.runAsync({ info.findUsedTypes(typeSolver) }, executor)
		}.toArray(new CompletableFuture<?>[0])
		CompletableFuture.allOf(arrayOfFutures).join()

		runProcessor(infos)
		return Collections.unmodifiableList(infos)
	}

}
