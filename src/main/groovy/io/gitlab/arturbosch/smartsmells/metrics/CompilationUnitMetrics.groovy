package io.gitlab.arturbosch.smartsmells.metrics

/**
 * @author Artur Bosch
 */
class CompilationUnitMetrics {

	private Set<ClassInfo> infos

	Set<ClassInfo> getInfos() {
		return Collections.unmodifiableSet(infos)
	}

	CompilationUnitMetrics(Set<ClassInfo> infos) {
		this.infos = infos
	}

	@Override
	String toString() {
		return "CompilationUnitMetrics{" +
				"infos=" + infos +
				'}'
	}
}
