package io.gitlab.arturbosch.smartsmells.smells.dataclass

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import io.gitlab.arturbosch.jpal.ast.ClassHelper
import io.gitlab.arturbosch.jpal.ast.NodeHelper
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.common.Visitor

import java.util.stream.Collectors

/**
 * @author artur
 */
class DataClassVisitor extends Visitor<DataClass> {

	String currentClassName

	@Override
	void visit(ClassOrInterfaceDeclaration n, Resolver resolver) {

		n.getNodesByType(ClassOrInterfaceDeclaration.class)
				.each { visit(it, resolver) }

		if (n.interface) return

		currentClassName = n.name
		def filteredMethods = NodeHelper.findMethods(n).stream()
				.filter { ClassHelper.inClassScope(it, currentClassName) }
				.collect(Collectors.toList())

		if (DataClassHelper.checkMethods(filteredMethods)) {

			String signature = ClassHelper.createFullSignature(n)
			smells.add(new DataClass(n.nameAsString, signature,
					SourceRange.fromNode(n), SourcePath.of(path)))
		}
	}

}
