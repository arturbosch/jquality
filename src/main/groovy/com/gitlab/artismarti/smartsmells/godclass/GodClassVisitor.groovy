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
	private List<String> fields
	private List<String> methods
	private Map<String, Set<String>> methodFieldAccesses
	private List<String> publicMethods

	GodClassVisitor(int accessToForeignDataThreshold,
					int weightedMethodCountThreshold,
					double tiedClassCohesionThreshold,
					Path path) {
		super(path)
		this.accessToForeignDataThreshold = accessToForeignDataThreshold
		this.weightedMethodCountThreshold = weightedMethodCountThreshold
		this.tiedClassCohesionThreshold = tiedClassCohesionThreshold
		this.methodFieldAccesses = new HashMap<>()
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
		tcc = calculateTcc()

		println "WMC: $wmc"
		println "ATFD: $atfd"
		println "TCC: $tcc"

		if (checkThresholds(tcc)) {
			addSmell(n)
		}
	}

	private boolean checkThresholds(BigDecimal tcc) {
		atfd > accessToForeignDataThreshold &&
				(wmc > weightedMethodCountThreshold ||
						tcc > tiedClassCohesionThreshold)
	}

	private boolean addSmell(ClassOrInterfaceDeclaration n) {
		smells.add(new GodClass(wmc, tcc, atfd, SourcePath.of(path),
				SourceRange.of(SourcePosition.of(n.beginLine, n.beginColumn),
						SourcePosition.of(n.endLine, n.endColumn))))
	}

	private double calculateTcc() {
		double tcc = new BigDecimal("0.0");
		double methodPairs = determineMethodPairs();
		println "method pairs: $methodPairs"
		double totalMethodPairs = calculateTotalMethodPairs();
		println "total method pairs: $totalMethodPairs"
		if (totalMethodPairs.compareTo(BigDecimal.ZERO)) {
			tcc = methodPairs / totalMethodPairs;
		}
		return tcc;
	}

	double determineMethodPairs() {
		def methods = methodFieldAccesses.keySet().toList();
		def methodCount = methods.size();
		def pairs = 0.0;
		if (methodCount > 1) {
			for (int i = 0; i < methodCount; i++) {
				for (int j = i + 1; j < methodCount; j++) {
					String firstMethodName = methods.get(i);
					String secondMethodName = methods.get(j);
					Set<String> accessesOfFirstMethod = methodFieldAccesses.get(firstMethodName);
					Set<String> accessesOfSecondMethod = methodFieldAccesses.get(secondMethodName);
					Set<String> combinedAccesses = new HashSet<String>();
					combinedAccesses.addAll(accessesOfFirstMethod);
					combinedAccesses.addAll(accessesOfSecondMethod);
					if (combinedAccesses.size() < (accessesOfFirstMethod.size() + accessesOfSecondMethod.size())) {
						pairs++;
					}
				}
			}
		}
		return pairs;
	}

	double calculateTotalMethodPairs() {
		int n = methodFieldAccesses.size();
		return n * (n - 1) / 2.0;
	}

	private static List<String> getFieldNames(ClassOrInterfaceDeclaration n) {
		ASTHelper.getNodesByType(n, FieldDeclaration.class)
				.collect({ it.variables })
				.collect({ it.id })
				.collect({ it.name })
				.flatten()
				.collect({ (String) it })
	}

	@Override
	void visit(ConstructorDeclaration n, Object arg) {
		wmc += MethodHelper.calcMcCabe(n)
		super.visit(n, arg)
	}

	@Override
	void visit(MethodDeclaration n, Object arg) {
		wmc += MethodHelper.calcMcCabe(n)

		def visitor = new FieldAccessVisitor()
		n.accept(visitor, null)

		def accessedFieldNames = visitor.fieldNames
		def methodName = n.name

		methodFieldAccesses.put(methodName, accessedFieldNames)

		super.visit(n, arg)
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

