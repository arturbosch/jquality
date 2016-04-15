package com.gitlab.artismarti.smartsmells.godclass

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.ConstructorDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.gitlab.artismarti.smartsmells.common.MethodHelper
import com.gitlab.artismarti.smartsmells.common.Visitor

import java.nio.file.Path

/**
 * GodClasses := ((ATFD, TopValues(20%)) ∧ (ATFD, HigherThan(4))) ∧
 * ((WMC, HigherThan(20)) ∨ (TCC, LowerThan(0.33)).
 *
 * @author artur
 */
class GodClassVisitor extends Visitor<GodClass> {

	GodClassVisitor(Path path) {
		super(path)
	}

	@Override
	void visit(ClassOrInterfaceDeclaration n, Object arg) {
		super.visit(n, arg)
	}

	@Override
	void visit(ConstructorDeclaration n, Object arg) {
		int mcc = MethodHelper.calcMcCabe(n)
	}

	@Override
	void visit(MethodDeclaration n, Object arg) {
		int mcc = MethodHelper.calcMcCabe(n)
	}
}

