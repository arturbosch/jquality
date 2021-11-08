package io.gitlab.arturbosch.jpal.resolution.nested

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.TypeDeclaration
import com.github.javaparser.ast.type.ClassOrInterfaceType
import com.github.javaparser.ast.type.Type
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.internal.Printer
import io.gitlab.arturbosch.jpal.internal.Validate

/**
 * Stores inner class information of a compilation unit.
 *
 * @author artur
 */
@CompileStatic
class InnerClassesHandler {

	final TypeDeclaration mainType
	final String outerClassName
	final Set<String> innerClassesNames

	InnerClassesHandler(CompilationUnit unit) {
		Validate.notNull(unit)
		def types = unit.getTypes()
		if (types.size() >= 1) {
			mainType = types[0]
			outerClassName = mainType.name
			innerClassesNames = new InnerClassesNameCollector(outerClassName).start(mainType)
		} else {
			throw new NoClassesException("Given compilation unit has no type declarations!")
		}
	}

	/**
	 * Appends the outer class to the given inner class
	 * @param type probably a inner class type
	 * @return unqualified name for inner class
	 */
	String getUnqualifiedNameForInnerClass(Type type) {
		Validate.notNull(type)
		def name = type.toString(Printer.NO_COMMENTS)
		if (innerClassesNames.contains(name)) {
			return "$outerClassName.$name"
		} else {
			def fullName = innerClassesNames.find { it.substring(it.lastIndexOf('.') + 1) == name }
			if (fullName) {
				return fullName
			}
		}
		return "$type"
	}

	/**
	 *
	 * Appends the outer class type to the given inner class type if it is a inner class
	 * of the compilation unit of this handler.
	 *
	 * @param type probably a inner class type
	 * @return unqualified type for inner class
	 */
	ClassOrInterfaceType getUnqualifiedTypeForInnerClass(ClassOrInterfaceType type) {
		return new ClassOrInterfaceType(getUnqualifiedNameForInnerClass(type))
	}
}
