package io.gitlab.arturbosch.smartsmells.common.helper

import com.github.javaparser.ast.body.FieldDeclaration
import com.github.javaparser.ast.body.Parameter
import com.github.javaparser.ast.expr.NameExpr
import com.github.javaparser.ast.expr.VariableDeclarationExpr
import io.gitlab.arturbosch.smartsmells.common.CustomVariableDeclaration

import java.util.stream.Collectors

/**
 * @author artur
 */
class VariableHelper {

	static Set<CustomVariableDeclaration> toCustomVariableDeclarations(Set<VariableDeclarationExpr> declarations) {
		return declarations.stream().map {
			declaration ->
				declaration.vars.collect {
					new CustomVariableDeclaration(
							it.beginLine,
							it.beginColumn,
							it.endLine,
							it.endColumn,
							declaration.modifiers,
							declaration.annotations,
							declaration.type,
							it.id,
							it.init,
							CustomVariableDeclaration.Nature.Local)
				}
		}.flatMap { it.stream() }.collect(Collectors.toSet())
	}

	static Set<CustomVariableDeclaration> fromFieldToCustomVariableDeclarations(List<FieldDeclaration> declarations) {
		return declarations.stream().map {
			decl ->
				decl.variables.collect {
					new CustomVariableDeclaration(
							it.beginLine,
							it.beginColumn,
							it.endLine,
							it.endColumn,
							decl.modifiers,
							decl.annotations,
							decl.type,
							it.id,
							it.init,
							CustomVariableDeclaration.Nature.Field)
				}
		}.flatMap { it.stream() }.collect(Collectors.toSet())
	}

	static Set<CustomVariableDeclaration> toCustomVariableDeclarations(List<Parameter> parameters) {
		return parameters.collect {
			new CustomVariableDeclaration(
					it.beginLine,
					it.beginColumn,
					it.endLine,
					it.endColumn,
					it.modifiers,
					it.annotations,
					it.type,
					it.id,
					new NameExpr(it.id.name),
					CustomVariableDeclaration.Nature.Parameter)
		}
	}
}
