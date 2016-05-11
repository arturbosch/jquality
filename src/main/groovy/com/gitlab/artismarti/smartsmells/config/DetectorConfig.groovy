package com.gitlab.artismarti.smartsmells.config

import org.yaml.snakeyaml.Yaml

import java.nio.file.Files
import java.nio.file.Path

/**
 * @author artur
 */
class DetectorConfig {

	private final Map<String, Map<String, String>> values

	private DetectorConfig(Map<String, Map<String, String>> values) {
		this.values = values
	}

	@SuppressWarnings("unchecked")
	static DetectorConfig load(Path path) {
		InputStream input = Files.newInputStream(path)
		Yaml yaml = new Yaml()
		Map data = yaml.loadAs(input, Map.class)
		return new DetectorConfig(data)
	}

	Map<String, String> getKey(String key) {
		return values.getOrDefault(key, new HashMap<>())
	}
}
