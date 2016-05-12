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

	def "dostuff"() {

		when:
		XMLBuilder.toXmlEntry(new FeatureEnvy("methode", "signature", "object", "objectSignature",
				1d, 1d, SourcePath.of(Paths.get(".")), SourceRange.of(1, 1, 1, 1)))
		then:
		1 == 1
	}
}
