package io.gitlab.arturbosch.smartsmells.api

import io.gitlab.arturbosch.jpal.core.UpdatableCompilationStorage
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.util.Validate

import java.nio.file.Path

/**
 * @author Artur Bosch
 */
class SmartSmells {

	private Resolver resolver
	private UpdatableCompilationStorage storage

	private DetectorFacade detectorFacade
	@Delegate
	private UpdatableDetectorFacade updatableDetectorFacade

	SmartSmells(final UpdatableCompilationStorage storage,
				final Resolver resolver,
				final DetectorFacade detectorFacade) {
		this.detectorFacade = Validate.notNull(detectorFacade)
		this.storage = Validate.notNull(storage)
		this.resolver = Validate.notNull(resolver)
		this.updatableDetectorFacade = new UpdatableDetectorFacade(detectorFacade, storage)
	}

	SmellResult executeForRoot(Path root) {
		storage.initialize(root)
		return detectorFacade.run(storage.allCompilationInfo, resolver)
	}
}
