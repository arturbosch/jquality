package com.gitlab.artismarti.smartsmells.java;

import java.util.ArrayList;
import java.util.List;

/**
 * @author artur
 */
@SuppressWarnings("ALL") public class ConditionalDeadCodeDummy {

	public void start() {
		run(true);
	}

	private void run(boolean go) {
		boolean used = true;
		if(go && used) {
			String concat = "";
			switch (concat) {
				case "truetrue": run2(); break;
				default: break;
			}
		}
	}

	private void run2() {
		List<String> strings = new ArrayList<>();
		for (String s : strings) {
			System.out.println("");
		}
	}
}
