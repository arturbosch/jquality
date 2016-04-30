package com.gitlab.artismarti.smartsmells.cycle

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.FieldDeclaration
import com.github.javaparser.ast.type.ClassOrInterfaceType
import com.github.javaparser.ast.visitor.VoidVisitorAdapter

/**
 * @author artur
 */
class SameFieldTypeVisitor extends VoidVisitorAdapter {

	private QualifiedType searchedType
	private PackageImportHelper packageImportHelper

	private boolean found
	private Tuple2<QualifiedType, FieldDeclaration> foundFieldWithType
	private QualifiedType currentClass

	SameFieldTypeVisitor(QualifiedType searchedType) {
		this.searchedType = searchedType
	}

	@Override
	void visit(CompilationUnit n, Object arg) {
		packageImportHelper = new PackageImportHelper(n.package, n.imports)
		super.visit(n, arg)
	}

	@Override
	void visit(ClassOrInterfaceDeclaration n, Object arg) {
		currentClass = packageImportHelper.getQualifiedType(new ClassOrInterfaceType(n.name))
		super.visit(n, arg)
	}

	@Override
	void visit(FieldDeclaration n, Object arg) {
		def qualifiedType = packageImportHelper.getQualifiedType(n.type)
		if (qualifiedType.name.equals(searchedType.name)) {
			foundFieldWithType = new Tuple2<>(currentClass, n)
			found |= true
		}
	}

	boolean haveFound() {
		return found
	}

	Tuple2<QualifiedType, FieldDeclaration> getFoundFieldWithType() {
		return foundFieldWithType
	}
}
