package system

import io.gitlab.arturbosch.jpal.core.CompilationInfo
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.common.Detector
import io.gitlab.arturbosch.smartsmells.common.Visitor
import io.gitlab.arturbosch.smartsmells.config.Defaults
import io.gitlab.arturbosch.smartsmells.config.Smell
import io.gitlab.arturbosch.smartsmells.metrics.ClassInfo
import io.gitlab.arturbosch.smartsmells.metrics.FileInfo
import io.gitlab.arturbosch.smartsmells.smells.ElementTarget
import io.gitlab.arturbosch.smartsmells.smells.godclass.GodClass

/**
 * @author Artur Bosch
 */
class GodClassMetricVisitor extends Detector<GodClass> {

	@Override
	Smell getType() {
		return Smell.GOD_CLASS
	}

	@Override
	protected Visitor getVisitor() {
		return new Visitor() {

			int wmcThreshold = Defaults.WEIGHTED_METHOD_COUNT
			int atfdThreshold = Defaults.ACCESS_TO_FOREIGN_DATA
			double tccThreshold = Defaults.TIED_CLASS_COHESION

			@Override
			void visit(CompilationInfo n, Resolver resolver) {
				resolver.storage.getCompilationInfo(info.path).ifPresent { info ->
					def object = info.getProcessedObject(FileInfo.class)
					object.classes.each {
						if (checkThresholds(it)) {
							addSmell(it)
						}
					}
				}
			}

			private boolean checkThresholds(ClassInfo n) {
				n.atfd > atfdThreshold &&
						(n.wmc > wmcThreshold ||
								n.tcc < tccThreshold)
			}

			private boolean addSmell(ClassInfo n) {
				smells.add(new GodClass(n.name, n.signature, n.wmc, n.tcc, n.atfd,
						wmcThreshold, tccThreshold,
						atfdThreshold, n.sourcePath, n.sourceRange, ElementTarget.CLASS))
			}
		}
	}
}
