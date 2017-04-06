package io.gitlab.arturbosch.smartsmells.java;

@SuppressWarnings("ALL")
public class JavadocDummy {

	// no doc 1
	public interface NoDoc {

	}

	/**
	 * Ulala some documentation :3 <3
	 */
	public interface HaveDoc {

		public class MeNoDoc {

		}

		// no doc 2
		void verySpecialUndocumentedMethod();
	}

	public enum NoDocForEnum {

	}

}
