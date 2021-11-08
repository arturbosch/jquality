package io.gitlab.arturbosch.jpal.ast

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.PackageDeclaration
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.TypeDeclaration
import com.github.javaparser.ast.type.ClassOrInterfaceType
import com.github.javaparser.ast.type.Type
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import com.github.javaparser.utils.Pair
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.core.CompilationInfo
import io.gitlab.arturbosch.jpal.internal.Validate
import io.gitlab.arturbosch.jpal.resolution.QualifiedType
import io.gitlab.arturbosch.jpal.resolution.ResolutionData
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.jpal.resolution.nested.NoClassesException
import io.gitlab.arturbosch.jpal.resolution.solvers.TypeSolver

import java.util.regex.Pattern
import java.util.stream.Collectors

/**
 * Provides static methods to search for specific types.
 *
 * @author artur
 */
@CompileStatic
final class TypeHelper {

	public static final String DEFAULT_PACKAGE = "<default>"

	private TypeHelper() {}

	/**
	 * From a given type try to find it's class or interface type.
	 * This is a convenience method as javaparser often returns just the
	 * abstract {@code Type} which is often not helpful.
	 *
	 * {@code
	 *
	 * E.g. you have a FieldDeclaration field = ...
	 * 	Optional<ClassOrInterfaceType> maybeFullType = TypeHelper.getClassOrInterfaceType(field.getType())
	 *
	 *}
	 *
	 * @param type given type
	 * @return maybe a class or interface type
	 */
	static Optional<ClassOrInterfaceType> getClassOrInterfaceType(Type type) {
		if (type instanceof ClassOrInterfaceType) {
			return Optional.of(type as ClassOrInterfaceType)
		}
		return Optional.empty()
	}

	/**
	 * Fastest way to find the qualified type from a class or interface.
	 * As this method makes use of finding the compilation unit first and then
	 * asking the resolver for the type, this method may be really slow.
	 *
	 * Consider caching the compilation unit and use the resolver if performance
	 * is important.
	 *
	 * @param n class or interface declaration
	 * @return maybe the qualified type for given class/interface declaration as the
	 * searched compilation unit can be not found
	 */
	static Optional<QualifiedType> getQualifiedType(ClassOrInterfaceDeclaration n) {
		return NodeHelper.findDeclaringCompilationUnit(n).map { getQualifiedType(n, it) }
	}

	/**
	 * Convenient method to get the qualified type by providing the compilation unit
	 * and the type declaration.
	 * @param n type declaration
	 * @param unit compilation uni
	 * @return a qualified type - be aware it can be unknown
	 */
	static QualifiedType getQualifiedType(ClassOrInterfaceDeclaration n, CompilationUnit unit) {
		def name = n.nameAsString
		def holder = ResolutionData.of(unit)
		return new TypeSolver().getQualifiedType(holder, new ClassOrInterfaceType(name))
	}

	/**
	 * Convenient method to get the qualified type if you are sure the given class is inside the
	 * given package.
	 *
	 * @param n type declaration
	 * @param packageDeclaration package declaration
	 * @return a qualified type consisting of the package name and the type name - be aware it is handled
	 * as a reference type
	 */
	static QualifiedType getQualifiedTypeFromPackage(TypeDeclaration n, Optional<PackageDeclaration> packageDeclaration) {
		return new QualifiedType("${packageDeclaration.map { it.nameAsString }.orElse(DEFAULT_PACKAGE)}.$n.name",
				QualifiedType.TypeToken.REFERENCE)
	}

	/**
	 * Tests if given type is within given compilation unit.
	 *
	 * @param unit the compilation unit
	 * @param qualifiedType searched type
	 * @return true if found
	 */
	static boolean isTypePresentInCompilationUnit(CompilationUnit unit, QualifiedType qualifiedType) {
		Validate.notNull(unit)
		def shortName = Validate.notNull(qualifiedType).shortName()
		def types = unit.getChildNodesByType(ClassOrInterfaceType.class)
		return types.any { it.nameAsString == shortName }
	}

	/**
	 * Finds the qualified types of all inner classes within given compilation unit.
	 *
	 * @param unit compilation unit
	 * @return set of qualified types
	 */
	static Set<QualifiedType> getQualifiedTypesOfInnerClasses(CompilationUnit unit) {
		def types = unit.getTypes()
		if (types.size() >= 1) {
			TypeDeclaration mainClass = types[0]
			String packageName = unit.packageDeclaration.map { it.nameAsString }.orElse("")
			Set<String> innerClassesNames = NodeHelper.findNamesOfInnerClasses(mainClass)
			String outerClassName = mainClass.name
			return innerClassesNames.stream().map {
				new QualifiedType("$packageName.$outerClassName.$it", QualifiedType.TypeToken.REFERENCE)
			}.collect(Collectors.toSet())
		} else {
			return Collections.emptySet()
		}
	}

