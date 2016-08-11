package io.gitlab.arturbosch.smartsmells.smells.dataclass

import com.github.javaparser.ASTHelper
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import io.gitlab.arturbosch.jpal.ast.ClassHelper
import io.gitlab.arturbosch.jpal.ast.NodeHelper
import io.gitlab.arturbosch.smartsmells.common.Visitor
import io.gitlab.arturbosch.smartsmells.common.helper.BadSmellHelper
import io.gitlab.arturbosch.smartsmells.common.source.SourcePath

import java.nio.file.Path
import java.util.stream.Collectors

/**
 * @author artur
 */
class DataClassVisitor extends Visitor<DataClass> {

	String currentClassName

	DataClassVisitor(Path path) {
		super(path)
	}

	@Override
	void visit(ClassOrInterfaceDeclaration n, Object arg) {

		ASTHelper.getNodesByType(n, ClassOrInterfaceDeclaration.class)
				.each { visit(it, null) }

		if (n.interface) return

		currentClassName = n.name
		def filteredMethods = NodeHelper.findMethods(n).stream()
				.filter { ClassHelper.inClassScope(it, currentClassName) }
				.collect(Collectors.toList())

		if (DataClassHelper.checkMethods(filteredMethods)) {

			String signature = BadSmellHelper.createClassSignature(n)
			smells.add(new DataClass(n.getName(), signature,
					BadSmellHelper.createSourceRangeFromNode(n),
					SourcePath.of(path)))
		}
	}

}
