package io.gitlab.arturbosch.smartsmells.java;

import java.io.IOException;

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

		// no doc 3
		/**
		 * Not blank but no throws!
		 *
		 * @throws IOException
		 */
		void throwsNotDocumented() throws IOException;
	}

	public enum NoDocForEnum {

	}

}
