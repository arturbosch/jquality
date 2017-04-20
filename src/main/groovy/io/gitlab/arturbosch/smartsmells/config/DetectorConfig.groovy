package io.gitlab.arturbosch.smartsmells.config

import org.yaml.snakeyaml.Yaml

import java.nio.file.Files
import java.nio.file.Path

/**
 * @author artur
 */
class DetectorConfig {

	final Map<String, Map<String, String>> values

	DetectorConfig(Map<String, Map<String, String>> values) {
		this.values = values
	}

	@SuppressWarnings("unchecked")
	static DetectorConfig load(Path path) {
		InputStream input = Files.newInputStream(path)
		Yaml yaml = new Yaml()
		Map data = yaml.loadAs(input, Map.class)
		return new DetectorConfig(data)
	}

	static DetectorConfig loadFromString(String config) {
		return new DetectorConfig(new Yaml().loadAs(config, Map.class))
	}

	static String save(Map<String, Map<String, String>> values) {
		def yaml = new Yaml()
		return yaml.dump(values)
	}

	Map<String, String> getKey(String key) {
		return values.getOrDefault(key, new HashMap<>())
	}
}
