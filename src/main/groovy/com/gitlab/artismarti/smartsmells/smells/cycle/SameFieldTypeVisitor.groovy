package com.gitlab.artismarti.smartsmells.smells.cycle

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.FieldDeclaration
import com.github.javaparser.ast.type.ClassOrInterfaceType
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import com.gitlab.artismarti.smartsmells.common.helper.PackageImportHelper
import com.gitlab.artismarti.smartsmells.common.PackageImportHolder
import com.gitlab.artismarti.smartsmells.common.QualifiedType

/**
 * @author artur
 */
class SameFieldTypeVisitor extends VoidVisitorAdapter {

	private QualifiedType searchedType
	private PackageImportHolder packageImportHolder
	private InnerClassesHandler innerClassesHandler

	private boolean found
	private Tuple2<QualifiedType, FieldDeclaration> foundFieldWithType
	private QualifiedType currentClass

	SameFieldTypeVisitor(QualifiedType searchedType) {
		this.searchedType = searchedType
	}

	@Override
	void visit(CompilationUnit n, Object arg) {
		packageImportHolder = new PackageImportHolder(n.package, n.imports)
		innerClassesHandler = new InnerClassesHandler(n)
		super.visit(n, arg)
	}

	@Override
	void visit(ClassOrInterfaceDeclaration n, Object arg) {
		String unqualifiedName = innerClassesHandler.appendOuterClassIfInnerClass(n)
		currentClass = PackageImportHelper.getQualifiedType(
				packageImportHolder, new ClassOrInterfaceType(unqualifiedName))
		if (currentClass.name == searchedType.name) {
			// Note: Singletons are no cycles
			found = false
		} else {
			super.visit(n, arg)
		}

	}

	@Override
	void visit(FieldDeclaration n, Object arg) {
		def unqualifiedFieldName = innerClassesHandler.getUnqualifiedNameForInnerClass(n.type)
		def qualifiedType = PackageImportHelper.getQualifiedType(
				packageImportHolder, new ClassOrInterfaceType(unqualifiedFieldName))

		if (qualifiedType.isReference()) {
			if (qualifiedType.name.equals(searchedType.name)) {
				foundFieldWithType = new Tuple2<>(currentClass, n)
				found |= true
			}
		}
	}

	boolean haveFound() {
		return found
	}

	Tuple2<QualifiedType, FieldDeclaration> getFoundFieldWithType() {
		return foundFieldWithType
	}
}
