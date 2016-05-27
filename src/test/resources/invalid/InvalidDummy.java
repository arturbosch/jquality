package com.gitlab.artismarti.smartsmells.integration.invalid;

/**
 * @author artur
 */
@SuppressWarnings("ALL")
public blass InvalidDummy {

	/**
	 * Ich bin ein Kommentar!
	 */
	public void publicMethod(String in, int i) {
		// TODO Kommentar
		String s = "Hiiix" + i + in;
		/*
		FIXME KOMMENTATIS MAXIMUS
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
