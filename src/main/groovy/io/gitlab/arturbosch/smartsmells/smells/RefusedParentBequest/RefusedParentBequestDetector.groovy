package io.gitlab.arturbosch.smartsmells.smells.RefusedParentBequest

import com.github.javaparser.ast.body.BodyDeclaration
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.ConstructorDeclaration
import com.github.javaparser.ast.body.FieldDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.body.TypeDeclaration
import com.github.javaparser.ast.expr.FieldAccessExpr
import com.github.javaparser.ast.expr.MethodCallExpr
import com.github.javaparser.ast.expr.ThisExpr
import com.github.javaparser.ast.nodeTypes.modifiers.NodeWithProtectedModifier
import groovy.transform.Canonical
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.ast.ClassHelper
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.jpal.core.CompilationInfo
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.api.Detector
import io.gitlab.arturbosch.smartsmells.common.Visitor
import io.gitlab.arturbosch.smartsmells.config.Smell
import io.gitlab.arturbosch.smartsmells.metrics.Metrics
import io.gitlab.arturbosch.smartsmells.smells.ElementTarget

/**
 *
 * (((NProtM > Few) AND (BUR < A Third)) OR (BOvR < A Third)) AND
 * (((AMW > AVerage) OR (WMC > Average)) AND (NOM > Average))
 *
 * @author Artur Bosch
 */
@CompileStatic
class RefusedParentBequestDetector extends Detector<RefusedParentBequest> {

	private final RPBConfig config

	RefusedParentBequestDetector(RPBConfig config) {
		this.config = config
	}

	@Override
	protected Visitor<RefusedParentBequest> getVisitor() {
		return new RefusedParentBequestVisitor(config)
	}

	@Override
	Smell getType() {
		return Smell.REFUSED_PARENT_BEQUEST
	}
}

@Canonical
class RPBConfig {
	int NProtM = 3
	double BUR = 0.33
	double BOvR = 0.33
	double AMW = 2
	int WMC = 14
	int NOM = 7
}

@CompileStatic
class RefusedParentBequestVisitor extends Visitor<RefusedParentBequest> {

	private final RPBConfig config

	RefusedParentBequestVisitor(RPBConfig config) {
		this.config = config
	}

	@Override
	void visit(ClassOrInterfaceDeclaration n, Resolver resolver) {
		if (n.interface) {
			return
		}

		def extendedTypes = n.extendedTypes
		if (extendedTypes.size() != 1) {
			return // a class must have one super class
		}

		def superClass = extendedTypes[0]
		def qualifiedSuperType = resolver.resolveType(superClass, info) // is super class in code base?
		resolver.find(qualifiedSuperType).ifPresent { CompilationInfo superInfo ->
			superInfo.getTypeDeclarationByQualifier(qualifiedSuperType).ifPresent { TypeDeclaration superDecl ->
				raiseMetrics(n, superDecl)
			}
		}

		super.visit(n, resolver)
	}

	private void raiseMetrics(ClassOrInterfaceDeclaration n, TypeDeclaration superDecl) {
		Collection<BodyDeclaration> protSuperMems = protectedMembers(superDecl)
		Collection<MethodDeclaration> protSuperMethods = protSuperMems.stream()
				.filter { it instanceof MethodDeclaration }
				.collect()
		Collection<FieldDeclaration> protSuperFields = protSuperMems.stream()
				.filter { it instanceof FieldDeclaration }
				.collect()

		Set<String> protSuperMemberNames = (protSuperMethods.collect { it.nameAsString } + protSuperFields
				.stream()
				.flatMap { it.variables.stream() }
				.map { it.nameAsString }
				.collect()
		).toSet()

		Collection<String> superMethodDeclString = protSuperMethods.collect { it.declarationAsString }
		Collection<BodyDeclaration> protMembers = protectedMembers(n)

		Collection<String> overriddenMethods = protMembers.stream()
				.filter { it instanceof MethodDeclaration }
				.map { it as MethodDeclaration }
				.filter { it.annotations.find { it.nameAsString == "Override" } != null }
				.map { it.declarationAsString }
				.collect()

		def overriddenFromSuper = superMethodDeclString.intersect(overriddenMethods).size()

		Collection<String> usedFields = n.getChildNodesByType(FieldAccessExpr.class).stream()
				.filter { ClassHelper.inClassScope(it, n.nameAsString) }
				.filter { it.scope == null || it.scope instanceof ThisExpr }
				.map { it.nameAsString }
				.collect()

		Collection<String> usedMethods = n.getChildNodesByType(MethodCallExpr.class).stream()
				.filter { ClassHelper.inClassScope(it, n.nameAsString) }
				.filter { !it.scope.isPresent() || it.scope.get() instanceof ThisExpr }
				.map { it.nameAsString }
				.collect()

		def usedFromSuper = protSuperMemberNames.intersect(usedFields + usedMethods).size()

		def bur = (double) usedFromSuper / protSuperMemberNames.size()
		def bovr = (double) overriddenFromSuper / superMethodDeclString.size()
		def noProtMembers = protMembers.size()
		def nom = n.methods.size()
		def wmc = Metrics.wmc(n)
		def amw = (double) wmc / nom

		println(new RPBConfig(noProtMembers, bur, bovr, amw, wmc, nom))

		if ((bovr < config.BOvR || (noProtMembers > config.NProtM && bur < config.BUR)) &&
				((amw > config.AMW || wmc > config.WMC) && nom > config.NOM)) {
			report(new RefusedParentBequest(n.nameAsString, ClassHelper.createFullSignature(n), SourceRange
					.fromNode(n), SourcePath.of(info), ElementTarget.CLASS))
		}
	}

	private static Collection<BodyDeclaration> protectedMembers(TypeDeclaration decl) {
		return decl.members.stream()
				.filter { it instanceof NodeWithProtectedModifier }
				.filter { !(it instanceof ConstructorDeclaration) }
				.collect()
	}
}
