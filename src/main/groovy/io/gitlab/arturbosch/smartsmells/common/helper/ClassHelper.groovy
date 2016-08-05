package io.gitlab.arturbosch.smartsmells.common.helper

import com.github.javaparser.ast.Node
/**
 * @author artur
 */
class ClassHelper {

	private ClassHelper() {}

	static boolean inCurrentClass(Node node, String className) {
		return NodeHelper.findDeclaringClass(node)
				.filter { it.name == className }
				.isPresent()
	}

}
