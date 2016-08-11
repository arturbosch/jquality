package io.gitlab.arturbosch.smartsmells.smells.cycle

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.FieldDeclaration
import com.github.javaparser.ast.type.ClassOrInterfaceType
import io.gitlab.arturbosch.jpal.ast.ClassHelper
import io.gitlab.arturbosch.jpal.ast.NodeHelper
import io.gitlab.arturbosch.jpal.core.CompilationStorage
import io.gitlab.arturbosch.jpal.core.CompilationTree
import io.gitlab.arturbosch.jpal.nested.InnerClassesHandler
import io.gitlab.arturbosch.jpal.resolve.QualifiedType
import io.gitlab.arturbosch.jpal.resolve.ResolutionData
import io.gitlab.arturbosch.jpal.resolve.Resolver
import io.gitlab.arturbosch.smartsmells.common.Visitor
import io.gitlab.arturbosch.smartsmells.common.helper.BadSmellHelper
import io.gitlab.arturbosch.smartsmells.common.source.SourcePath

import java.nio.file.Path

/**
 * @author artur
 */
class CycleVisitor extends Visitor<Cycle> {

	private ResolutionData packageImportHolder
	private InnerClassesHandler innerClassesHandler

	CycleVisitor(Path path) {
		super(path)
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
		def thisClassType = Resolver.getQualifiedType(
				packageImportHolder, new ClassOrInterfaceType(unqualifiedName))

		def fields = NodeHelper.findFields(n)
		fields.each { field ->

			def qualifiedType = Resolver.getQualifiedType(packageImportHolder, field.type)
			if (qualifiedType.isReference()) {

				def unqualifiedFieldName = innerClassesHandler.getUnqualifiedNameForInnerClass(field.type)
				qualifiedType = Resolver.getQualifiedType(
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

			def maybePath = CompilationTree.findPathFor(otherType)
			if (maybePath.isPresent()) {
				def otherPath = maybePath.get()
				CompilationTree.findCompilationUnit(otherPath).ifPresent {
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
