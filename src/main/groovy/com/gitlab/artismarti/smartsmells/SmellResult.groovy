package com.gitlab.artismarti.smartsmells

import com.gitlab.artismarti.smartsmells.common.Smell
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

	def prettyPrint() {
		smellSets.entrySet().each {
			println "$it.key: ${it.value.size()}"
		}
	}
}
