package io.gitlab.arturbosch.smartsmells.common

import com.github.javaparser.ASTHelper
import com.github.javaparser.JavaParser
import com.github.javaparser.ParseException
import com.github.javaparser.TokenMgrError
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import io.gitlab.arturbosch.smartsmells.common.helper.TypeHelper
import io.gitlab.arturbosch.smartsmells.util.StreamCloser
import io.gitlab.arturbosch.smartsmells.util.Validate
import org.codehaus.groovy.runtime.IOGroovyMethods

import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ForkJoinPool
import java.util.logging.Logger
import java.util.stream.Stream

/**
 * @author artur
 */
final class CompilationStorage {

	private static CompilationStorage storage;

	private static CompilationStorage getInstance() {
		Validate.notNull(storage, "Compilation storage not yet initialized!")
		return storage;
	}

	private final static Logger LOGGER = Logger.getLogger(CompilationStorage.simpleName)

	private final Path root
	private final SmartCache<QualifiedType, CompilationInfo> typeCache = new SmartCache<>()
	private final SmartCache<Path, CompilationInfo> pathCache = new SmartCache<>()

	private CompilationStorage(Path path) { root = path }

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
		ASTHelper.getNodesByType(compilationUnit, ClassOrInterfaceDeclaration.class).first()
	}

	private static logCompilationFailure(Path path) {
		LOGGER.warning("Could not create compilation unit from: $path due to syntax errors.")
	}

	private Stream<Path> getJavaFilteredFileStream() {
		Files.walk(root).parallel().filter { it.toString().endsWith(".java") }
				.filter { !it.toString().endsWith("package-info.java") }
	}

	static CompilationStorage create(Path root) {
		Validate.isTrue(root != null, "Project path must be not null!")
		storage = new CompilationStorage(root)
		storage.createInternal()
		return storage
	}

	static Set<QualifiedType> getAllQualifiedTypes() {
		instance.typeCache.internalCache.keySet()
	}

	static List<CompilationInfo> getAllCompilationInfo() {
		instance.typeCache.internalCache.values().toList()
	}

	static Optional<CompilationInfo> getCompilationUnit(Path path) {
		Validate.notNull(path)
		return instance.pathCache.get(path)
	}

	static Optional<CompilationInfo> getCompilationInfo(QualifiedType qualifiedType) {
		Validate.notNull(qualifiedType)
		return instance.typeCache.get(qualifiedType.asOuterClass())
	}

	static boolean isInitialized() {
		return storage != null
	}
}
