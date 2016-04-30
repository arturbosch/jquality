package com.gitlab.artismarti.smartsmells.cycle

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.FieldDeclaration
import com.github.javaparser.ast.type.ClassOrInterfaceType
import com.gitlab.artismarti.smartsmells.common.BadSmellHelper
import com.gitlab.artismarti.smartsmells.common.NodeHelper
import com.gitlab.artismarti.smartsmells.common.Visitor
import com.gitlab.artismarti.smartsmells.common.source.SourcePath

import java.nio.file.Files
import java.nio.file.Path

/**
 * @author artur
 */
class CycleVisitor extends Visitor<Cycle> {

	private PackageImportHelper packageImportHelper

	CycleVisitor(Path path) {
		super(path)
	}

	@Override
	void visit(CompilationUnit n, Object arg) {
		packageImportHelper = new PackageImportHelper(n.package, n.imports)
		super.visit(n, arg)
	}

	@Override
	void visit(ClassOrInterfaceDeclaration n, Object arg) {

		def thisClassType = packageImportHelper.getQualifiedType(new ClassOrInterfaceType(n.name))

		def fields = NodeHelper.findFields(n)

		fields.each {

			def qualifiedType = packageImportHelper.getQualifiedType(it.type)
			if (qualifiedType.isReference()) {

				def maybeType = findReferencedType(qualifiedType)

				if (maybeType.isPresent()) {
					println maybeType.get()
					searchForCycles(maybeType.get(), thisClassType, it)
				}
			}
		}

		super.visit(n, arg)
	}

	def searchForCycles(Path path, QualifiedType thisClass, FieldDeclaration field) {

		CompilationUnit unit = getCompilationUnit(path)
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

	private static CompilationUnit getCompilationUnit(Path path) {
		def maybeUnit = CompilationTree.getUnit(path)
		def unit
		if (maybeUnit.isPresent()) {
			unit = maybeUnit.get()
		} else {
			unit = CompilationTree.compileFor(path)
		}
		unit
	}

	private Optional<Path> findReferencedType(QualifiedType it) {
		def search = "${it.name.replaceAll("\\.", "/")}.java"
		return Files.walk(startPath)
				.filter { it.endsWith(search) }
				.findFirst()
				.map { it.toAbsolutePath().normalize() }
	}
}
