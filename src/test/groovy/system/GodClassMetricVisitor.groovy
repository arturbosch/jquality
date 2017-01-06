package system

import com.github.javaparser.ast.CompilationUnit
import io.gitlab.arturbosch.jpal.core.CompilationStorage
import io.gitlab.arturbosch.smartsmells.common.Detector
import io.gitlab.arturbosch.smartsmells.common.Visitor
import io.gitlab.arturbosch.smartsmells.config.Defaults
import io.gitlab.arturbosch.smartsmells.config.Smell
import io.gitlab.arturbosch.smartsmells.metrics.ClassInfo
import io.gitlab.arturbosch.smartsmells.metrics.CompilationUnitMetrics
import io.gitlab.arturbosch.smartsmells.smells.godclass.GodClass

import java.nio.file.Path

/**
 * @author Artur Bosch
 */
class GodClassMetricVisitor extends Detector<GodClass> {

	@Override
	Smell getType() {
		return Smell.GOD_CLASS
	}

	@Override
	protected Visitor getVisitor(Path path) {
		return new Visitor(path) {

			int wmcThreshold = Defaults.WEIGHTED_METHOD_COUNT
			int atfdThreshold = Defaults.ACCESS_TO_FOREIGN_DATA
			double tccThreshold = Defaults.TIED_CLASS_COHESION

			@Override
			void visit(CompilationUnit n, Object arg) {
				CompilationStorage.getCompilationInfo(this.path).ifPresent { info ->
					def object = info.getProcessedObject(CompilationUnitMetrics.class)
					object.infos.each {
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
						atfdThreshold, n.sourcePath, n.sourceRange))
			}
		}
	}
}
