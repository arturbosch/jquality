package com.gitlab.artismarti.smartsmells.out

import com.gitlab.artismarti.smartsmells.common.source.SourcePath
import com.gitlab.artismarti.smartsmells.common.source.SourceRange
import com.gitlab.artismarti.smartsmells.featureenvy.FeatureEnvy
import spock.lang.Specification

import java.nio.file.Paths

/**
 * @author artur
 */
class XMLBuilderTest extends Specification {

	def "smell to xml entry with escapes"() {

		when:
		def xml = XMLBuilder.toXmlEntry(new FeatureEnvy("methode", "signature", "<\"'>&", "objectSignature",
				1d, 1d, SourcePath.of(Paths.get(".")), SourceRange.of(1, 1, 1, 1)))
		then:
		xml.startsWith("<FeatureEnvy")
		xml.endsWith("/>")
		xml.contains("objectSignature")
		xml.contains("&lt;&quot;&apos;&gt;&amp;")
	}
}
