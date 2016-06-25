package com.gitlab.artismarti.smartsmells.smells.cycle

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.FieldDeclaration
import com.github.javaparser.ast.type.ClassOrInterfaceType
import com.gitlab.artismarti.smartsmells.common.*
import com.gitlab.artismarti.smartsmells.common.helper.BadSmellHelper
import com.gitlab.artismarti.smartsmells.common.helper.NodeHelper
import com.gitlab.artismarti.smartsmells.common.helper.PackageImportHelper
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

		fields.each { field ->

			def qualifiedType = PackageImportHelper.getQualifiedType(
					packageImportHolder, field.type)

			if (qualifiedType.isReference()) {

				def unqualifiedFieldName = innerClassesHandler.getUnqualifiedNameForInnerClass(field.type)
				qualifiedType = PackageImportHelper.getQualifiedType(
						packageImportHolder, new ClassOrInterfaceType(unqualifiedFieldName))

				searchForCycles(qualifiedType, thisClassType, field)

			}
		}

		super.visit(n, arg)
	}

	def searchForCycles(QualifiedType otherType, QualifiedType thisType, FieldDeclaration field) {

		if (CompilationStorage.isInitialized()) {

			CompilationStorage.getCompilationInfo(otherType).ifPresent {
				def visitor = new SameFieldTypeVisitor(thisType)
				visitor.visit(it.unit, null)

				if (visitor.haveFound()) {
					addCycle(visitor, thisType, field, it.path)
				}
			}

		} else {

			def maybePath = CompilationTree.findReferencedType(otherType)
			if (maybePath.isPresent()) {
				def otherPath = maybePath.get()
				CompilationTree.getCompilationUnit(otherPath).ifPresent {
					def visitor = new SameFieldTypeVisitor(thisType)
					visitor.visit(it, null)

					if (visitor.haveFound()) {
						addCycle(visitor, thisType, field, otherPath)
					}
				}
			}

		}
	}

	private void addCycle(SameFieldTypeVisitor visitor, QualifiedType thisClass, FieldDeclaration field, Path otherPath) {
		def tuple = visitor.foundFieldWithType
		def otherType = tuple.first
		def otherField = tuple.second

		def dep1 = new Dependency(thisClass.shortName(), thisClass.name,
				SourcePath.of(path), BadSmellHelper.createSourceRangeFromNode(field))
		def dep2 = new Dependency(otherType.shortName(), otherType.name,
				SourcePath.of(otherPath), BadSmellHelper.createSourceRangeFromNode(otherField))
		def cycle = new Cycle(dep1, dep2)

		if (!smells.contains(cycle)) {
			smells.add(cycle)
		}
	}

}
