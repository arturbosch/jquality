package io.gitlab.arturbosch.smartsmells

import groovy.transform.CompileStatic
import io.gitlab.arturbosch.smartsmells.api.DetectorFacade
import io.gitlab.arturbosch.smartsmells.api.SmellResult
import io.gitlab.arturbosch.smartsmells.out.XMLWriter
import io.gitlab.arturbosch.smartsmells.util.Validate

import java.nio.file.Files
import java.nio.file.Path

/**
 * @author Artur Bosch
 */
@CompileStatic
abstract class Runner {

	protected Path project
	protected Optional<Path> outputPath
	protected List<String> filters

	Runner(Path project, Optional<Path> outputPath, List<String> filters) {
		Validate.isTrue(Files.exists(project), "Project path does not exist.")
		Validate.isTrue(Files.isDirectory(project), "Project path is not a directory.")
		this.project = project
		this.outputPath = outputPath
		this.filters = filters
	}

	abstract SmellResult run()

	protected SmellResult run(DetectorFacade facade) {
		def result = facade.run(project)
		writeToFile(result)
		return result
	}

	protected void writeToFile(SmellResult result) {
		outputPath.ifPresent {
			Files.createDirectories(it.parent)
			Files.write(it, XMLWriter.toXml(result).getBytes())
		}
	}
}
