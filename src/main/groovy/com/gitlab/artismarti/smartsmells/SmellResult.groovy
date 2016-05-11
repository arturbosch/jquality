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
//			if (it.key.equals(Smell.MIDDLE_MAN)) {
//				it.value.each { println it.toString() }
//			}
			println "$it.key: ${it.value.size()}"
		}
	}
}
