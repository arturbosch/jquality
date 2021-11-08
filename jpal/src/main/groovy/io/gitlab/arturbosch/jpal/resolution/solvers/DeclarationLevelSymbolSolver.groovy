package io.gitlab.arturbosch.jpal.resolution.solvers

import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.body.TypeDeclaration
import com.github.javaparser.ast.body.VariableDeclarator
import com.github.javaparser.ast.expr.SimpleName
import com.github.javaparser.ast.expr.VariableDeclarationExpr
import com.github.javaparser.ast.nodeTypes.NodeWithType
import com.github.javaparser.ast.type.ClassOrInterfaceType
import com.github.javaparser.ast.type.Type
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.core.CompilationInfo
import io.gitlab.arturbosch.jpal.core.CompilationStorage
import io.gitlab.arturbosch.jpal.resolution.symbols.LocaleVariableSymbolReference
import io.gitlab.arturbosch.jpal.resolution.symbols.MethodSymbolReference
import io.gitlab.arturbosch.jpal.resolution.symbols.NodeWithTypeSymbolReference
import io.gitlab.arturbosch.jpal.resolution.symbols.SimpleSymbolReference
import io.gitlab.arturbosch.jpal.resolution.symbols.SymbolReference
import io.gitlab.arturbosch.jpal.resolution.symbols.TypeSymbolReference

/**
 * @author Artur Bosch
 */
@CompileStatic
final class DeclarationLevelSymbolSolver implements Solver {

	private TypeSolver resolver
	private CompilationStorage storage

	DeclarationLevelSymbolSolver(CompilationStorage storage, TypeSolver resolver) {
		this.storage = storage
		this.resolver = resolver
	}

	@Override
	Optional<? extends SymbolReference> resolve(SimpleName symbol, CompilationInfo info) {
		def parent = symbol.parentNode.orElse(null)
		if (parent && parent instanceof NodeWithType) {
			def withType = parent as NodeWithType
			def symbolReference = resolveVariableDeclaration(parent, symbol, info)
			return Optional.ofNullable(symbolReference.orElseGet {
				createNodeWithTypeReference(symbol, withType, info)
			})
		} else if (parent && parent instanceof TypeDeclaration) {
			def declaration = parent as TypeDeclaration
			def qualifiedType = resolver.getQualifiedType(info.data,
					new ClassOrInterfaceType(declaration.name.identifier))
			return Optional.of(new TypeSymbolReference(symbol, qualifiedType, declaration))
		} else if (parent && parent instanceof Type) { // javaparser 3.1.0 fixed that symbols are parents in types
			def qualifiedType = resolver.getQualifiedType(info.data, parent)
			if (qualifiedType.isFromJdk()) { // If its a java type we can't find the declaration
				return Optional.of(new SimpleSymbolReference(symbol, qualifiedType))
			}
			return storage.getCompilationInfo(qualifiedType)
					.map { it.innerClasses[qualifiedType] ?: it.mainType }
					.map { new TypeSymbolReference(symbol, qualifiedType, it as TypeDeclaration) }
		}
		return Optional.empty()
	}

	private Optional<LocaleVariableSymbolReference> resolveVariableDeclaration(NodeWithType parent,
																			   SimpleName symbol,
																			   CompilationInfo info) {
		if (parent instanceof VariableDeclarator) {
			return (parent as VariableDeclarator).parentNode
					.filter { it instanceof VariableDeclarationExpr }
					.map { (it as VariableDeclarationExpr) }
					.map {
				def qualifiedType = resolver.getQualifiedType(info.data, it.commonType)
				new LocaleVariableSymbolReference(symbol, qualifiedType, it)
			}
		}
		return Optional.empty()
	}

	private SymbolReference createNodeWithTypeReference(SimpleName symbol, NodeWithType withType, CompilationInfo info) {
		def qualifiedType = resolver.getQualifiedType(info.data, withType.type)
		if (withType instanceof MethodDeclaration) {
			new MethodSymbolReference(symbol, qualifiedType, withType as MethodDeclaration)
		} else {
			new NodeWithTypeSymbolReference(symbol, qualifiedType, withType)
		}
	}

}
