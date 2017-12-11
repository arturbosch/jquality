package io.gitlab.arturbosch.smartsmells

import io.gitlab.arturbosch.jpal.core.JPAL
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.api.Detector
import io.gitlab.arturbosch.smartsmells.common.Visitor
import io.gitlab.arturbosch.smartsmells.metrics.FileMetricProcessor
import io.gitlab.arturbosch.smartsmells.smells.DetectionResult
import io.gitlab.arturbosch.smartsmells.util.Validate
import spock.lang.Specification

import java.nio.file.Path
import java.nio.file.Paths

/**
 * @author Artur Bosch
 */
abstract class DetectorSpecification<T extends DetectionResult> extends Specification {

	abstract Detector<T> detector()

	Set<T> run(String code) {
		Validate.notNull(code)
		def detector = detector()
		def visitorMethod = detector.class.getDeclaredMethods().find { it.name == "getVisitor" }
		visitorMethod.accessible = true
		def visitor = visitorMethod.invoke(detector) as Visitor<T>

		def path = Paths.get("foo/bar.java")
		def storage = JPAL.updatable(new FileMetricProcessor())
		def map = new HashMap<Path, String>()
		map.put(path, code)
		storage.updateCompilationInfo(map)
		def info = storage.getCompilationInfo(path).get()

		visitor.visit(info, new Resolver(storage))
		return visitor.smells
	}

	Set<T> run(Path path) {
		Validate.notNull(path)
		def resolver = new Resolver(JPAL.newInstance(path, new FileMetricProcessor()))
		def info = resolver.find(path).get()
		detector().execute(info, resolver)
	}

	Set<T> run(Path path, Detector<T> detector) {
		Validate.notNull(path)
		def resolver = new Resolver(JPAL.newInstance(path, new FileMetricProcessor()))
		def info = resolver.find(path).get()
		detector.execute(info, resolver)
	}
}
