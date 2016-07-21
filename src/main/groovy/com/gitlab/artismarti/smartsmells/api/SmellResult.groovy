package com.gitlab.artismarti.smartsmells.api

import com.gitlab.artismarti.smartsmells.config.Smell
import com.gitlab.artismarti.smartsmells.common.Smelly
import groovy.transform.PackageScope

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
				.filter { bySourcePath(it).equals(path) }
				.collect()
	}

	private static String bySourcePath(Smelly smelly) {
		return smelly.class.getDeclaredField("sourcePath").with {
			setAccessible(true)
			get(smelly).toString()
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

	Map<Smell, Deque<Smelly>> getSmellSets() {
		return smellSets
	}
}
