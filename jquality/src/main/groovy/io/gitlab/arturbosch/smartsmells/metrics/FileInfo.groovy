package io.gitlab.arturbosch.smartsmells.metrics

import com.github.javaparser.ast.DataKey
import groovy.transform.Canonical
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange

/**
 * @author Artur Bosch
 */
@CompileStatic
@Canonical
class FileInfo implements HasMetrics {

	static final DataKey<FileInfo> KEY = new DataKey<FileInfo>() {}

	Set<ClassInfo> classes

	@Delegate
	final SourcePath sourcePath
	@Delegate
	final SourceRange sourceRange

	FileInfo(SourcePath sourcePath,
			 SourceRange sourceRange,
			 Set<ClassInfo> classes = new HashSet<>(),
			 Map<String, Metric> metrics = new HashMap<>(0)) {
		this.sourcePath = sourcePath
		this.sourceRange = sourceRange
		this.classes = classes
		this.metrics = metrics
	}

	@Override
	String toString() {
		return "FileInfo{relativePath=$relativePath\n\t" +
				classes.collect { it.toString() }.join("\n\t") +
				'}'
	}

	/**
	 * @return the class info matching given name or null
	 */
	ClassInfo findClassByName(String name) {
		return classes.find { it.name == name }
	}

	void addClass(ClassInfo clazz) {
		classes.add(clazz)
	}
}
