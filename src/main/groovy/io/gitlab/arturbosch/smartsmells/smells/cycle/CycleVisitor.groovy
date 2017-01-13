package io.gitlab.arturbosch.smartsmells.smells.cycle

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.FieldDeclaration
import com.github.javaparser.ast.type.ClassOrInterfaceType
import io.gitlab.arturbosch.jpal.ast.ClassHelper
import io.gitlab.arturbosch.jpal.ast.NodeHelper
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.jpal.core.CompilationInfo
import io.gitlab.arturbosch.jpal.resolution.QualifiedType
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.jpal.resolution.nested.InnerClassesHandler
import io.gitlab.arturbosch.smartsmells.common.Visitor

import java.nio.file.Path

/**
 * @author artur
 */
class CycleVisitor extends Visitor<Cycle> {

	private InnerClassesHandler innerClassesHandler

	@Override
	protected void visit(CompilationInfo info, Resolver arg) {
		innerClassesHandler = info.data.innerClassesHandler
		super.visit(info.unit, arg)
	}

	@Override
	void visit(ClassOrInterfaceDeclaration n, Resolver resolver) {
		String unqualifiedName = ClassHelper.appendOuterClassIfInnerClass(n)
		def thisClassType = resolver.resolveType(new ClassOrInterfaceType(unqualifiedName), info)

		def fields = NodeHelper.findFields(n)
		fields.each { field ->

			def unqualifiedFieldName = innerClassesHandler.getUnqualifiedNameForInnerClass(field.commonType)
			def qualifiedType = resolver.resolveType(new ClassOrInterfaceType(unqualifiedFieldName), info)

			if (qualifiedType.isReference()) {
				searchForCycles(qualifiedType, thisClassType, field, resolver)
			}
		}

		super.visit(n, resolver)
	}

	def searchForCycles(QualifiedType otherType, QualifiedType thisType,
						FieldDeclaration field, Resolver resolver) {

		resolver.storage.getCompilationInfo(otherType).ifPresent {
			def visitor = new SameFieldTypeVisitor(thisType)
			visitor.visit(it, resolver)

			if (visitor.haveFound()) {
				addCycle(visitor, thisType, field, it.path)
			}
		}

	}

	private void addCycle(SameFieldTypeVisitor visitor, QualifiedType thisClass,
						  FieldDeclaration field, Path otherPath) {
		def tuple = visitor.foundFieldWithType
		def otherType = tuple.first
		def otherField = tuple.second

		def dep1 = new Dependency(thisClass.shortName(), thisClass.name,
				SourcePath.of(path), SourceRange.fromNode(field))
		def dep2 = new Dependency(otherType.shortName(), otherType.name,
				SourcePath.of(otherPath), SourceRange.fromNode(otherField))
		def cycle = new Cycle(dep1, dep2)

		if (!smells.contains(cycle)) {
			smells.add(cycle)
		}
	}

}
