package com.gitlab.artismarti.smartsmells.smells.dataclass

import com.github.javaparser.ASTHelper
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.gitlab.artismarti.smartsmells.common.Visitor
import com.gitlab.artismarti.smartsmells.common.helper.BadSmellHelper
import com.gitlab.artismarti.smartsmells.common.helper.ClassHelper
import com.gitlab.artismarti.smartsmells.common.helper.NodeHelper
import com.gitlab.artismarti.smartsmells.common.source.SourcePath
import com.gitlab.artismarti.smartsmells.common.source.SourceRange

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
				.filter { ClassHelper.inCurrentClass(it, currentClassName) }
				.collect(Collectors.toList())

		if (DataClassHelper.checkMethods(filteredMethods)) {

			String signature = BadSmellHelper.createClassSignature(n)
			smells.add(new DataClass(n.getName(), signature,
					SourceRange.of(n.beginLine, n.endLine, n.beginColumn, n.endColumn),
					SourcePath.of(path)))
		}
	}

}
