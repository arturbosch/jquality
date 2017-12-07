package io.gitlab.arturbosch.smartsmells

import io.gitlab.arturbosch.jpal.core.CompilationInfo
import io.gitlab.arturbosch.jpal.core.CompilationStorage
import io.gitlab.arturbosch.jpal.resolution.QualifiedType
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.api.Detector
import io.gitlab.arturbosch.smartsmells.common.Test
import io.gitlab.arturbosch.smartsmells.common.Visitor
import io.gitlab.arturbosch.smartsmells.smells.DetectionResult
import org.spockframework.util.Assert
import spock.lang.Specification

import java.nio.file.Path
import java.nio.file.Paths

/**
 * @author Artur Bosch
 */
abstract class DetectorSpecification<T extends DetectionResult> extends Specification {

	abstract Detector<T> detector()

	Set<T> run(String code) {
		Assert.notNull(code)
		def detector = detector()
		def visitorMethod = detector.class.getDeclaredMethods().find { it.name == "getVisitor" }
		visitorMethod.accessible = true
		def visitor = visitorMethod.invoke(detector) as Visitor<T>
		def unit = Test.compileContent(code)
		def info = CompilationInfo.of(unit, Paths.get("foo/bar"))
		visitor.initialize(info)
		visitor.visit(unit, new Resolver(new NOPCompilationStorage()))
		return visitor.smells
	}
}

final class NOPCompilationStorage implements CompilationStorage {

	@Override
	Set<QualifiedType> getAllQualifiedTypes() {
		return Collections.emptySet()
	}

	@Override
	Collection<CompilationInfo> getAllCompilationInfo() {
		return Collections.emptyList()
	}

	@Override
	Optional<CompilationInfo> getCompilationInfo(Path path) {
		return Optional.empty()
	}

	@Override
	Optional<CompilationInfo> getCompilationInfo(QualifiedType qualifiedType) {
		return Optional.empty()
	}

	@Override
	Set<String> getStoredPackageNames() {
		return Collections.emptySet()
	}
}
