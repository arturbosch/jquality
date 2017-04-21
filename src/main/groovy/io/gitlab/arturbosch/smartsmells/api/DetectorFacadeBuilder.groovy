package io.gitlab.arturbosch.smartsmells.api

import groovy.transform.CompileStatic
import io.gitlab.arturbosch.smartsmells.config.DetectorConfig
import io.gitlab.arturbosch.smartsmells.config.DetectorInitializer
import io.gitlab.arturbosch.smartsmells.metrics.ClassInfoDetector
import io.gitlab.arturbosch.smartsmells.smells.comment.CommentDetector
import io.gitlab.arturbosch.smartsmells.smells.comment.JavadocDetector
import io.gitlab.arturbosch.smartsmells.smells.complexcondition.ComplexConditionDetector
import io.gitlab.arturbosch.smartsmells.smells.complexmethod.ComplexMethodDetector
import io.gitlab.arturbosch.smartsmells.smells.cycle.CycleDetector
import io.gitlab.arturbosch.smartsmells.smells.dataclass.DataClassDetector
import io.gitlab.arturbosch.smartsmells.smells.deadcode.DeadCodeDetector
import io.gitlab.arturbosch.smartsmells.smells.featureenvy.FeatureEnvyDetector
import io.gitlab.arturbosch.smartsmells.smells.godclass.GodClassDetector
import io.gitlab.arturbosch.smartsmells.smells.largeclass.LargeClassDetector
import io.gitlab.arturbosch.smartsmells.smells.longmethod.LongMethodDetector
import io.gitlab.arturbosch.smartsmells.smells.longparam.LongParameterListDetector
import io.gitlab.arturbosch.smartsmells.smells.messagechain.MessageChainDetector
import io.gitlab.arturbosch.smartsmells.smells.middleman.MiddleManDetector
import io.gitlab.arturbosch.smartsmells.smells.nestedblockdepth.NestedBlockDepthDetector
import io.gitlab.arturbosch.smartsmells.smells.shotgunsurgery.ShotgunSurgeryDetector
import io.gitlab.arturbosch.smartsmells.smells.statechecking.StateCheckingDetector
import io.gitlab.arturbosch.smartsmells.util.Validate

/**
 * @author Artur Bosch
 */
@CompileStatic
class DetectorFacadeBuilder {

	private List<Detector> detectors = new LinkedList<>()
	private List<String> filters = new ArrayList<String>()
	private DetectorConfig config = null

	DetectorFacadeBuilder with(Detector detector) {
		Validate.notNull(detector)
		detectors.add(detector)
		return this
	}

	DetectorFacadeBuilder withLoader(DetectorLoader loader) {
		Validate.notNull(loader)
		detectors.addAll(loader.load())
		return this
	}

	DetectorFacade fullStackFacade() {
		detectors = [new CommentDetector(), new JavadocDetector(), new DeadCodeDetector(),
					 new LongMethodDetector(), new LongParameterListDetector(), new ComplexMethodDetector(),
					 new LargeClassDetector(), new DataClassDetector(),
					 new CycleDetector(), new FeatureEnvyDetector(), new MiddleManDetector(),
					 new ShotgunSurgeryDetector(), new MessageChainDetector(), new GodClassDetector(),
					 new StateCheckingDetector(), new ComplexConditionDetector(), new NestedBlockDepthDetector()]
		return build()
	}

	DetectorFacadeBuilder withFilters(List<String> filters) {
		this.filters = Validate.notNull(filters)
		return this
	}

	static DetectorFacade metricFacade() {
		return new DetectorFacadeBuilder().with(new ClassInfoDetector()).build()
	}

	DetectorFacadeBuilder fromConfig(final DetectorConfig config) {
		Validate.notNull(config, "Configuration must not be null!")
		detectors = DetectorInitializer.init(config)
		this.config = config
		return this
	}

	DetectorFacade build() {
		return new DetectorFacade(detectors, config, filters)
	}
}