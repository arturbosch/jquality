package io.gitlab.arturbosch.smartsmells.smells.messagechain

import com.github.javaparser.ast.expr.Expression
import com.github.javaparser.ast.expr.MethodCallExpr
import com.github.javaparser.ast.stmt.Statement
import groovy.transform.CompileStatic
import groovy.transform.Immutable
import groovy.transform.ToString
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.jpal.internal.Printer
import io.gitlab.arturbosch.smartsmells.smells.DetectionResult
import io.gitlab.arturbosch.smartsmells.smells.ElementTarget
import io.gitlab.arturbosch.smartsmells.smells.LocalSpecific

/**
 * @author Artur Bosch
 */
@Immutable
@ToString(includeNames = false, includePackage = false)
class MessageChain implements DetectionResult, LocalSpecific {

	String signature
	String sourceEntity
	String targetEntity
	int chainSize
	int chainSizeThreshold

	@Delegate
	SourcePath sourcePath
	@Delegate
	SourceRange sourceRange

	ElementTarget elementTarget = ElementTarget.LOCAL

	@Override
	ElementTarget elementTarget() {
		return elementTarget
	}

	@Override
	String asCompactString() {
		"MessageChain\n\nchain size: $chainSize with threshold " +
				"$chainSizeThreshold\nSource: $sourceEntity\nTarget: $targetEntity"
	}

	@Override
	String asComparableString() {
		return "${javaClassName()}$signature"
	}

	@Override
	String name() {
		return sourceEntity
	}

	@Override
	String signature() {
		return signature
	}

	@Override
	LocalSpecific copy(Statement statement) {
		return this
	}

	@Override
	LocalSpecific copy(Expression expression) {
		if (expression instanceof MethodCallExpr) {
			def expr = expression as MethodCallExpr
			def signature = expr.toString(Printer.NO_COMMENTS)
			return new MessageChain(signature, MessageChainVisitor.extractSourceString(signature),
					expr.nameAsString, MessageChainVisitor.countOccurrences(signature, "."),
					chainSizeThreshold, sourcePath, SourceRange.fromNode(expression), elementTarget)
		}
		return this
	}

	boolean equals(o) {
		if (this.is(o)) return true
		if (getClass() != o.class) return false

		MessageChain that = (MessageChain) o

		if (chainSize != that.chainSize) return false
		if (chainSizeThreshold != that.chainSizeThreshold) return false
		if (elementTarget != that.elementTarget) return false
		if (signature != that.signature) return false
		if (sourceEntity != that.sourceEntity) return false
		if (sourcePath != that.sourcePath) return false
		if (sourceRange != that.sourceRange) return false
		if (targetEntity != that.targetEntity) return false

		return true
	}

	int hashCode() {
		int result
		result = (signature != null ? signature.hashCode() : 0)
		result = 31 * result + (sourceEntity != null ? sourceEntity.hashCode() : 0)
		result = 31 * result + (targetEntity != null ? targetEntity.hashCode() : 0)
		result = 31 * result + chainSize
		result = 31 * result + chainSizeThreshold
		result = 31 * result + (sourcePath != null ? sourcePath.hashCode() : 0)
		result = 31 * result + (sourceRange != null ? sourceRange.hashCode() : 0)
		result = 31 * result + (elementTarget != null ? elementTarget.hashCode() : 0)
		return result
	}
}
