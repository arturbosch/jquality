package io.gitlab.arturbosch.smartsmells.smells.cycle

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.FieldDeclaration
import com.github.javaparser.ast.type.ClassOrInterfaceType
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.ast.ClassHelper
import io.gitlab.arturbosch.jpal.core.CompilationInfo
import io.gitlab.arturbosch.jpal.resolution.QualifiedType
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.jpal.resolution.nested.InnerClassesHandler

/**
 * @author Artur Bosch
 */
@CompileStatic
class SameFieldTypeVisitor extends VoidVisitorAdapter<Resolver> {

	private QualifiedType searchedType
	private CompilationInfo info
	private InnerClassesHandler innerClassesHandler

	private boolean found
	private Tuple2<QualifiedType, FieldDeclaration> foundFieldWithType
	private QualifiedType currentClass

	SameFieldTypeVisitor(QualifiedType searchedType) {
		this.searchedType = searchedType
	}

	void visit(CompilationInfo info, Resolver resolver) {
		this.info = info
		innerClassesHandler = info.data.innerClassesHandler
		super.visit(info.unit, resolver)
	}

	@Override
	void visit(ClassOrInterfaceDeclaration n, Resolver arg) {
		String unqualifiedName = ClassHelper.appendOuterClassIfInnerClass(n)
		currentClass = arg.resolveType(new ClassOrInterfaceType(unqualifiedName), info)
		if (currentClass.name == searchedType.name) {
			// Note: Singletons are no cycles
			found = false
		} else {
			super.visit(n, arg)
		}

	}

	@Override
	void visit(FieldDeclaration n, Resolver arg) {
		def commonType = CycleVisitor.getCommonType(n)
		if (commonType) {
			def unqualifiedFieldName = innerClassesHandler.getUnqualifiedNameForInnerClass(commonType)
			def qualifiedType = arg.resolveType(new ClassOrInterfaceType(unqualifiedFieldName), info)

			if (qualifiedType.isReference()) {
				if (qualifiedType.name == searchedType.name) {
					foundFieldWithType = new Tuple2<>(currentClass, n)
					found |= true
				}
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
