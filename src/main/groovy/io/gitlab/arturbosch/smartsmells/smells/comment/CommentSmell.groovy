package io.gitlab.arturbosch.smartsmells.smells.comment

import groovy.transform.Immutable
import groovy.transform.ToString
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.smartsmells.smells.DetectionResult
import io.gitlab.arturbosch.smartsmells.smells.ElementTarget

/**
 * Represents a comment smell. There are two types of comment smell.
 * One is a comment above private and package private methods.
 * The other is a orphan comment inside a method. Both indicate excuses for a poor code style.
 *
 * @author artur
 */
@Immutable
@ToString(includePackage = false)
class CommentSmell implements DetectionResult {

	enum Type {
		ORPHAN(CommentConstants.ORPHAN_MESSAGE),
		PRIVATE(CommentConstants.PRIVATE_JAVADOC_MESSAGE),
		MISSING_PARAMETER(CommentConstants.MISSING_PARAM_TAG),
		MISSING_RETURN(CommentConstants.MISSING_RETURN_TAG),
		MISSING_JAVADOC(CommentConstants.MISSING_PUBLIC_JAVADOC)

		Type(String message) {
			this.message = message
		}
		String message
	}

	Type type
	String signature // orphan comments have no signature as they must not be located on a node

	boolean hasTODO
	boolean hasFIXME

	@Delegate
	SourcePath sourcePath
	@Delegate
	SourceRange sourceRange

	ElementTarget elementTarget = ElementTarget.ANY

	@Override
	ElementTarget elementTarget() {
		return elementTarget
	}

	@Override
	String asCompactString() {
		"CommentSmell - $type\n\n$type.message"
	}

	@Override
	String asComparableString() {
		return "${javaClassName()}$type$signature"
	}

}
