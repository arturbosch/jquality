package com.gitlab.artismarti.smartsmells.config

import com.gitlab.artismarti.smartsmells.common.Defaults
import com.gitlab.artismarti.smartsmells.common.Detector
import com.gitlab.artismarti.smartsmells.dataclass.DataClassDetector
import com.gitlab.artismarti.smartsmells.featureenvy.FeatureEnvyDetector
import com.gitlab.artismarti.smartsmells.featureenvy.FeatureEnvyFactor
import com.gitlab.artismarti.smartsmells.godclass.GodClassDetector
import com.gitlab.artismarti.smartsmells.longmethod.LongMethodDetector
import com.gitlab.artismarti.smartsmells.longparam.LongParameterListDetector

import java.util.function.Supplier

import static com.gitlab.artismarti.smartsmells.util.Numbers.toDouble
import static com.gitlab.artismarti.smartsmells.util.Numbers.toInt

/**
 * @author artur
 */
class DetectorInitializer {

	private static final String GOD_CLASS = "godclass";
	private static final String WMC = "wmc";
	private static final String ATFD = "atfd";
	private static final String TCC = "tcc";

	private static final String ACTIVE = "active";
	private static final String THRESHOLD = "featureEnvyFactor";

	private static final String LONG_METHOD = "longmethod";
	private static final String LONG_PARAM = "longparameterlist";
	private static final String DATA_CLASS = "dataclass";

	private static final String FEATURE_ENVY = "featureenvy";
	private static final String WEIGHT = "weight";
	private static final String BASE = "base";

	private final DetectorConfig detectorConfig;
	private final ArrayList<Detector> detectors;

	private DetectorInitializer(DetectorConfig config) {
		this.detectorConfig = config;
		this.detectors = new ArrayList<>();
	}

	static List<Detector> init(DetectorConfig config) {

		DetectorInitializer initializer = new DetectorInitializer(config);
		initializer.initGodClass();
		initializer.initLongMethod();
		initializer.initLongParameterList();
		initializer.initDataClass();
		initializer.initFeatureEnvy();

		return initializer.detectors;
	}

	private void initGodClass() {
		Map<String, String> config = detectorConfig.getKey(GOD_CLASS);
		if (isActive(config)) {
			detectors.add(new GodClassDetector(
					toInt(config.get(WMC), Defaults.WEIGHTED_METHOD_COUNT),
					toInt(config.get(ATFD), Defaults.ACCESS_TO_FOREIGN_DATA),
					toDouble(config.get(TCC), Defaults.TIED_CLASS_COHESION)));
		}
	}

	private void initLongMethod() {
		Map<String, String> config = detectorConfig.getKey(LONG_METHOD);
		if (isActive(config)) {
			detectors.add(new LongMethodDetector(
					toInt(config.get(THRESHOLD), Defaults.LONG_METHOD)));
		}
	}

	private void initLongParameterList() {
		Map<String, String> config = detectorConfig.getKey(LONG_PARAM);
		if (isActive(config)) {
			detectors.add(new LongParameterListDetector(
					toInt(config.get(THRESHOLD), Defaults.LONG_PARAMETER_LIST)));
		}
	}

	private void initFeatureEnvy() {
		Map<String, String> config = detectorConfig.getKey(FEATURE_ENVY);
		if (isActive(config)) {
			detectors.add(new FeatureEnvyDetector(FeatureEnvyFactor.newInstance(
					toDouble(config.get(THRESHOLD), Defaults.FEATURE_ENVY_FACTOR),
					toDouble(config.get(BASE), Defaults.FEATURE_ENVY_BASE),
					toDouble(config.get(WEIGHT), Defaults.FEATURE_ENVY_WEIGHT))));
		}
	}

	private void initDataClass() {
		initDefault(DATA_CLASS, { new DataClassDetector() });
	}

	private void initDefault(String key, Supplier<Detector> supplier) {
		Map<String, String> config = detectorConfig.getKey(key);
		if (isActive(config)) {
			detectors.add(supplier.get());
		}
	}

	private static boolean isActive(final Map<String, String> config) {
		return config != null && !config.isEmpty() && "true".equals(config.get(ACTIVE));
	}
}
