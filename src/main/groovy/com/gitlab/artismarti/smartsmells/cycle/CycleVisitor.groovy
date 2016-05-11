package com.gitlab.artismarti.smartsmells.cycle

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.FieldDeclaration
import com.github.javaparser.ast.type.ClassOrInterfaceType
import com.gitlab.artismarti.smartsmells.common.helper.BadSmellHelper
import com.gitlab.artismarti.smartsmells.common.CompilationTree
import com.gitlab.artismarti.smartsmells.common.helper.NodeHelper
import com.gitlab.artismarti.smartsmells.common.helper.PackageImportHelper
import com.gitlab.artismarti.smartsmells.common.PackageImportHolder
import com.gitlab.artismarti.smartsmells.common.QualifiedType
import com.gitlab.artismarti.smartsmells.common.Visitor
import com.gitlab.artismarti.smartsmells.common.source.SourcePath

import java.nio.file.Path

/**
 * @author artur
 */
class CycleVisitor extends Visitor<Cycle> {

	private PackageImportHolder packageImportHolder
	private InnerClassesHandler innerClassesHandler

	CycleVisitor(Path path) {
		super(path)
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
		def thisClassType = PackageImportHelper.getQualifiedType(
				packageImportHolder, new ClassOrInterfaceType(unqualifiedName))

		def fields = NodeHelper.findFields(n)

		fields.each {

			def qualifiedType = PackageImportHelper.getQualifiedType(
					packageImportHolder, it.type)

			if (qualifiedType.isReference()) {

				def unqualifiedFieldName = innerClassesHandler.getUnqualifiedNameForInnerClass(it.type)
				qualifiedType = PackageImportHelper.getQualifiedType(
						packageImportHolder, new ClassOrInterfaceType(unqualifiedFieldName))
				def maybeType = CompilationTree.findReferencedType(qualifiedType)

				if (maybeType.isPresent()) {
					searchForCycles(maybeType.get(), thisClassType, it)
				}
			}
		}

		super.visit(n, arg)
	}

	def searchForCycles(Path path, QualifiedType thisClass, FieldDeclaration field) {

		CompilationUnit unit = CompilationTree.getCompilationUnit(path)
		def visitor = new SameFieldTypeVisitor(thisClass)
		visitor.visit(unit, null)

		if (visitor.haveFound()) {
			addCycle(visitor, thisClass, field, path)
		}
	}

	private void addCycle(SameFieldTypeVisitor visitor, QualifiedType thisClass, FieldDeclaration field, Path path) {
		def tuple = visitor.foundFieldWithType
		def otherType = tuple.first
		def otherField = tuple.second

		def dep1 = new Dependency(thisClass.shortName(), thisClass.name,
				SourcePath.of(this.path), BadSmellHelper.createSourceRangeFromNode(field))
		def dep2 = new Dependency(otherType.shortName(), otherType.name,
				SourcePath.of(path), BadSmellHelper.createSourceRangeFromNode(otherField))
		def cycle = new Cycle(dep1, dep2)

		if (!smells.contains(cycle)) {
			smells.add(cycle)
		}
	}

}
