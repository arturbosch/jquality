package io.gitlab.arturbosch.jpal.dummies;

import io.gitlab.arturbosch.jpal.resolution.QualifiedType;
import io.gitlab.arturbosch.jpal.resolution.ResolutionData;

import java.util.ArrayList;
import java.util.List;

/**
 * It is not allowed to deleted any of these statements.
 * Adding members is allowed if not breaking tests!
 *
 * @author artur
 */
@SuppressWarnings("ALL")
public class Dummy {

	List<String> list = new ArrayList<>();
	QualifiedType qualifiedType = null;
	ResolutionData resolutionData = ResolutionData.of(null);
	CycleDummy.InnerCycleOne one = new CycleDummy.InnerCycleOne();

	void hello() {
		ArrayList<String> strings = new ArrayList<>(list);
		String s = strings.get(0);

		switch (s) {
			case "hi":
				break;
		}
	}

	void me(int number, String text) {
		int i = number;
	}

	List<String> getList() {
		return list;
	}

	void setList(List<String> list) {
		this.list = list;
	}
}
