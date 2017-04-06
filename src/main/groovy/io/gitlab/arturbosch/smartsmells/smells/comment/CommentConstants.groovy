package io.gitlab.arturbosch.smartsmells.smells.comment

/**
 * @author Artur Bosch
 */
final class CommentConstants {
	private CommentConstants() {}

	private final
	static String DEFAULT_MESSAGE = "are considered as a smell, try to refactor your code so others will understand it without a comment."
	final static String ORPHAN_MESSAGE = "Loosely comments " + DEFAULT_MESSAGE
	final static String PRIVATE_JAVADOC_MESSAGE = "Javadoc over private or package private methods " + DEFAULT_MESSAGE
	final
	static String MISSING_PUBLIC_JAVADOC = "All public members should have a javadoc as this helps other maintainers understand the code more quickly."
	final
	static String MISSING_RETURN_TAG = "The return value should be specified and described which contracts they meet."
	final static String MISSING_PARAM_TAG = "All parameters must be specified and their contracts described."
	final static String MISSING_THROWS_TAG = "All thrown exceptions must be specified and their contracts described."

}
