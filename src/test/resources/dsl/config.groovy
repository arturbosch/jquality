//noinspection GroovyAssignabilityCheck
config {

	input "/path/to/project"
	output "/path/to/output"

	filters {
		filter ".*/test/.*"
	}

	metrics(['wmc', 'atfd', 'tcc'])
	jars(['/home/artur/Repos/SmartSmells/src/test/resources/detector.jar'])

	detectors {
		detector("longmethod") {
			let('active', 'true')
			let('threshold', '20')
		}
	}

}