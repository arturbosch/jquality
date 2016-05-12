package com.gitlab.artismarti.smartsmells.out

import com.gitlab.artismarti.smartsmells.common.Smelly
import com.gitlab.artismarti.smartsmells.start.SmellResult

import java.lang.reflect.Field

/**
 * @author artur
 */
class XMLBuilder {

	static void toXml(SmellResult smellResult) {
		smellResult.smellSets.each { key, value ->
			value.each {
				toXmlEntry(it)
			}
		}
	}

	static void toXmlEntry(Smelly smelly) {

		def name = smelly.class.simpleName
		println name

		def path = smelly.class.getDeclaredField("sourcePath").with {
			setAccessible(true)
			["path": get(smelly).toString()]
		}
		def range = smelly.class.getDeclaredField("sourceRange").with {
			setAccessible(true)
			def pos = get(smelly).toString().split(',')
			["startLine": pos[0], "startColumn": pos[1], "endLine": pos[2], "endColumn": pos[3]]
		}

		List<Field> fields = Arrays.stream(smelly.class.declaredFields)
				.filter { !it.synthetic }
				.filter { !it.name.equals("sourceRange") }
				.filter { !it.name.equals("sourcePath") }
				.collect()

		def entries = fields.collectEntries() {
			it.setAccessible(true)
			[it.name, it.get(smelly).toString()]
		}

		entries << path << range

		def content = "<$name "
		entries.each { key, value ->
			content += """$key="$value" """
		}
		content += "/>"

		println content
	}
}
