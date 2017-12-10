package io.gitlab.arturbosch.smartsmells.out

import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.jpal.resolution.QualifiedType
import io.gitlab.arturbosch.smartsmells.common.Test
import io.gitlab.arturbosch.smartsmells.metrics.ClassInfo
import io.gitlab.arturbosch.smartsmells.metrics.Metric
import io.gitlab.arturbosch.smartsmells.smells.ElementTarget
import io.gitlab.arturbosch.smartsmells.smells.featureenvy.FeatureEnvy
import io.gitlab.arturbosch.smartsmells.util.Strings
import spock.lang.Specification

import java.nio.file.Paths

/**
 * @author artur
 */
class XMLWriterTest extends Specification {

	def "smell to xml entry with escapes"() {

		def envy = new FeatureEnvy("methode", "signature", "class",
				"<\"'>&", "objectSignature", "Locale",
				1d, 1d, SourceRange.of(1, 1, 1, 1),
				SourcePath.of(Paths.get("path"), Paths.get("path")), ElementTarget.METHOD)
		when:
		def xml = XMLWriter.toXmlEntry(envy)

		then:
		xml.startsWith("<FeatureEnvy")
		xml.endsWith("/>")
		xml.contains("objectSignature")
		xml.contains("objectType")
		xml.contains("&lt;&quot;&apos;&gt;&amp;")
		!xml.contains("null")
	}

	def "class info with metrics transforms to xml"() {
		given: "class info with three metrics"
		def info = new ClassInfo(new QualifiedType("Test", QualifiedType.TypeToken.REFERENCE), "Test",
				["SIZE": Metric.of("SIZE", 5), "STUFF": Metric.of("STUFF", 2.2d)],
				SourcePath.of(Test.BASE_PATH, Test.BASE_PATH),
				SourceRange.of(0, 0, 0, 0))
		when: "persisting info"
		def xml = XMLWriter.handleClassInfo(info)
		then: "its correct xml"
		Strings.amountOf(xml, "Metric") == 2
	}

}
