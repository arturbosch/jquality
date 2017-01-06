package io.gitlab.arturbosch.smartsmells.config

import io.gitlab.arturbosch.smartsmells.common.Detector
import io.gitlab.arturbosch.smartsmells.metrics.ClassInfoDetector
import io.gitlab.arturbosch.smartsmells.smells.comment.CommentDetector
import io.gitlab.arturbosch.smartsmells.smells.complexmethod.ComplexMethodDetector
import io.gitlab.arturbosch.smartsmells.smells.cycle.CycleDetector
import io.gitlab.arturbosch.smartsmells.smells.dataclass.DataClassDetector
import io.gitlab.arturbosch.smartsmells.smells.deadcode.DeadCodeDetector
import io.gitlab.arturbosch.smartsmells.smells.featureenvy.FeatureEnvyDetector
import io.gitlab.arturbosch.smartsmells.smells.featureenvy.FeatureEnvyFactor
import io.gitlab.arturbosch.smartsmells.smells.godclass.GodClassDetector
import io.gitlab.arturbosch.smartsmells.smells.largeclass.LargeClassDetector
import io.gitlab.arturbosch.smartsmells.smells.longmethod.LongMethodDetector
import io.gitlab.arturbosch.smartsmells.smells.longparam.LongParameterListDetector
import io.gitlab.arturbosch.smartsmells.smells.messagechain.MessageChainDetector
import io.gitlab.arturbosch.smartsmells.smells.middleman.MiddleManDetector
import io.gitlab.arturbosch.smartsmells.smells.middleman.MiddleManVisitor
import io.gitlab.arturbosch.smartsmells.smells.shotgunsurgery.ShotgunSurgeryDetector
import io.gitlab.arturbosch.smartsmells.util.Strings

import java.util.function.Function

import static io.gitlab.arturbosch.smartsmells.util.Numbers.toDouble
import static io.gitlab.arturbosch.smartsmells.util.Numbers.toInt

/**
 * @author artur
 */
enum Smell {

	CLASS_INFO{
		@Override
		Optional<Detector> initialize(DetectorConfig detectorConfig) {
			return initDefault(detectorConfig, Constants.CLASS_INFO, {
				new ClassInfoDetector(Strings.isTrue(it.get(Constants.SKIP_CC_CM)))
			})
		}
	},
	COMMENT{
		@Override
		Optional<Detector> initialize(DetectorConfig detectorConfig) {
			return initDefault(detectorConfig, Constants.COMMENT, { new CommentDetector() })
		}
	}, COMPLEX_METHOD{
		@Override
		Optional<Detector> initialize(DetectorConfig detectorConfig) {
			return initDefault(detectorConfig, Constants.COMPLEX_METHOD, {
				new ComplexMethodDetector(toInt(it.get(Constants.THRESHOLD), Defaults.COMPLEX_METHOD))
			})
		}
	}, CYCLE{
		@Override
		Optional<Detector> initialize(DetectorConfig detectorConfig) {
			return initDefault(detectorConfig, Constants.CYCLE, { new CycleDetector() })
		}
	}, DATA_CLASS{
		@Override
		Optional<Detector> initialize(DetectorConfig detectorConfig) {
			return initDefault(detectorConfig, Constants.DATA_CLASS, { new DataClassDetector() })
		}
	}, DEAD_CODE{
		@Override
		Optional<Detector> initialize(DetectorConfig detectorConfig) {
			return initDefault(detectorConfig, Constants.DEAD_CODE, { new DeadCodeDetector() })
		}
	}, FEATURE_ENVY{
		@Override
		Optional<Detector> initialize(DetectorConfig detectorConfig) {
			initDefault(detectorConfig, Constants.FEATURE_ENVY,
					{
						new FeatureEnvyDetector(FeatureEnvyFactor.newInstance(
								toDouble(it.get(Constants.THRESHOLD), Defaults.FEATURE_ENVY_FACTOR),
								toDouble(it.get(Constants.BASE), Defaults.FEATURE_ENVY_BASE),
								toDouble(it.get(Constants.WEIGHT), Defaults.FEATURE_ENVY_WEIGHT)),
								Strings.isTrue(it.get(Constants.FEATURE_ENVY_IGNORE_STATIC)))
					})
		}

	}, GOD_CLASS{
		@Override
		Optional<Detector> initialize(DetectorConfig detectorConfig) {
			initDefault(detectorConfig, Constants.GOD_CLASS,
					{
						new GodClassDetector(
								toInt(it.get(Constants.WMC), Defaults.WEIGHTED_METHOD_COUNT),
								toInt(it.get(Constants.ATFD), Defaults.ACCESS_TO_FOREIGN_DATA),
								toDouble(it.get(Constants.TCC), Defaults.TIED_CLASS_COHESION))
					})
		}
	}, LARGE_CLASS{
		@Override
		Optional<Detector> initialize(DetectorConfig detectorConfig) {
			initDefault(detectorConfig, Constants.LARGE_CLASS,
					{
						new LargeClassDetector(
								toInt(it.get(Constants.THRESHOLD), Defaults.LARGE_CLASS))
					})
		}
	}, LONG_METHOD{
		@Override
		Optional<Detector> initialize(DetectorConfig detectorConfig) {
			initDefault(detectorConfig, Constants.LONG_METHOD,
					{
						new LongMethodDetector(
								toInt(it.get(Constants.THRESHOLD), Defaults.LONG_METHOD))
					})
		}
	}, LONG_PARAM{
		@Override
		Optional<Detector> initialize(DetectorConfig detectorConfig) {
			initDefault(detectorConfig, Constants.LONG_PARAM,
					{
						new LongParameterListDetector(
								toInt(it.get(Constants.THRESHOLD), Defaults.LONG_PARAMETER_LIST))
					})
		}
	}, MESSAGE_CHAIN{
		@Override
		Optional<Detector> initialize(DetectorConfig detectorConfig) {
			initDefault(detectorConfig, Constants.MESSAGE_CHAIN,
					{
						new MessageChainDetector(
								toInt(it.get(Constants.THRESHOLD), Defaults.CHAIN_SIZE))
					})
		}
	}, MIDDLE_MAN{
		@Override
		Optional<Detector> initialize(DetectorConfig detectorConfig) {
			initDefault(detectorConfig, Constants.MIDDLE_MAN,
					{
						new MiddleManDetector(
								MiddleManVisitor.MMT.valueOf(it.get(Constants.THRESHOLD)))
					})
		}
	}, SHOTGUN_SURGERY{

		@Override
		Optional<Detector> initialize(DetectorConfig detectorConfig) {
			initDefault(detectorConfig, Constants.SHOTGUN_SURGERY,
					{
						new ShotgunSurgeryDetector(
								toInt(it.get(Constants.CHANGING_CLASSES), Defaults.CHANGING_CLASSES),
								toInt(it.get(Constants.CHANGING_METHODS), Defaults.CHANGING_METHODS))
					})
		}
	}
	, UNKNOWN{
		@Override
		Optional<Detector> initialize(DetectorConfig detectorConfig) {
			return Optional.empty()
		}
	}

	abstract Optional<Detector> initialize(DetectorConfig detectorConfig)

	private static Optional<Detector> initDefault(DetectorConfig detectorConfig,
												  String key,
												  Function<Map<String, String>, Detector> supplier) {
		Map<String, String> config = detectorConfig.getKey(key)
		if (isActive(config)) {
			return Optional.of(supplier.apply(config))
		}
		Optional.empty()
	}

	private static boolean isActive(final Map<String, String> config) {
		return config != null && !config.isEmpty() && "true" == config.get(Constants.ACTIVE)
	}

}