	static Pair<Pair<QualifiedType, TypeDeclaration>, Map<QualifiedType, TypeDeclaration>> getQualifiedDeclarationsOfInnerClasses(CompilationUnit unit) {
		def types = unit.getTypes()
		if (types.size() >= 1) {
			String packageName = unit.packageDeclaration.map { it.nameAsString }.orElse(DEFAULT_PACKAGE)

			Pair<QualifiedType, TypeDeclaration> firstTopLevel
			Map<QualifiedType, TypeDeclaration> otherTopLevelOrInnerDeclarations = [:]
			for (indexTypeDecl in types.withIndex()) {
				def currentType = qualifiedTypeForTopLevelTypeDeclaration(indexTypeDecl.first, packageName)
				def innerTypesMap = findInnerTypes(indexTypeDecl.first, currentType)
				if (indexTypeDecl.second == 0) { // first top level class is treated special by CompilationInfo
					firstTopLevel = new Pair<>(currentType, indexTypeDecl.first)
				} else {
					otherTopLevelOrInnerDeclarations.put(currentType, indexTypeDecl.first)
				}
				otherTopLevelOrInnerDeclarations.putAll(innerTypesMap)
			}
			if (firstTopLevel == null) throw new NoClassesException("No classes found inside this compilation unit: \n $unit")
			return new Pair<>(firstTopLevel, otherTopLevelOrInnerDeclarations)
		} else {
			throw new NoClassesException("No classes found inside this compilation unit: \n $unit")
		}
	}

	private
	static QualifiedType qualifiedTypeForTopLevelTypeDeclaration(TypeDeclaration mainClass, String packageName) {
		String mainClassName = mainClass.name
		return new QualifiedType("$packageName.$mainClassName", QualifiedType.TypeToken.REFERENCE)
	}

	private static Map<QualifiedType, TypeDeclaration> findInnerTypes(
			TypeDeclaration typeDecl,
			QualifiedType parentType) {
		def types = Validate.notNull(typeDecl).getChildNodesByType(TypeDeclaration.class)
		return types.stream().filter { it.parentNode instanceof Optional<TypeDeclaration> }
				.collect()
				.collectEntries {
			def declaration = it as TypeDeclaration
			def signature = ClassHelper.createFullSignature(declaration).replaceAll(DOLLAR, '.')
			def qualifiedType = new QualifiedType("$parentType.onlyPackageName.$signature", QualifiedType.TypeToken.REFERENCE)
			[qualifiedType, it]
		}
	}
	private final static Pattern DOLLAR = Pattern.compile("\\\$")

	/**
	 * Finds all used types which are used within this compilation unit.
	 * @param unit the compilation unit
	 * @param resolver use given type solver for qualified types or build one if null
	 * @return a set of qualified types
	 */
	static Set<QualifiedType> findAllUsedTypes(CompilationUnit unit, Resolver resolver = null) {
		def resolutionData = ResolutionData.of(unit)
		return new FindAllUsedTypesCollector().collect(unit).stream()
				.map { withOuterClasses(it) }
				.map { (resolver ? new Resolver(null) : resolver).resolveType(it, resolutionData) }
				.collect(Collectors.toSet())
	}

	static class FindAllUsedTypesCollector extends VoidVisitorAdapter {

		private Map<String, ClassOrInterfaceType> types = new HashMap<>()

		Collection<ClassOrInterfaceType> collect(CompilationUnit unit) {
			visit(unit, null)
			return types.values()
		}

		@Override
		void visit(ClassOrInterfaceType n, Object arg) {
			if (!types.containsKey(n.nameAsString)) types.put(n.nameAsString, n)
			super.visit(n, arg)
		}
	}

	private static ClassOrInterfaceType withOuterClasses(ClassOrInterfaceType type) {
		String fullType = type.nameAsString
		def current = type.scope
		while (current.isPresent()) {
			def presentType = current.get()
			fullType = "${presentType.nameAsString}.$fullType"
			current = presentType.scope
		}
		return new ClassOrInterfaceType(fullType)
	}

	/**
	 * Collects all qualified types of extended and implemented types starting from given class.
	 * A CompilationInfo for this class can be provided to decrease search cost .
	 *
	 * @param aClass the class to resolve ancestors for
	 * @param resolver the resolver
	 * @param info compilation info for the class
	 * @return qualified types of ancestors of given class
	 */
	static Set<QualifiedType> findAllAncestors(ClassOrInterfaceDeclaration aClass, Resolver resolver, CompilationInfo info = null) {
		ResolutionData data = info?.data ?:
				ResolutionData.of(NodeHelper.findDeclaringCompilationUnit(aClass)
						.orElseThrow {
					new CompilationUnitNotFoundError("Resolving all ancestors needs compilation unit of given class!")
				})

		def collector = new AncestorCollector(resolver)
		return collector.getAll(data, aClass)
	}

}
