package io.gitlab.arturbosch.smartsmells.api

import groovy.transform.CompileStatic
import io.gitlab.arturbosch.smartsmells.config.DetectorConfig
import io.gitlab.arturbosch.smartsmells.config.dsl.DetectorConfigDsl
import io.gitlab.arturbosch.smartsmells.metrics.internal.FullstackMetrics
import io.gitlab.arturbosch.smartsmells.util.Validate

/**
 * @author Artur Bosch
 */
@CompileStatic
class MetricFacadeBuilder {

	private List<CompositeMetricRaiser> compositeRaisers = new ArrayList<>()
	private List<MetricRaiser> raisers = new LinkedList<>()
	private List<String> filters = new ArrayList<String>()
	private DetectorConfig config = null

	MetricFacadeBuilder with(MetricRaiser raiser) {
		Validate.notNull(raiser)
		raisers.add(raiser)
		return this
	}

	MetricFacadeBuilder with(CompositeMetricRaiser raiser) {
		Validate.notNull(raiser)
		compositeRaisers.add(raiser)
		return this
	}

	MetricFacadeBuilder withLoader(final MetricLoader loader) {
		Validate.notNull(loader)
		raisers.addAll(loader.loadSimple())
		compositeRaisers.addAll(loader.loadComposite())
		return this
	}

	MetricFacadeBuilder withFilters(final List<String> filters) {
		this.filters = Validate.notNull(filters)
		return this
	}

	MetricFacadeBuilder fromConfig(final DetectorConfigDsl config) {
		Validate.notNull(config, "Configuration must not be null!")
		raisers = []
		this.config = config.build()
		return this
	}

	MetricFacade fullStackFacade() {
		compositeRaisers.add(FullstackMetrics.create())
		return build()
	}

	MetricFacade build() {
		def simple = new SimpleCompositeMetricRaiser(raisers)
		def combined = new CombinedCompositeMetricRaiser(compositeRaisers + simple)
		return new MetricFacade(combined, config, filters)
	}
}