package io.gitlab.arturbosch.jpal.dummies;

import java.util.ArrayList;

/**
 * @author Artur Bosch
 */
@SuppressWarnings("ALL")
public class AnonymousDummy {

	void hello() {
		ArrayList<String> strings = new ArrayList<>();
		String s = strings.get(0);

		switch (s) {
			case "hi":
				break;
		}
	}

	void me(int number, String text) {
		int i = number;

		new Runnable() {
			@Override
			public void run() {
				hello();
			}
		};
	}

}
