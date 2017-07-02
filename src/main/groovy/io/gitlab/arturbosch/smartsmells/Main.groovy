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
	@Parameter(names = ["--config", "-c"], description = "Point to your SmartSmells configuration file. Supported formats are YAML and GROOVY. Take a look at default-config.[yaml|groovy]")
	String configPath
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
		def jcommander = new JCommander(main)
		try {
			jcommander.parse(args)
			jcommander.setProgramName("SmartSmells")
			if (main.help) {
				jcommander.usage()
				return
			}
			def runner = main.buildRunnerBasedOnParameters()
			println("\n Duration: " + benchmark {
				runner.run().prettyPrint(*Smell.values())
			} / 1000)
		} catch (any) {
			System.err.println(any.message + "\n")
			jcommander.usage()
		}
	}

	private Runner buildRunnerBasedOnParameters() {
		def configFile = new File(configPath).name
		def ending = configFile.substring(configFile.lastIndexOf('.'))
		if (ending == ".groovy") {
			return buildGroovyConfigurationRunner()
		} else {
			return buildConfigOrFullStackRunner()
		}
	}

	private Runner buildGroovyConfigurationRunner() {
		def path = Paths.get(configPath)
		Validate.isTrue(Files.exists(path), configPath)
		def configDsl = DetectorConfigDslRunner.execute(path.toFile())
		configDsl.validate()
		def groovyDslRunner = new GroovyDslRunner(configDsl)
		return runMetrics ? new CompositeRunner(configDsl, [groovyDslRunner, new MetricRunner(configDsl)])
				: groovyDslRunner
	}

	private static final String PATH_ERROR = "The path to the project file is not specified (--input)."
	private static final String CONFIG_ERROR = "The path to the config file is not specified (--config)."

	private Runner buildConfigOrFullStackRunner() {
		Validate.isTrue(projectPath != null, PATH_ERROR)
		def project = Paths.get(projectPath)
		Validate.isTrue(Files.exists(project), PATH_ERROR)
		def output = Optional.ofNullable(outputPath).map { Paths.get(it) }

		List<String> filters = (filters?.split()?.collect { it.trim() } ?: Collections.emptyList()) as List<String>
		if (!fullStackFacade) {
			Validate.isTrue(configPath != null, CONFIG_ERROR)
			def config = Paths.get(configPath)
			Validate.isTrue(Files.exists(config), CONFIG_ERROR)
			return new YamlConfigRunner(config, project, output, filters)
		} else {
			return new FullStackRunner(project, output, filters)
		}
	}

}
