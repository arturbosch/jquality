package io.gitlab.arturbosch.jpal.core

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.TypeDeclaration
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import io.gitlab.arturbosch.jpal.ast.TypeHelper
import io.gitlab.arturbosch.jpal.internal.Validate
import io.gitlab.arturbosch.jpal.resolution.QualifiedType
import io.gitlab.arturbosch.jpal.resolution.ResolutionData
import io.gitlab.arturbosch.jpal.resolution.Resolver

import java.nio.file.Path
import java.nio.file.Paths

/**
 * Compact information about a compilation unit. Storing the qualified type of
 * root class, a path to the matching file this compilation unit belongs and
 * additional qualified types of it's inner and used classes.
 *
 * @author artur
 */
@CompileStatic
class CompilationInfo implements UserDataHolder, Comparable<CompilationInfo> {

	final QualifiedType qualifiedType
	final CompilationUnit unit
	final Path path
	final Path relativePath
	final TypeDeclaration mainType
	final ResolutionData data
	final Map<QualifiedType, TypeDeclaration> innerClasses

	/**
	 * Are set after creation of all compilation units because
	 * only at that point resolution of star imports is possible.
	 */
	private Set<QualifiedType> usedTypes

	Set<QualifiedType> getUsedTypes() {
		return Collections.unmodifiableSet(usedTypes)
	}

	/**
	 * Factory method to build compilation info's. In most cases you don't need
	 * to build them by yourself, just use a CompilationStorage.
	 *
	 * @param qualifiedType qualified type of the root class
	 * @param unit corresponding compilation unit
	 * @param path path to the root class file
	 * @return a compilation info
	 */
	static CompilationInfo of(CompilationUnit unit, Path path) {
		Validate.notNull(unit)
		Validate.notNull(path)
		def mainClassAndInnerClassesPair = TypeHelper.getQualifiedDeclarationsOfInnerClasses(unit)
		QualifiedType qualifiedType = mainClassAndInnerClassesPair.a.a
		TypeDeclaration mainType = mainClassAndInnerClassesPair.a.b
		Map<QualifiedType, TypeDeclaration> innerTypes = mainClassAndInnerClassesPair.b
		return new CompilationInfo(qualifiedType, mainType, innerTypes, unit, path)
	}

	private CompilationInfo(QualifiedType qualifiedType,
							TypeDeclaration mainType,
							Map<QualifiedType, TypeDeclaration> innerClasses,
							CompilationUnit unit,
							Path path) {

		this.innerClasses = innerClasses
		this.mainType = mainType
		this.qualifiedType = qualifiedType
		this.unit = unit
		this.path = path
		this.relativePath = relativizePath(path, unit)
		this.usedTypes = Collections.emptySet()
		this.data = ResolutionData.of(unit)
	}

	private static Path relativizePath(Path path, CompilationUnit unit) {
		def name = path.fileName
		unit.packageDeclaration.ifPresent { name = Paths.get(it.nameAsString.replace(".", "//")).resolve(name) }
		name
	}

	@PackageScope
	void findUsedTypes(Resolver typeSolver) {
		Validate.notNull(usedTypes)
		def usedTypes = TypeHelper.findAllUsedTypes(unit, typeSolver)
		this.usedTypes = replaceQualifiedTypesOfInnerClasses(usedTypes, innerClasses.keySet())
	}

	private static Set<QualifiedType> replaceQualifiedTypesOfInnerClasses(Set<QualifiedType> types,
																		  Set<QualifiedType> innerClasses) {
		def set = new HashSet<QualifiedType>()
		for (QualifiedType type : types) {
			def found = innerClasses.find { sameNameAndPackage(it, type) }
			if (found) set.add(found) else set.add(type)
		}
		return set
	}

	private static boolean sameNameAndPackage(QualifiedType first, QualifiedType second) {
		first.shortName == second.shortName && first.onlyPackageName == second.onlyPackageName
	}

	/**
	 * Tests if the given qualified type is referenced by this compilation unit.
	 *
	 * @param type given qualified type
	 * @return true if given type is used within this instance
	 */
	boolean isWithinScope(QualifiedType type) {
		Validate.notNull(type)
		Validate.isTrue(type.name.contains("."), "Is not a qualified type!")
		return usedTypes.contains(type)
	}

	/**
	 * Returns the type declaration of this info matching the given qualifier.
	 *
	 * @param qualifier searched type
	 * @return the main declaration or an inner declaration
	 */
	Optional<TypeDeclaration> getTypeDeclarationByQualifier(QualifiedType qualifier) {
		if (qualifiedType == qualifier) Optional.of(mainType)
		else Optional.ofNullable(innerClasses.find { it.key == qualifier }).map { it.value }
	}

	/**
	 * Return the qualified type matching given class name
	 * @param clazzName given name
	 * @return qualified type if class with given name is declared inside this compilation info
	 */
	Optional<QualifiedType> getQualifiedTypeBySimpleName(String clazzName) {
		return Optional.ofNullable(
				qualifiedType.shortName == clazzName
						? qualifiedType
						: innerClasses.find { it.key.shortName == clazzName }?.key)
	}

	@Override
	String toString() {
		return "CompilationInfo{" +
				"qualifiedType=" + qualifiedType +
				'}'
	}

	@Override
	int compareTo(CompilationInfo o) {
		return qualifiedType <=> o.qualifiedType
	}

	@Override
	int hashCode() {
		return Objects.hash(qualifiedType, relativePath)
	}

	@Override
	boolean equals(Object obj) {
		if (this.is(obj)) return true
		if (getClass() != obj.class) return false

		CompilationInfo that = (CompilationInfo) obj

		if (qualifiedType != that.qualifiedType) return false
		if (relativePath != that.relativePath) return false

		return true
	}
}
