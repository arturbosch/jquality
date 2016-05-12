package com.gitlab.artismarti.smartsmells.out

import com.gitlab.artismarti.smartsmells.common.Smelly
import com.gitlab.artismarti.smartsmells.config.Smell
import com.gitlab.artismarti.smartsmells.start.SmellResult
import com.gitlab.artismarti.smartsmells.util.Strings

import java.lang.reflect.Field
/**
 * @author artur
 */
class XMLBuilder {

	static void toXml(SmellResult smellResult) {
		smellResult.smellSets.each { key, value ->
			value.each {
				if (key == Smell.COMPLEX_METHOD || key == Smell.LONG_PARAM) {
					println handleLongMethodDelegates(it)
				} else if (key == Smell.CYCLE) {
					println handleDependencyDelegates(it)
				} else {
					println toXmlEntry(it)
				}
			}
		}
	}

	private static String handleDependencyDelegates(Smelly smelly) {
		def name = smelly.class.simpleName
		def source = (Smelly) extractField(smelly, "source")
		def target = (Smelly) extractField(smelly, "target")

		def sourceEntry = "<Source " + Strings.substringAfter(toXmlEntry(source), " ")
		def targetEntry = "<Target " + Strings.substringAfter(toXmlEntry(target), " ")
		return "<$name>$sourceEntry$targetEntry</$name>"
	}

	private static Object extractField(Smelly smelly, String fieldName) {
		smelly.class.getDeclaredField(fieldName).with {
			setAccessible(true)
			get(smelly)
		}
	}

	private static String handleLongMethodDelegates(Smelly smelly) {

		def name = smelly.class.simpleName
		def longMethod = extractField(smelly, "longMethod")

		String attributes = getAttributesFromFields(smelly)

		def base = toXmlEntry((Smelly) longMethod)
		def appendStart = "<$name " + Strings.substringAfter(base, " ")
		def appendEnd = Strings.substringBefore(appendStart, "/>") + "$attributes/>"
		return appendEnd
	}

	private static String getAttributesFromFields(Smelly smelly) {
		List<Field> fields = extractFields(smelly)
		def entries = joinFieldsToNameValueMap(fields, smelly)
		joinToXmlAttribute(entries)
	}

	private static String toXmlEntry(Smelly smelly) {

		def name = smelly.class.simpleName
		def path = extractPath(smelly)
		def range = extractSourceRange(smelly)

		List<Field> fields = extractFields(smelly)
		def entries = joinFieldsToNameValueMap(fields, smelly)
		entries << path << range

		return "<$name " + joinToXmlAttribute(entries) + "/>"
	}

	private static List<Field> extractFields(Smelly smelly) {
		Arrays.stream(smelly.class.declaredFields)
				.filter { !it.synthetic }
				.filter { !it.name.equals("sourceRange") }
				.filter { !it.name.equals("sourcePath") }
				.filter { !it.name.equals("longMethod") }
				.collect()
	}

	private static Map<String, String> joinFieldsToNameValueMap(List<Field> fields, smelly) {
		fields.collectEntries() {
			it.setAccessible(true)
			[it.name, it.get(smelly).toString()]
		}
	}

	private static String joinToXmlAttribute(Map<String, String> entries) {
		String content = ""
		entries.each { key, value ->
			content += """$key="$value" """
		}
		content
	}

	private static LinkedHashMap<String, String> extractSourceRange(Smelly smelly) {
		smelly.class.getDeclaredField("sourceRange").with {
			setAccessible(true)
			def pos = get(smelly).toString().split(',')
			["startLine": pos[0], "startColumn": pos[1], "endLine": pos[2], "endColumn": pos[3]]
		}
	}

	private static LinkedHashMap<String, String> extractPath(Smelly smelly) {
		smelly.class.getDeclaredField("sourcePath").with {
			setAccessible(true)
			["path": get(smelly).toString()]
		}
	}
}
