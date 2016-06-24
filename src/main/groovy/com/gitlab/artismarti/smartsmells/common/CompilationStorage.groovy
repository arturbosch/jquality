package com.gitlab.artismarti.smartsmells.common

import com.github.javaparser.ASTHelper
import com.github.javaparser.JavaParser
import com.github.javaparser.ParseException
import com.github.javaparser.TokenMgrError
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.gitlab.artismarti.smartsmells.common.helper.TypeHelper
import com.gitlab.artismarti.smartsmells.util.StreamCloser
import com.gitlab.artismarti.smartsmells.util.Validate
import org.codehaus.groovy.runtime.IOGroovyMethods

import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ForkJoinPool
import java.util.function.Supplier
import java.util.logging.Logger
import java.util.stream.Stream

/**
 * @author artur
 */
final class CompilationStorage {

	private static CompilationStorage storage;

	private final static Logger LOGGER = Logger.getLogger(CompilationStorage.simpleName)

	private final Path root
	private final SmartCache<QualifiedType, CompilationInfo> typeCache = new SmartCache<>()
	private final SmartCache<Path, CompilationInfo> pathCache = new SmartCache<>()

	private CompilationStorage(Path path) { root = path }

	static CompilationStorage create(Path root) {
		Validate.isTrue(root != null, "Project path must be not null!")
		storage = new CompilationStorage(root)
		storage.createInternal()
		return storage
	}

	static Optional<CompilationUnit> getCompilationUnit(Path path) {
		Validate.notNull(path)
		return getCUInternal { storage.pathCache.get(path) }
	}

	private static Optional<CompilationUnit> getCUInternal(Supplier<Optional<CompilationInfo>> cu) {
		Validate.notNull(storage)
		def info = cu.get()
		return info.isPresent() ? info.map { it.unit } : Optional.empty()
	}

	static Optional<CompilationUnit> getCompilationUnit(QualifiedType qualifiedType) {
		Validate.notNull(qualifiedType)
		return getCUInternal { storage.typeCache.get(qualifiedType) }
	}

	private void createInternal() {

		def forkJoinPool = new ForkJoinPool(
				Runtime.getRuntime().availableProcessors(),
				ForkJoinPool.defaultForkJoinWorkerThreadFactory, null, true)

		List<CompletableFuture> futures = new ArrayList<>(1000)

		def walker = getJavaFilteredFileStream()
		walker.forEach { path ->
			futures.add(CompletableFuture
					.supplyAsync({ compileFor(path) }, forkJoinPool)
					.exceptionally { logCompilationFailure(path) })
		}

		CompletableFuture.allOf(futures.toArray(new CompletableFuture<?>[0])).join()
		forkJoinPool.shutdown()
		StreamCloser.quietly(walker)

	}

	private void compileFor(Path path) {
		IOGroovyMethods.withCloseable(Files.newInputStream(path)) {
			try {
				def unit = JavaParser.parse(it)
				def type = TypeHelper.getQualifiedType(getFirstDeclaredClass(unit), unit.package)
				def compilationInfo = CompilationInfo.of(type, unit, path)
				typeCache.put(type, compilationInfo)
				pathCache.put(path, compilationInfo)
			} catch (ParseException | TokenMgrError ignored) {
			}
		}
	}

	private static ClassOrInterfaceDeclaration getFirstDeclaredClass(CompilationUnit compilationUnit) {
		ASTHelper.getNodesByType(compilationUnit, ClassOrInterfaceDeclaration).first()
	}

	private static logCompilationFailure(Path path) {
		LOGGER.warning("Could not create compilation unit from: $path due to syntax errors.")
	}

	private Stream<Path> getJavaFilteredFileStream() {
		Files.walk(root).parallel().filter { it.toString().endsWith(".java") }
				.filter { !it.toString().endsWith("package-info.java") }
	}

	Set<QualifiedType> getAllQualifiedTypes() {
		typeCache.internalCache.keySet()
	}

	List<CompilationInfo> getAllCompilationInfo() {
		typeCache.internalCache.values().toList()
	}
}
