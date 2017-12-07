package io.gitlab.arturbosch.smartsmells.smells.classdatashouldbeprivate

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.FieldDeclaration
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.ast.ClassHelper
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.api.Detector
import io.gitlab.arturbosch.smartsmells.common.Visitor
import io.gitlab.arturbosch.smartsmells.config.Smell
import io.gitlab.arturbosch.smartsmells.smells.ElementTarget

/**
 * @author artur
 */
@CompileStatic
class ClassDataShouldBePrivateDetector extends Detector<ClassDataShouldBePrivate> {

	@Override
	protected ClassDataShouldBePrivateVisitor getVisitor() {
		return new ClassDataShouldBePrivateVisitor()
	}

	@Override
	Smell getType() {
		return Smell.DATA_CLASS
	}
}

@CompileStatic
class ClassDataShouldBePrivateVisitor extends Visitor<ClassDataShouldBePrivate> {

	private Deque<String> stack = new ArrayDeque<>()

	@Override
	void visit(ClassOrInterfaceDeclaration n, Resolver resolver) {
		stack.push(n.nameAsString)
		if (!n.isStatic()) {
			def hasPublicField = n.getChildNodesByType(FieldDeclaration.class).stream()
					.filter { ClassHelper.inClassScope(it, stack.peek()) }
					.filter { !it.isStatic() }
					.anyMatch { it.isPublic() }
			if (hasPublicField) {
				String signature = ClassHelper.createFullSignature(n)
				report(new ClassDataShouldBePrivate(n.nameAsString, signature,
						SourceRange.fromNode(n), SourcePath.of(info), ElementTarget.CLASS))
			}
		}
		super.visit(n, resolver)
		stack.pop()
	}
}
