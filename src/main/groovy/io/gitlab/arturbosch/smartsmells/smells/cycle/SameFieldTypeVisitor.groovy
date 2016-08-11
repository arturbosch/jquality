package io.gitlab.arturbosch.smartsmells.smells.cycle

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.FieldDeclaration
import com.github.javaparser.ast.type.ClassOrInterfaceType
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import io.gitlab.arturbosch.jpal.ast.ClassHelper
import io.gitlab.arturbosch.jpal.nested.InnerClassesHandler
import io.gitlab.arturbosch.jpal.resolve.QualifiedType
import io.gitlab.arturbosch.jpal.resolve.ResolutionData
import io.gitlab.arturbosch.jpal.resolve.Resolver

/**
 * @author artur
 */
class SameFieldTypeVisitor extends VoidVisitorAdapter {

	private QualifiedType searchedType
	private ResolutionData packageImportHolder
	private InnerClassesHandler innerClassesHandler

	private boolean found
	private Tuple2<QualifiedType, FieldDeclaration> foundFieldWithType
	private QualifiedType currentClass

	SameFieldTypeVisitor(QualifiedType searchedType) {
		this.searchedType = searchedType
	}

	@Override
	void visit(CompilationUnit n, Object arg) {
		packageImportHolder = ResolutionData.of(n)
		innerClassesHandler = new InnerClassesHandler(n)
		super.visit(n, arg)
	}

	@Override
	void visit(ClassOrInterfaceDeclaration n, Object arg) {
		String unqualifiedName = ClassHelper.appendOuterClassIfInnerClass(n)
		currentClass = Resolver.getQualifiedType(
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
		def qualifiedType = Resolver.getQualifiedType(
				packageImportHolder, new ClassOrInterfaceType(unqualifiedFieldName))

		if (qualifiedType.isReference()) {
			if (qualifiedType.name == searchedType.name) {
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
