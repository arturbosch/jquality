package com.gitlab.artismarti.smartsmells.java;

/**
 * @author artur
 */
@SuppressWarnings("ALL") public class CommentDummy {

	/**
	 * Ich bin ein Kommentar!
	 */
	public void publicMethod(String in, int i) {
		// Kommentar
		String s = "Hiiix" + i + in;
		/*
		KOMMENTATIS MAXIMUS
		 */
		System.out.println(s);
	}

	/**
	 * Ich bin soooo Private!
	 */
	private void privateMethod(String s) {
		System.out.println(s);
	}
}
