package io.gitlab.arturbosch.smartsmells.metrics

import com.github.javaparser.ast.body.FieldDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.expr.FieldAccessExpr
import com.github.javaparser.ast.expr.MethodCallExpr
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.core.CompilationInfo
import io.gitlab.arturbosch.jpal.resolution.QualifiedType
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.jpal.resolution.symbols.FieldSymbolReference
import io.gitlab.arturbosch.jpal.resolution.symbols.MethodSymbolReference
import io.gitlab.arturbosch.smartsmells.common.visitor.InternalVisitor

import java.lang.reflect.Modifier
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.function.Supplier
import java.util.stream.Collectors

/**
 * @author Artur Bosch
 */
@CompileStatic
class TypeReferenceStorage {

	private Resolver resolver

	TypeReferenceStorage(Resolver resolver) {
		this.resolver = resolver
	}

	class References {
		List<MethodDeclaration> methods
		List<FieldDeclaration> fields
		List<MethodSymbolReference> methodReferences
		List<FieldSymbolReference> fieldReferences
		List<QualifiedType> usedTypes
		Set<QualifiedType> innerTypes

		References(List<MethodDeclaration> methods, List<FieldDeclaration> fields, List<MethodSymbolReference> methodReferences, List<FieldSymbolReference> fieldReferences, List<QualifiedType> usedTypes, Set<QualifiedType> innerTypes) {
			this.methods = methods
			this.fields = fields
			this.methodReferences = methodReferences
			this.fieldReferences = fieldReferences
			this.usedTypes = usedTypes
			this.innerTypes = innerTypes
		}
	}

	final ConcurrentMap<QualifiedType, References> cache = new ConcurrentHashMap<>()

	void create(List<CompilationInfo> cus) {
		def pool = Executors.newFixedThreadPool(Runtime.runtime.availableProcessors())

		allOf(cus.collect { info ->
			async(pool) {
				createReferences(info)
			}.thenAccept {
				cache.put(info.qualifiedType, it)
			}
		}).get()

		pool.shutdown()
	}

	private References createReferences(CompilationInfo info) {
		def collector = new ReferenceCollector()
		collector.initialize(info)
		collector.visit(info.unit, resolver)
		return collector.create()
	}

	class ReferenceCollector extends InternalVisitor {
		List<MethodDeclaration> methods = new ArrayList<>()
		List<FieldDeclaration> fields = new ArrayList<>()
		List<MethodSymbolReference> methodReferences = new ArrayList<>()
		List<FieldSymbolReference> fieldReferences = new ArrayList<>()

		References create() {
			return new References(methods, fields, methodReferences, fieldReferences,
					info.usedTypes, info.innerClasses.keySet())
		}

		@Override
		void visit(FieldDeclaration n, Resolver arg) {
			if (n.modifiers.contains(Modifier.PUBLIC)) fields.add(n)
			super.visit(n, arg)
		}

		@Override
		void visit(MethodDeclaration n, Resolver arg) {
			if (n.modifiers.contains(Modifier.PUBLIC)) methods.add(n)
			super.visit(n, arg)
		}

		@Override
		void visit(MethodCallExpr n, Resolver arg) {
			arg.resolve(n.name, info).ifPresent {
				methodReferences.add(it as MethodSymbolReference)
			}
			super.visit(n, arg)
		}

		@Override
		void visit(FieldAccessExpr n, Resolver arg) {
			arg.resolve(n.name, info).ifPresent {
				println(n.toString())
				fieldReferences.add(it as FieldSymbolReference)
			}
			super.visit(n, arg)
		}
	}

	static <T> CompletableFuture<List<T>> allOf(List<CompletableFuture<T>> futures) {
		return CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]))
				.thenApply {
			futures.stream().map { it.join() }.collect(Collectors.toList())
		}
	}

	static <T> CompletableFuture<T> async(Executor executor, Supplier<T> block) {
		CompletableFuture.supplyAsync(block, executor)
	}
}
