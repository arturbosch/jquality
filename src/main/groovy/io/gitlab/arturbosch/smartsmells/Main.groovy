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

	@Parameter(names = ["--input", "-i"], description = "Specify a path where your project is located for the analysis.")
	String projectPath
	@Parameter(names = ["--output", "-o"], description = "Point to a path where the xml output file with the detection result should be saved.")
	String outputPath
	@Parameter(names = ["--config", "-c"], description = "Point to your SmartSmells configuration file. Prefer this over -f if only specified detectors are needed. Take a look at the default-config.yml file within SmartSmells git repository for an example.")
	String configPath
	@Parameter(names = ["--filters", "-f"], description = "Regex expressions, separated by a comma to specify path filters eg. '.*/test/.*'")
	String filters
	@Parameter(names = ["--fullStack", "-fs"], description = "Use all available detectors with default thresholds.")
	Boolean fullStackFacade
	@Parameter(names = ["--help", "-h"], description = "Shows this help message.")
	Boolean help

	static benchmark = { closure ->
		def start = System.currentTimeMillis()
		closure.call()
		def now = System.currentTimeMillis()
		now - start
	}

	@SuppressWarnings("GroovyResultOfObjectAllocationIgnored")
	static void main(String... args) {
		Main main = new Main()
		try {
			def jCommander = new JCommander(main, args)
			jCommander.setProgramName("SmartSmells")
			if (main.help) {
				jCommander.usage()
				return
			}
		} catch (any) {
			exitExceptionally(any.message)
		}
		main.validateParsedArguments()

		println "\n Duration: " + benchmark {
			main.run()
		} / 1000
	}

	private static void exitExceptionally(String message) {
		System.err.println(message)
		System.exit(-1)
	}

	private void validateParsedArguments() {
		def pathError = "The path to the project file is not specified."
		def configError = "The path to the config file is not specified."
		def outputError = "The path to the output file is not specified."

		try {
			Validate.isTrue(projectPath != null && Files.exists(Paths.get(projectPath)), pathError)
			Validate.isTrue(outputPath != null, outputError)

			if (!fullStackFacade) {
				Validate.isTrue(configPath != null, configError)
				Validate.isTrue(Files.exists(Paths.get(configPath)), configError)
			}
		} catch (any) {
			exitExceptionally(any.message)
		}
	}

	private void run() {
		def filters = (filters?.split()?.collect { it.trim() } ?: Collections.emptyList()) as List<String>
		if (fullStackFacade) {
			run(DetectorFacade.builder().withFilters(filters).fullStackFacade())
		} else {
			def path = Paths.get(configPath)
			def config = DetectorConfig.load(path)
			run(DetectorFacade.builder().withFilters(filters).fromConfig(config).build())
		}
	}

	private void run(DetectorFacade facade) {
		def project = Paths.get(projectPath)
		def result = facade.run(project)
		writeToFile(result)
		result.prettyPrint()
	}

	private Path writeToFile(SmellResult result) {
		def path = Paths.get(outputPath)
		Files.createDirectories(path)
		Files.write(path, XMLWriter.toXml(result).getBytes())
	}

}
