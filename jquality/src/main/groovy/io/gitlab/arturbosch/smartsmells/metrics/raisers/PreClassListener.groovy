package io.gitlab.arturbosch.smartsmells.metrics.raisers

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.FieldDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.ast.MethodHelper
import io.gitlab.arturbosch.jpal.ast.TypeHelper
import io.gitlab.arturbosch.jpal.core.CompilationInfo
import io.gitlab.arturbosch.jpal.resolution.QualifiedType
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.metrics.ClassInfo
import io.gitlab.arturbosch.smartsmells.metrics.FileInfo
import io.gitlab.arturbosch.smartsmells.metrics.MethodInfo
import io.gitlab.arturbosch.smartsmells.metrics.Metric
import io.gitlab.arturbosch.smartsmells.metrics.Metrics
import io.gitlab.arturbosch.smartsmells.metrics.internal.LinesOfCode
import io.gitlab.arturbosch.smartsmells.smells.shotgunsurgery.CMCCMetrics

import java.util.regex.Pattern
import java.util.stream.Collectors

/**
 * @author Artur Bosch
 */
@CompileStatic
trait PreClassListener {

	int priority() {
		return 0
	}

	abstract void raise(ClassOrInterfaceDeclaration aClass, CompilationInfo info, Resolver resolver)

	/**
	 * @return the class info for given class or null
	 */
	ClassInfo findClassInfo(ClassOrInterfaceDeclaration aClass, CompilationInfo info) {
		return info.getData(FileInfo.KEY)?.findClassByName(aClass.nameAsString)
	}
}

@CompileStatic
class CC_CM implements PreClassListener {

	static final String COUNT_CLASSES = "CountClasses"
	static final String COUNT_METHODS = "CountMethods"

	@Override
	void raise(ClassOrInterfaceDeclaration aClass, CompilationInfo info, Resolver resolver) {
		def pair = CMCCMetrics.raise(aClass, resolver)
		def (int cc, int cm) = [pair.a, pair.b]
		def classInfo = findClassInfo(aClass, info)
		classInfo?.addMetric(Metric.of(COUNT_CLASSES, cc))
		classInfo?.addMetric(Metric.of(COUNT_METHODS, cm))
	}
}

@CompileStatic
class TCC implements PreClassListener {

	static final String TIED_CLASS_COHESION = "TiedClassCohesion"

	@Override
	void raise(ClassOrInterfaceDeclaration aClass, CompilationInfo info, Resolver resolver) {
		findClassInfo(aClass, info)?.addMetric(Metric.of(TIED_CLASS_COHESION, Metrics.tcc(aClass)))
	}
}

@CompileStatic
class ATFD implements PreClassListener {

	static final String ACCESS_TO_FOREIGN_DATA = "AccessToForeignData"

	@Override
	void raise(ClassOrInterfaceDeclaration aClass, CompilationInfo info, Resolver resolver) {
		findClassInfo(aClass, info)?.addMetric(Metric.of(ACCESS_TO_FOREIGN_DATA, Metrics.atfd(aClass)))
	}
}

@CompileStatic
class LOC implements PreClassListener {

	static final String LOC = "LinesOfCode"
	static final String SLOC = "SourceLinesOfCode"
	static final String CLOC = "CommentsLinesOfCode"
	static final String LLOC = "LogicalLinesOfCode"
	static final String BLOC = "BlankLinesOfCode"

	static final Pattern NL = Pattern.compile("\n")

	@Override
	void raise(ClassOrInterfaceDeclaration aClass, CompilationInfo info, Resolver resolver) {
		def classInfo = findClassInfo(aClass, info)
		if (classInfo) {
			def content = NL.split(aClass.toString())
			def loc = new LinesOfCode()
			loc.analyze(content)
			[Metric.of(LOC, loc.source + loc.blank + loc.comment),
			 Metric.of(SLOC, loc.source),
			 Metric.of(CLOC, loc.comment),
			 Metric.of(LLOC, loc.logical),
			 Metric.of(BLOC, loc.blank)].each {
				classInfo.addMetric(it)
			}
		}
	}
}

@CompileStatic
class NOA implements PreClassListener {

	static final String NUMBER_OF_ATTRIBUTES = "NumberOfAttributes"
	static final String NUMBER_OF_PUBLIC_ATTRIBUTES = "NumberOfPublicAttributes"

	@Override
	void raise(ClassOrInterfaceDeclaration aClass, CompilationInfo info, Resolver resolver) {
		def classInfo = findClassInfo(aClass, info)

		if (classInfo) {
			int noa = 0
			int nopa = 0
			for (FieldDeclaration field in aClass.fields) {
				if (!field.isStatic()) {
					def vars = field.variables.size()
					if (field.isPublic()) {
						nopa += vars
					}
					noa += vars
				}
			}
			classInfo.addMetric(Metric.of(NUMBER_OF_ATTRIBUTES, noa))
			classInfo.addMetric(Metric.of(NUMBER_OF_PUBLIC_ATTRIBUTES, nopa))
		}
	}
}

@CompileStatic
class NOM implements PreClassListener {

	static final String NUMBER_OF_METHODS = "NumberOfMethods"
	static final String NUMBER_OF_PUBLIC_METHODS = "NumberOfPublicMethods"

	@Override
	void raise(ClassOrInterfaceDeclaration aClass, CompilationInfo info, Resolver resolver) {
		def classInfo = findClassInfo(aClass, info)

		if (classInfo) {
			int noa = 0
			int nopa = 0
			for (MethodDeclaration method in aClass.methods) {
				if (!method.isStatic()) {
					if (method.isPublic()) {
						nopa += 1
					}
					noa += 1
				}
			}
			classInfo.addMetric(Metric.of(NUMBER_OF_METHODS, noa))
			classInfo.addMetric(Metric.of(NUMBER_OF_PUBLIC_METHODS, nopa))
		}
	}
}
