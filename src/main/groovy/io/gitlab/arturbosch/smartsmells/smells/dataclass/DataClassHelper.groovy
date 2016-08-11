package io.gitlab.arturbosch.smartsmells.smells.dataclass

import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import io.gitlab.arturbosch.jpal.ast.MethodHelper

/**
 * @author artur
 */
class DataClassHelper extends VoidVisitorAdapter {

	private DataClassHelper() {}

	static boolean checkMethods(List<MethodDeclaration> methods) {
		boolean isDataClass = true
		methods.each {
			isDataClass &= MethodHelper.isGetterOrSetter(it)
		}
		return isDataClass
	}
}
