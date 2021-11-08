package io.gitlab.arturbosch.jpal.ast

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.type.ClassOrInterfaceType
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.resolution.QualifiedType
import io.gitlab.arturbosch.jpal.resolution.ResolutionData
import io.gitlab.arturbosch.jpal.resolution.Resolver

/**
 * Resolves and collects recursively all qualified types of the sub classes of starting class.
 *
 * @author Artur Bosch
 */
@CompileStatic
class AncestorCollector {

	private Resolver typeSolver

	AncestorCollector(Resolver resolver) {
		typeSolver = resolver
	}

	Set<QualifiedType> getAll(ResolutionData startData, ClassOrInterfaceDeclaration aClass) {
		def types = new HashSet<QualifiedType>()
		resolve(types, startData, aClass)
		return types
	}

	private void resolve(Set<QualifiedType> resolved, ResolutionData data, ClassOrInterfaceDeclaration aClass) {
		def ancestorTypes = resolveAncestorTypes(aClass, data)
		def ancestors = extractAncestorClasses(ancestorTypes)
		resolved.addAll(ancestorTypes)
		for (Map.Entry<ClassOrInterfaceDeclaration, ResolutionData> entry : ancestors) {
			resolve(resolved, entry.value, entry.key)
		}
	}

	private Set<QualifiedType> resolveAncestorTypes(ClassOrInterfaceDeclaration aClass,
													ResolutionData data) {
		Set<QualifiedType> result = new HashSet<>()
		def className = aClass.nameAsString
		addNonCyclicTypes(aClass.implementedTypes, className, data, result)
		addNonCyclicTypes(aClass.extendedTypes, className, data, result)
		return result
	}

	private void addNonCyclicTypes(List<ClassOrInterfaceType> types,
								   String className,
								   ResolutionData data,
								   Set<QualifiedType> result) {
		for (ClassOrInterfaceType type : types) {
			if (type.nameAsString != className) { // anti cyclic
				result.add(typeSolver.resolveType(type, data))
			}
		}
	}

	private Map<ClassOrInterfaceDeclaration, ResolutionData> extractAncestorClasses(Set<QualifiedType> types) {
		def map = new HashMap<ClassOrInterfaceDeclaration, ResolutionData>()

		for (type in types) {
			if (type.isReference()) {
				typeSolver.find(type).ifPresent {
					def declaration = it.getTypeDeclarationByQualifier(type).orElse(null)
					if (declaration && declaration instanceof ClassOrInterfaceDeclaration) {
						map.put(declaration as ClassOrInterfaceDeclaration, it.data)
					}
				}
			}
		}

		return map
	}

}
