package com.gitlab.artismarti.smartsmells.godclass

import com.github.javaparser.ASTHelper
import com.github.javaparser.ast.body.*
import com.github.javaparser.ast.expr.FieldAccessExpr
import com.github.javaparser.ast.expr.MethodCallExpr
import com.gitlab.artismarti.smartsmells.common.MethodHelper
import com.gitlab.artismarti.smartsmells.common.Visitor
import com.gitlab.artismarti.smartsmells.domain.SourcePath
import com.gitlab.artismarti.smartsmells.domain.SourcePosition
import com.gitlab.artismarti.smartsmells.domain.SourceRange

import java.nio.file.Path

/**
 * GodClasses := ((ATFD, TopValues(20%)) ∧ (ATFD, HigherThan(4))) ∧
 * ((WMC, HigherThan(20)) ∨ (TCC, LowerThan(0.33)).
 *
 * Metric proposed by:
 * R. Marinescu, Measurement and quality in object-oriented design, Ph.D. thesis in
 * the Faculty of Automatics and Computer Science of the Politehnica University of
 * Timisoara, 2003.
 *
 * @author artur
 */
class GodClassVisitor extends Visitor<GodClass> {

	private int accessToForeignDataThreshold
	private int weightedMethodCountThreshold
	private BigDecimal tiedClassCohesionThreshold

	private int atfd = 0
	private int wmc = 0
	private double tcc = 0.0

	private String className
	private List<String> fields = new ArrayList<>()
	private List<String> methods = new ArrayList<>()
	private Map<String, Set<String>> methodFieldAccesses = new HashMap<>()
	private List<String> publicMethods = new ArrayList<>()

	GodClassVisitor(int accessToForeignDataThreshold,
					int weightedMethodCountThreshold,
					double tiedClassCohesionThreshold,
					Path path) {
		super(path)
		this.accessToForeignDataThreshold = accessToForeignDataThreshold
		this.weightedMethodCountThreshold = weightedMethodCountThreshold
		this.tiedClassCohesionThreshold = tiedClassCohesionThreshold
	}

	@Override
	void visit(ClassOrInterfaceDeclaration n, Object arg) {
		this.className = n.name
		fields = getFieldNames(n)

		def methodNodes = ASTHelper.getNodesByType(n, MethodDeclaration.class)
		methods = methodNodes.collect({ it.name })

		publicMethods = methodNodes.stream()
				.filter({ ModifierSet.isPublic(it.modifiers) })
				.collect({ it.name })

		// traverse all nodes and calculate values before evaluate for god class
		super.visit(n, arg)
		tcc = TiedClassCohesion.calc(methodFieldAccesses)

		if (checkThresholds(tcc)) {
			addSmell(n)
		}
	}

	private static List<String> getFieldNames(ClassOrInterfaceDeclaration n) {
		ASTHelper.getNodesByType(n, FieldDeclaration.class)
				.collect({ it.variables })
				.collect({ it.id })
				.collect({ it.name })
				.flatten()
				.collect({ (String) it })
	}

	private boolean checkThresholds(BigDecimal tcc) {
		atfd > accessToForeignDataThreshold &&
				(wmc > weightedMethodCountThreshold ||
						tcc < tiedClassCohesionThreshold)
	}

	private boolean addSmell(ClassOrInterfaceDeclaration n) {
		smells.add(new GodClass(wmc, tcc, atfd, SourcePath.of(path),
				SourceRange.of(SourcePosition.of(n.beginLine, n.beginColumn),
						SourcePosition.of(n.endLine, n.endColumn))))
	}

	@Override
	void visit(ConstructorDeclaration n, Object arg) {
		wmc += MethodHelper.calcMcCabe(n)
		super.visit(n, arg)
	}

	@Override
	void visit(MethodDeclaration n, Object arg) {
		wmc += MethodHelper.calcMcCabe(n)

		if (publicMethods.contains(n.name)) {
			collectFieldAccesses(n)
		}

		super.visit(n, arg)
	}

	private void collectFieldAccesses(MethodDeclaration n) {
		def visitor = new FieldAccessVisitor()
		n.accept(visitor, null)

		def accessedFieldNames = visitor.fieldNames
		def methodName = n.name

		methodFieldAccesses.put(methodName, accessedFieldNames)
	}

	@Override
	void visit(FieldAccessExpr n, Object arg) {
		if (isNotMemberOfThisClass(n.field, fields)) {
			atfd++
		}
		super.visit(n, arg)
	}

	@Override
	void visit(MethodCallExpr n, Object arg) {
		if (isNotMemberOfThisClass(n.name, methods)) {
			if (isNotAGetterOrSetter(n.name)) {
				atfd++
			}
		}
		super.visit(n, arg)
	}

	private static boolean isNotMemberOfThisClass(String name, List<String> members) {
		!members.contains(name)
	}

	static boolean isNotAGetterOrSetter(String name) {
		!name.startsWith("get") && !name.startsWith("set") && !name.startsWith("is")
	}
}

