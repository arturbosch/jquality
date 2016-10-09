package io.gitlab.arturbosch.smartsmells.api

import groovy.transform.PackageScope
import io.gitlab.arturbosch.smartsmells.common.DetectionResult
import io.gitlab.arturbosch.smartsmells.config.Smell
import io.gitlab.arturbosch.smartsmells.smells.cycle.Cycle

/**
 * @author artur
 */
class SmellResult {

	private Map<Smell, Deque<DetectionResult>> smellSets

	@PackageScope
	SmellResult(Map<Smell, Deque<DetectionResult>> smellSets) {
		this.smellSets = smellSets
	}

	Deque<DetectionResult> of(Smell smell) {
		smellSets.getOrDefault(smell, new ArrayDeque<>())
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

	Map<Smell, Deque<DetectionResult>> getSmellSets() {
		return smellSets
	}
}
