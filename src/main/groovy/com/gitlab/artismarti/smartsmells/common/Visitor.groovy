package com.gitlab.artismarti.smartsmells.common

import com.github.javaparser.ast.visitor.VoidVisitorAdapter
/**
 * @author artur
 */
abstract class Visitor<T> extends VoidVisitorAdapter<Object> {

	List<T> smells = new ArrayList<>()
}
