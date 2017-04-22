package io.gitlab.arturbosch.smartsmells.api

import io.gitlab.arturbosch.smartsmells.config.Smell
import io.gitlab.arturbosch.smartsmells.smells.DetectionResult
import io.gitlab.arturbosch.smartsmells.smells.cycle.Cycle

/**
 * @author Artur Bosch
 */
class SmellResult {

	private Map<Smell, List<DetectionResult>> smellSets

	SmellResult(Map<Smell, List<DetectionResult>> smellSets = new HashMap<>()) {
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
		def summary = ""
		def printList = Arrays.stream(smells)
				.filter { it != Smell.CLASS_INFO }
				.collect()
		println()
		smellSets.entrySet().each {
			if (printList.contains(it.key)) {
				it.value.each { println it }
			}
			summary += "\n$it.key: ${it.value.size()}"
		}
		println(summary)
		println()
		if (smells.contains(Smell.UNKNOWN)) {
			smellSets.entrySet().stream()
					.filter { it.key == Smell.UNKNOWN }
					.findFirst()
					.ifPresent { it.value.each { println it.toString() } }
		}
	}

	Map<Smell, List<DetectionResult>> getSmellSets() {
		return smellSets
	}

	SmellResult merge(SmellResult forMerge) {
		forMerge.smellSets.each {
			smellSets.merge(it.key, it.value) { l1, l2 ->
				def result = new ArrayList<>(l1)
				result.addAll(l2)
				return result
			}
		}
		return this
	}
}
