package io.gitlab.arturbosch.smartsmells.out

import io.gitlab.arturbosch.smartsmells.api.SmellResult
import io.gitlab.arturbosch.smartsmells.common.DetectionResult
import io.gitlab.arturbosch.smartsmells.config.Smell
import io.gitlab.arturbosch.smartsmells.util.Strings

import java.lang.reflect.Field

/**
 * @author artur
 */
class XMLWriter {

	static String toXml(SmellResult smellResult) {

		List<String> entries = new ArrayList<>()
		smellResult.smellSets.each { key, value ->
			entries.addAll(value.stream().filter { it != null }.collect {
				println it
				if (key == Smell.COMPLEX_METHOD || key == Smell.LONG_PARAM) {
					"\t" + handleLongMethodDelegates(it)
				} else if (key == Smell.CYCLE) {
					"\t" + handleDependencyDelegates(it)
				} else {
					"\t" + toXmlEntry(it)
				}
			})
		}

		return "<SmartSmells>\n" + entries.join("\n") + "\n</SmartSmells>"
	}

	private static String handleDependencyDelegates(DetectionResult smelly) {
		def name = smelly.class.simpleName
		def source = (DetectionResult) extractField(smelly, "source")
		def target = (DetectionResult) extractField(smelly, "target")

		def sourceEntry = "<Source " + Strings.substringAfter(toXmlEntry(source), " ")
		def targetEntry = "<Target " + Strings.substringAfter(toXmlEntry(target), " ")
		return "<$name>$sourceEntry$targetEntry</$name>"
	}

	private static Object extractField(DetectionResult smelly, String fieldName) {
		smelly.class.getDeclaredField(fieldName).with {
			setAccessible(true)
			get(smelly)
		}
	}

	private static String handleLongMethodDelegates(DetectionResult smelly) {

		def name = smelly.class.simpleName
		def longMethod = extractField(smelly, "longMethod")

		String attributes = getAttributesFromFields(smelly)
		def base = toXmlEntry(longMethod as DetectionResult)
		def appendStart = "<$name " + Strings.substringAfter(base, " ")
		def appendEnd = Strings.substringBefore(appendStart, "/>") + "$attributes/>"
		return appendEnd
	}

	private static String getAttributesFromFields(DetectionResult smelly) {
		List<Field> fields = extractFields(smelly)
		def entries = joinFieldsToNameValueMap(fields, smelly)
		joinToXmlAttribute(entries)
	}

	static String toXmlEntry(DetectionResult smelly) {

		def name = smelly.class.simpleName
		def path = extractPath(smelly)
		def range = extractSourceRange(smelly)

		List<Field> fields = extractFields(smelly)
		def entries = joinFieldsToNameValueMap(fields, smelly)
		entries << path << range

		return "<$name " + joinToXmlAttribute(entries) + "/>"
	}

	private static List<Field> extractFields(DetectionResult smelly) {
		Arrays.stream(smelly.class.declaredFields)
				.filter { !it.synthetic }
				.filter { it.name != "sourceRange" }
				.filter { it.name != "sourcePath" }
				.filter { it.name != "longMethod" }
				.collect()
	}

	private static Map<String, String> joinFieldsToNameValueMap(List<Field> fields, smelly) {
		fields.collectEntries() {
			it.setAccessible(true)
			[it.name, replaceXmlSymbols(it.get(smelly).toString())]
		}
	}

	private static String replaceXmlSymbols(String value) {
		return value.replaceAll("&", "&amp;")
				.replaceAll("\"", "&quot;")
				.replaceAll("\'", "&apos;")
				.replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;")
	}

	private static String joinToXmlAttribute(Map<String, String> entries) {
		String content = ""
		entries.each { key, value ->
			content += """$key="$value" """
		}
		content
	}

	private static LinkedHashMap<String, String> extractSourceRange(DetectionResult smelly) {
		smelly.class.getDeclaredField("sourceRange").with {
			setAccessible(true)
			def pos = get(smelly).toString().replace("SourceRange(", "").replace(")", "").split(',')
			["startLine": pos[0], "startColumn": pos[1], "endLine": pos[2], "endColumn": pos[3]]
		}
	}

	private static LinkedHashMap<String, String> extractPath(DetectionResult smelly) {
		smelly.class.getDeclaredField("sourcePath").with {
			setAccessible(true)
			["path": get(smelly).toString()]
		}
	}
}
