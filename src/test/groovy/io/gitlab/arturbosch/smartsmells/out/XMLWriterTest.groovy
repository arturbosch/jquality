package io.gitlab.arturbosch.smartsmells.out

import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.smartsmells.smells.ElementTarget
import io.gitlab.arturbosch.smartsmells.smells.featureenvy.FeatureEnvy
import spock.lang.Specification

import java.nio.file.Paths

/**
 * @author artur
 */
class XMLWriterTest extends Specification {

	def "smell to xml entry with escapes"() {

		when:
		def xml = XMLWriter.toXmlEntry(new FeatureEnvy("methode", "signature", "class",
				"<\"'>&", "objectSignature", "Locale",
				1d, 1d, SourceRange.of(1, 1, 1, 1), SourcePath.of(Paths.get("path"), Paths.get("path")), ElementTarget.METHOD))

		println xml
		then:
		xml.startsWith("<FeatureEnvy")
		xml.endsWith("/>")
		xml.contains("objectSignature")
		xml.contains("objectType")
		xml.contains("&lt;&quot;&apos;&gt;&amp;")
		!xml.contains("null")
	}

}
