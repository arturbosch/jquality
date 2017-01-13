package io.gitlab.arturbosch.smartsmells.metrics

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import io.gitlab.arturbosch.jpal.ast.ClassHelper
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.common.Visitor

/**
 * @author artur
 */
class ClassInfoVisitor extends Visitor<ClassInfo> {

	private boolean skipCC_CM

	ClassInfoVisitor(boolean skipCC_CM) {
		this.skipCC_CM = skipCC_CM
	}

	@Override
	void visit(CompilationUnit n, Resolver resolver) {

		def classes = n.getNodesByType(ClassOrInterfaceDeclaration.class)

		classes.each {

			def methods = it.getNodesByType(MethodDeclaration.class)
			int methodCount = methods.isEmpty() ? 1 : methods.size()


			def methodSizes = methods.stream().map { Optional.ofNullable(it.body) }.filter { it.isPresent() }
					.map { it.get() }.mapToInt() { it.map { it.statements.size() }.orElse(0) }
			def amlSum = methodSizes.sum()
			def aml = amlSum / methodCount
			def sml = Math.sqrt(methods.stream().map { Optional.ofNullable(it.body) }.filter { it.isPresent() }
					.map { it.get() }.mapToInt { it.map { it.statements.size() }.orElse(0) }
					.mapToDouble { Math.pow(it - amlSum, 2) }.sum() / methodCount)

			def aplSum = methods.stream().mapToInt { it.parameters.size() }.sum()
			def apl = aplSum / methodCount
			def spl = Math.sqrt(methods.stream().mapToInt { it.parameters.size() }
					.mapToDouble { Math.pow(it - aplSum, 2) }.sum() / methodCount)

			def cc = -1
			def cm = -1
			if (!skipCC_CM) {
				cc = Metrics.cc(it, resolver)
				cm = Metrics.cm(it, resolver)
			}

			smells.add(new ClassInfo(
					name: it.name,
					signature: ClassHelper.createFullSignature(it),
					sourcePath: SourcePath.of(path),
					sourceRange: SourceRange.fromNode(it),
					wmc: Metrics.wmc(it),
					tcc: Metrics.tcc(it),
					atfd: Metrics.atfd(it),
					noa: Metrics.noa(it),
					nom: Metrics.nom(it),
					loc: Metrics.loc(it, path),
					sloc: Metrics.sloc(it, path),
					mlm: aml,
					plm: apl,
					mld: sml,
					pld: spl,
					cc: cc,
					cm: cm
			))
		}

	}
}
