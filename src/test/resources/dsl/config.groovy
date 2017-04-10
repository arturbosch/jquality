//noinspection GroovyAssignabilityCheck
config {

	input "/path/to/project"
	output "/path/to/output"

	filters {
		filter ".*/test/.*"
	}

	detectors {
		detector("longmethod") {
			let('active', 'true')
			let('threshold', '20')
		}
	}

}