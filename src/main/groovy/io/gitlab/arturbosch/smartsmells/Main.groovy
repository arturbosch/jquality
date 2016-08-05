package io.gitlab.arturbosch.smartsmells

import com.beust.jcommander.JCommander
import com.beust.jcommander.Parameter
import io.gitlab.arturbosch.smartsmells.api.DetectorFacade
import io.gitlab.arturbosch.smartsmells.api.SmellResult
import io.gitlab.arturbosch.smartsmells.config.DetectorConfig
import io.gitlab.arturbosch.smartsmells.out.XMLWriter
import io.gitlab.arturbosch.smartsmells.util.Validate

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * @author artur
 */
class Main {

	@Parameter(names = ["--input", "-i"], description = "Project path to analyze")
	String projectPath;
	@Parameter(names = ["--output", "-o"], description = "Xml output path")
	String outputPath;
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
		def outputError = "The path to the output file is not specified."

		Validate.isTrue(projectPath != null && Files.exists(Paths.get(projectPath)), pathError)
		Validate.isTrue(outputPath != null, outputError)

		if (!fullStackFacade) {
			Validate.isTrue(configPath != null, configError)
			Validate.isTrue(Files.exists(Paths.get(configPath)), configError)
		}
	}

	private void run() {
		def project = Paths.get(projectPath)
		if (fullStackFacade) {
			run(DetectorFacade.fullStackFacade(), project)
		} else {
			def path = Paths.get(configPath)
			def config = DetectorConfig.load(path)
			run(DetectorFacade.fromConfig(config), project)
		}
	}

	private void run(DetectorFacade facade, Path project) {
		def result = facade.run(project)
		writeToFile(result)
		result.prettyPrint()
	}

	private Path writeToFile(SmellResult result) {
		Files.write(Paths.get(outputPath), XMLWriter.toXml(result).getBytes())
	}

}