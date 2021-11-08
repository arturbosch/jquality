package io.gitlab.arturbosch.jpal.ast

import com.github.javaparser.ast.body.FieldDeclaration
import com.github.javaparser.ast.body.Parameter
import com.github.javaparser.ast.expr.VariableDeclarationExpr
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.ast.custom.JpalVariable

import java.util.stream.Collectors

/**
 * Provides static methods to convert fields, parameters and locale variables from javaparser
 * to jpal representation of variables.
 *
 * @author artur
 */
@CompileStatic
class VariableHelper {

	/**
	 * From locale variable to jpal variables as given expression can have many variables.
	 */
	static Set<JpalVariable> toJpalFromLocale(VariableDeclarationExpr variables) {
		return variables.variables.stream().map {

			def begin = it.getBegin()
			def end = it.getEnd()
			new JpalVariable(
					begin.map { it.line }.orElse(-1),
					begin.map { it.column }.orElse(-1),
					end.map { it.line }.orElse(-1),
					end.map { it.column }.orElse(-1),
					variables.modifiers,
					variables.annotations,
					it.type,
					it.nameAsString,
					it.initializer,
					JpalVariable.Nature.Local)
		}.collect(Collectors.toSet())
	}

	/**
	 * From locale variables to jpal variables.
	 */
	static Set<JpalVariable> toJpalFromLocales(List<VariableDeclarationExpr> declarations) {
		return declarations.stream().map { toJpalFromLocale(it) }
				.flatMap { it.stream() }.collect(Collectors.toSet())
	}

	/**
	 * From field declaration to jpal variables as given declaration can have many variables.
	 */
	static Set<JpalVariable> toJpalFromField(FieldDeclaration field) {
		return field.variables.stream().map {
			def begin = it.getBegin()
			def end = it.getEnd()
			new JpalVariable(
					begin.map { it.line }.orElse(-1),
					begin.map { it.column }.orElse(-1),
					end.map { it.line }.orElse(-1),
					end.map { it.column }.orElse(-1),
					field.modifiers,
					field.annotations,
					it.type,
					it.nameAsString,
					it.initializer,
					JpalVariable.Nature.Field)
		}.collect(Collectors.toSet())
	}

	/**
	 * From field declarations to jpal variables.s
	 */
	static Set<JpalVariable> toJpalFromFields(List<FieldDeclaration> declarations) {
		return declarations.stream().map { toJpalFromField(it) }
				.flatMap { it.stream() }.collect(Collectors.toSet())
	}

	/**
	 * From parameter to jpal variable.
	 */
	static JpalVariable toJpalFromParameter(Parameter parameter) {
		def begin = parameter.getBegin()
		def end = parameter.getEnd()
		new JpalVariable(
				begin.map { it.line }.orElse(-1),
				begin.map { it.column }.orElse(-1),
				end.map { it.line }.orElse(-1),
				end.map { it.column }.orElse(-1),
				parameter.modifiers,
				parameter.annotations,
				parameter.type,
				parameter.nameAsString,
				Optional.empty(),
				JpalVariable.Nature.Parameter)
	}

	/**
	 * From parameters to jpal variables.
	 */
	static Set<JpalVariable> toJpalFromParameters(List<Parameter> parameters) {
		return parameters.stream().map { toJpalFromParameter(it) }.collect(Collectors.toSet())
	}

}
