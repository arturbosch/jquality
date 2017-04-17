package io.gitlab.arturbosch.smartsmells.out

import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.smartsmells.api.SmellResult
import io.gitlab.arturbosch.smartsmells.config.Smell
import io.gitlab.arturbosch.smartsmells.metrics.Metric
import io.gitlab.arturbosch.smartsmells.smells.DetectionResult
import io.gitlab.arturbosch.smartsmells.util.Strings

import java.lang.reflect.Field

/**
 * @author artur
 */
class XMLWriter {

	static String toXml(SmellResult smellResult) {
		List<String> entries = new ArrayList<>()
		smellResult.smellSets.each { key, value ->
			entries.addAll(value.stream()
					.filter { it != null }
					.collect { toXml(key, it) })
		}

		return "<SmartSmells>\n" + entries.join("\n") + "\n</SmartSmells>"
	}

	static String toXml(Smell key, DetectionResult it) {
		if (key == Smell.CYCLE) {
			"\t" + handleDependencyDelegates(it)
		} else if (key == Smell.CLASS_INFO) {
			"\t" + handleMetricDelegates(it)
		} else {
			"\t" + toXmlEntry(it)
		}
	}

	static String handleMetricDelegates(DetectionResult smelly) {
		def name = smelly.class.simpleName
		def metrics = (List<Metric>) extractField(smelly, "metrics")
		def xmlMetrics = metrics.collect { toXmlEntry(it) }.join("\n\t\t")
		def xml = toXmlEntry(smelly)
		return Strings.substringBefore(xml, "/>") + ">\n\t\t$xmlMetrics\n\t</$name>"
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

	static String toXmlEntry(Object object) {
		def name = object.class.simpleName

		List<Field> fields = extractFields(object)
		def entries = joinFieldsToNameValueMap(fields, object)

		return "<$name " + joinToXmlAttribute(entries) + "/>"
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

	private static List<Field> extractFields(Object smelly) {
		Arrays.stream(smelly.class.declaredFields)
				.filter { !it.synthetic }
				.filter { it.name != "sourceRange" }
				.filter { it.name != "sourcePath" }
				.filter { it.name != "metrics" }
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
			def pos = get(smelly).toString()
					.replace("SourceRange(", "")
					.replace(")", "")
					.split(',')
					.collect { it.trim() }
			["startLine": pos[0], "startColumn": pos[1], "endLine": pos[2], "endColumn": pos[3]]
		}
	}

	private static LinkedHashMap<String, String> extractPath(DetectionResult smelly) {
		smelly.class.getDeclaredField("sourcePath").with {
			setAccessible(true)
			["path": (get(smelly) as SourcePath).relativePath]
		}
	}
}
