package io.gitlab.arturbosch.jpal.resolution

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.ImportDeclaration
import com.github.javaparser.ast.type.ClassOrInterfaceType
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.ast.TypeHelper
import io.gitlab.arturbosch.jpal.internal.Validate
import io.gitlab.arturbosch.jpal.resolution.nested.InnerClassesHandler

import java.util.stream.Collectors

/**
 * Holds information of a compilation unit. Used by a resolver to access qualified types.
 *
 * @author artur
 */
@CompileStatic
class ResolutionData {

	final String packageName
	final Map<String, String> imports
	final List<String> importsWithAsterisk
	final InnerClassesHandler innerClassesHandler

	/**
	 * From a not null compilation unit a resolution data is constructed containing the
	 * package name and a map of type name to qualified package structure of the imports.
	 */
	static ResolutionData of(CompilationUnit unit) {
		Validate.notNull(unit)

		String packageName = unit.packageDeclaration
				.map { it.nameAsString }.orElse(TypeHelper.DEFAULT_PACKAGE)

		Map<String, String> imports = unit.imports
				.grep { !(it as ImportDeclaration).asterisk }
				.collectEntries { [it.name.identifier, it.nameAsString] }

		List<String> importsWithAsterisk = unit.imports.stream()
				.filter { it.asterisk }
				.map { it.nameAsString }.collect(Collectors.toList())

		def handler = new InnerClassesHandler(unit)
		return new ResolutionData(packageName, imports, importsWithAsterisk, handler)
	}

	private ResolutionData(String packageName, Map<String, String> imports,
						   List<String> importsWithAsterisk, InnerClassesHandler handler) {
		this.packageName = packageName
		this.imports = imports
		this.importsWithAsterisk = importsWithAsterisk
		this.innerClassesHandler = handler
	}

	ClassOrInterfaceType appendOuterTypeIfInnerType(ClassOrInterfaceType type) {
		return innerClassesHandler.getUnqualifiedTypeForInnerClass(type)
	}

}
