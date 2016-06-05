package com.gitlab.artismarti.smartsmells.out

import com.gitlab.artismarti.smartsmells.common.source.SourcePath
import com.gitlab.artismarti.smartsmells.common.source.SourceRange
import com.gitlab.artismarti.smartsmells.featureenvy.FeatureEnvy
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
				1d, 1d, SourcePath.of(Paths.get(".")), SourceRange.of(1, 1, 1, 1)))

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
