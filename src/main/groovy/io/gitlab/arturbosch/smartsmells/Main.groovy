package io.gitlab.arturbosch.smartsmells

import com.beust.jcommander.JCommander
import com.beust.jcommander.Parameter
import io.gitlab.arturbosch.smartsmells.config.Smell
import io.gitlab.arturbosch.smartsmells.config.dsl.DetectorConfigDslRunner
import io.gitlab.arturbosch.smartsmells.util.Validate

import java.nio.file.Files
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
	@Parameter(names = ["--groovy-config", "-gc"], description = "Point to your SmartSmells groovy-based configuration file. If using groovy config, no other parameters are necessary as everything can be configured through the groovy dsl. Take a look at the default-config.groovy file for an example.")
	String groovyConfigPath
	@Parameter(names = ["--filters", "-f"], description = "Regex expressions, separated by a comma to specify path filters eg. '.*/test/.*'")
	String filters
	@Parameter(names = ["--fullStack", "--fullstack", "-fs"], description = "Use all available detectors with default thresholds.")
	Boolean fullStackFacade
	@Parameter(names = ["--metrics", "-m"], description = "Additionally runs the metric facade, printing the means for configured metrics.")
	boolean runMetrics
	@Parameter(names = ["--help", "-h"], description = "Shows this help message.")
	boolean help

	static benchmark = { closure ->
		def start = System.currentTimeMillis()
		closure.call()
		def now = System.currentTimeMillis()
		now - start
	}

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

		def runner = main.validateParsedArguments()
		start(runner)
	}

	private static start(Runner runner) {
		println("\n Duration: " + benchmark {
			if (runner) {
				runner.run().prettyPrint(*Smell.values())
			} else {
				throw new IllegalStateException("Oops, this should never happen. Somehow a specialized Runner could not be created!")
			}
		} / 1000)
	}

	private Runner validateParsedArguments() {
		def pathError = "The path to the project file is not specified."
		def configError = "The path to the config file is not specified."

		try {
			if (groovyConfigPath) {
				return buildGroovyConfigurationRunner()
			} else {
				return buildConfigOrFullStackRunner(pathError, configError)
			}
		} catch (any) {
			exitExceptionally(any.message)
		}
		return null
	}

	private Runner buildGroovyConfigurationRunner() {
		def path = Paths.get(groovyConfigPath)
		Validate.isTrue(Files.exists(path), configPath)
		def configDsl = DetectorConfigDslRunner.execute(path.toFile())
		configDsl.validate()
		def groovyDslRunner = new GroovyDslRunner(configDsl)
		return runMetrics ? new CompositeRunner(configDsl, [groovyDslRunner, new MetricRunner(configDsl)])
				: groovyDslRunner
	}

	private Runner buildConfigOrFullStackRunner(String pathError, String configError) {
		Validate.isTrue(projectPath != null, pathError)
		def project = Paths.get(projectPath)
		Validate.isTrue(Files.exists(project), pathError)
		def output = Optional.ofNullable(outputPath).map { Paths.get(it) }

		List<String> filters = (filters?.split()?.collect { it.trim() } ?: Collections.emptyList()) as List<String>
		if (!fullStackFacade) {
			Validate.isTrue(configPath != null, configError)
			def config = Paths.get(configPath)
			Validate.isTrue(Files.exists(config), configError)
			return new YamlConfigRunner(config, project, output, filters)
		} else {
			return new FullStackRunner(project, output, filters)
		}
	}

	private static void exitExceptionally(String message) {
		System.err.println(message)
		System.exit(-1)
	}

}
