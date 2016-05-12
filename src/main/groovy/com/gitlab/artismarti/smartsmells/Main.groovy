package com.gitlab.artismarti.smartsmells

import com.beust.jcommander.JCommander
import com.beust.jcommander.Parameter
import com.gitlab.artismarti.smartsmells.config.DetectorConfig
import com.gitlab.artismarti.smartsmells.start.DetectorFacade
import com.gitlab.artismarti.smartsmells.util.Validate

import java.nio.file.Files
import java.nio.file.Paths
/**
 * @author artur
 */
class Main {

	@Parameter(names = ["--path", "-p"], description = "Project path to analyze")
	String projectPath;
	@Parameter(names = ["--config", "-c"], description = "Config path to use")
	String configPath;
	@Parameter(names = ["--fullStack", "-f"], description = "Use all available detectors with default thresholds")
	Boolean fullStackFacade;

	def static benchmark = { closure ->
		def start = System.currentTimeMillis()
		closure.call()
		def now = System.currentTimeMillis()
		now - start
	}

	@SuppressWarnings("GroovyResultOfObjectAllocationIgnored")
	static void main(String... args) {
		Main main = new Main()
		new JCommander(main, args)
		main.validateParsedArguments()

		println "\n Duration: " + benchmark {
			main.run();
		} / 1000
	}

	private void validateParsedArguments() {
		def pathError = "The path to the project file is not specified."
		def configError = "The path to the config file is not specified."

		Validate.isTrue(projectPath != null, pathError)
		Validate.isTrue(Files.exists(Paths.get(projectPath)), pathError)

		if (!fullStackFacade) {
			Validate.isTrue(configPath != null, configError)
			Validate.isTrue(Files.exists(Paths.get(configPath)), configError)
		}
	}

	private void run() {
		def project = Paths.get(projectPath)
		if (fullStackFacade) {
			DetectorFacade.builder().fullStackFacade()
					.run(project)
					.prettyPrint()
		} else {
			def config = Paths.get(configPath)
			DetectorFacade.fromConfig(DetectorConfig.load(config))
					.run(project)
					.prettyPrint()
		}
	}

}
