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
import io.gitlab.arturbosch.smartsmells.common.Visitor

/**
 * @author artur
 */
class CycleVisitor extends Visitor<Cycle> {

	@Override
	void visit(ClassOrInterfaceDeclaration n, Resolver resolver) {
		String unqualifiedName = ClassHelper.appendOuterClassIfInnerClass(n)
		def thisClassType = resolver.resolveType(new ClassOrInterfaceType(unqualifiedName), info)

		def fields = NodeHelper.findFields(n)
		fields.each { field ->

			def unqualifiedFieldName = info.data.innerClassesHandler.getUnqualifiedNameForInnerClass(field.commonType)
			def qualifiedType = resolver.resolveType(new ClassOrInterfaceType(unqualifiedFieldName), info)

			if (qualifiedType.isReference()) {
				searchForCycles(qualifiedType, thisClassType, field, resolver)
			}
		}

		super.visit(n, resolver)
	}

	def searchForCycles(QualifiedType otherType, QualifiedType thisType,
						FieldDeclaration field, Resolver resolver) {

		resolver.find(otherType).ifPresent {
			def visitor = new SameFieldTypeVisitor(thisType)
			visitor.visit(it, resolver)

			if (visitor.haveFound()) {
				addCycle(visitor, thisType, field, it)
			}
		}

	}

	private void addCycle(SameFieldTypeVisitor visitor, QualifiedType thisClass,
						  FieldDeclaration field, CompilationInfo otherInfo) {
		def tuple = visitor.foundFieldWithType
		def otherType = tuple.first
		def otherField = tuple.second

		def dep1 = new Dependency(thisClass.shortName(), thisClass.name,
				SourcePath.of(info), SourceRange.fromNode(field))
		def dep2 = new Dependency(otherType.shortName(), otherType.name,
				SourcePath.of(otherInfo), SourceRange.fromNode(otherField))
		def cycle = new Cycle(dep1, dep2)

		if (!smells.contains(cycle)) {
			smells.add(cycle)
		}
	}

}
