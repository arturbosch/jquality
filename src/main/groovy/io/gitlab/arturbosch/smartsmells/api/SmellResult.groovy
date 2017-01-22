package io.gitlab.arturbosch.smartsmells.api

import io.gitlab.arturbosch.smartsmells.common.DetectionResult
import io.gitlab.arturbosch.smartsmells.config.Smell
import io.gitlab.arturbosch.smartsmells.smells.cycle.Cycle

/**
 * @author artur
 */
class SmellResult {

	private Map<Smell, List<DetectionResult>> smellSets

	SmellResult(Map<Smell, List<DetectionResult>> smellSets = Collections.emptyMap()) {
		this.smellSets = smellSets
	}

	List<DetectionResult> of(Smell smell) {
		smellSets.getOrDefault(smell, new ArrayList<>())
	}

	List<DetectionResult> filter(String path) {
		return smellSets.values().stream()
				.flatMap { it.stream() }
				.filter { checkSmellPaths(it, path) }
				.collect()
	}

	private static boolean checkSmellPaths(DetectionResult it, String path) {
		switch (it) {
			case Cycle: (it as Cycle).comparePath(path)
				break
			default: it.pathAsString == path
		}
	}

	void prettyPrint(Smell... smells) {

		def printList = Arrays.asList(smells)
		smellSets.entrySet().each {
			if (printList.contains(it.key)) {
				it.value.each { println it.toString() }
			}
			println "$it.key: ${it.value.size()}"
		}
	}

	Map<Smell, List<DetectionResult>> getSmellSets() {
		return smellSets
	}
}
