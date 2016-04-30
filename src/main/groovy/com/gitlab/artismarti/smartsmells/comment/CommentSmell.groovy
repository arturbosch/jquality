package com.gitlab.artismarti.smartsmells.comment

import com.gitlab.artismarti.smartsmells.common.source.SourcePath
import com.gitlab.artismarti.smartsmells.common.source.SourceRange
import groovy.transform.Immutable

/**
 * Represents a comment smell. There are two types of comment smell.
 * One is a comment above private and package private methods.
 * The other is a orphan comment inside a method. Both indicate excuses for a poor code style.
 *
 * @author artur
 */
@Immutable
class CommentSmell {

	static String ORPHAN = "ORPHAN"
	static String PRIVATE = "PRIVATE"

	String type
	String comment
	String message

	boolean hasTODO
	boolean hasFIXME

	@Delegate
	SourcePath sourcePath
	@Delegate
	SourceRange sourceRange

}
