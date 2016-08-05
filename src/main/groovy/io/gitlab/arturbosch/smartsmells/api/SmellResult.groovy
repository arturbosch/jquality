package io.gitlab.arturbosch.smartsmells.api

import groovy.transform.PackageScope
import io.gitlab.arturbosch.smartsmells.common.Smelly
import io.gitlab.arturbosch.smartsmells.config.Smell

/**
 * @author artur
 */
class SmellResult {

	private Map<Smell, Deque<Smelly>> smellSets

	@PackageScope
	SmellResult(Map<Smell, Deque<Smelly>> smellSets) {
		this.smellSets = smellSets
	}

	Deque<Smelly> of(Smell smell) {
		smellSets.getOrDefault(smell, new ArrayDeque<>())
	}

	List<Smelly> filter(String path) {
		return smellSets.values().stream()
				.flatMap { it.stream() }
				.filter { it.pathAsString == path }
				.collect()
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

	Map<Smell, Deque<Smelly>> getSmellSets() {
		return smellSets
	}
}
