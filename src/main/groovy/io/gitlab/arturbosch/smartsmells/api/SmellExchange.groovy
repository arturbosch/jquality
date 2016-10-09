package io.gitlab.arturbosch.smartsmells.api

import io.gitlab.arturbosch.smartsmells.common.DetectionResult
import io.gitlab.arturbosch.smartsmells.config.Smell
import io.gitlab.arturbosch.smartsmells.smells.comment.CommentSmell
import io.gitlab.arturbosch.smartsmells.smells.complexmethod.ComplexMethod
import io.gitlab.arturbosch.smartsmells.smells.cycle.Cycle
import io.gitlab.arturbosch.smartsmells.smells.dataclass.DataClass
import io.gitlab.arturbosch.smartsmells.smells.deadcode.DeadCode
import io.gitlab.arturbosch.smartsmells.smells.featureenvy.FeatureEnvy
import io.gitlab.arturbosch.smartsmells.smells.godclass.GodClass
import io.gitlab.arturbosch.smartsmells.smells.largeclass.LargeClass
import io.gitlab.arturbosch.smartsmells.smells.longmethod.LongMethod
import io.gitlab.arturbosch.smartsmells.smells.longparam.LongParameterList
import io.gitlab.arturbosch.smartsmells.smells.messagechain.MessageChain
import io.gitlab.arturbosch.smartsmells.smells.middleman.MiddleMan

/**
 * @author artur
 */
final class SmellExchange {

	static Object getAttribute(DetectionResult smelly, String name) {
		return smelly.class.getDeclaredField(name).with {
			setAccessible(true)
			get(smelly)
		}
	}

	static List<CommentSmell> allComments(SmellResult result) {
		return result.of(Smell.COMMENT).toList() as List<CommentSmell>
	}

	static List<ComplexMethod> allComplexMethods(SmellResult result) {
		return result.of(Smell.COMPLEX_METHOD).toList() as List<ComplexMethod>
	}

	static List<Cycle> allCycles(SmellResult result) {
		return result.of(Smell.CYCLE).toList() as List<Cycle>
	}

	static List<DataClass> allDataClasses(SmellResult result) {
		return result.of(Smell.DATA_CLASS).toList() as List<DataClass>
	}

	static List<DeadCode> allDeadcodes(SmellResult result) {
		return result.of(Smell.DEAD_CODE).toList() as List<DeadCode>
	}

	static List<FeatureEnvy> allFeatureEnvies(SmellResult result) {
		return result.of(Smell.FEATURE_ENVY).toList() as List<FeatureEnvy>
	}

	static List<GodClass> allGodClasses(SmellResult result) {
		return result.of(Smell.GOD_CLASS).toList() as List<GodClass>
	}

	static List<LargeClass> allLargeClasses(SmellResult result) {
		return result.of(Smell.LARGE_CLASS).toList() as List<LargeClass>
	}

	static List<LongMethod> allLongMethods(SmellResult result) {
		return result.of(Smell.LONG_METHOD).toList() as List<LongMethod>
	}

	static List<LongParameterList> allLongParams(SmellResult result) {
		return result.of(Smell.LONG_PARAM).toList() as List<LongParameterList>
	}

	static List<MessageChain> allMessageChains(SmellResult result) {
		return result.of(Smell.MESSAGE_CHAIN).toList() as List<MessageChain>
	}

	static List<MiddleMan> allMiddleMans(SmellResult result) {
		return result.of(Smell.MIDDLE_MAN).toList() as List<MiddleMan>
	}

}
