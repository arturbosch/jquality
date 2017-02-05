package io.gitlab.arturbosch.smartsmells.smells.featureenvy

import com.github.javaparser.ast.body.MethodDeclaration
import groovy.transform.Immutable
import groovy.transform.ToString
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.smartsmells.smells.MethodSpecific

/**
 * @author artur
 */
@Immutable
@ToString(includeNames = false, includePackage = false)
class FeatureEnvy implements MethodSpecific {

	String name
	String signature
	String inClass

	String objectName
	String objectSignature
	String objectType

	double factor
	double factorThreshold

	@Delegate
	SourceRange sourceRange
	@Delegate
	SourcePath sourcePath

	@Override
	String asCompactString() {
		"FeatureEnvy \n\nMethod $name is jealousy of $objectName: $objectType" +
				"\nFactor: $factor with threshold: $factorThreshold"
	}

	@Override
	String asComparableString() {
		return "${javaClassName()}$signature#$objectSignature"
	}

	@Override
	MethodSpecific copy(MethodDeclaration method) {
		return new FeatureEnvy(method.getNameAsString(), method.declarationAsString, inClass,
				objectName, objectSignature, objectType, factor, factorThreshold,
				SourceRange.fromNode(method), sourcePath)
	}

	boolean equals(o) {
		if (this.is(o)) return true
		if (!(o instanceof FeatureEnvy)) return false

		FeatureEnvy that = (FeatureEnvy) o

		if (inClass != that.inClass) return false
		if (name != that.name) return false
		if (objectName != that.objectName) return false
		if (objectSignature != that.objectSignature) return false
		if (objectType != that.objectType) return false
		if (signature != that.signature) return false
		if (sourcePath != that.sourcePath) return false

		return true
	}

	int hashCode() {
		int result
		result = (name != null ? name.hashCode() : 0)
		result = 31 * result + (signature != null ? signature.hashCode() : 0)
		result = 31 * result + (inClass != null ? inClass.hashCode() : 0)
		result = 31 * result + (objectName != null ? objectName.hashCode() : 0)
		result = 31 * result + (objectSignature != null ? objectSignature.hashCode() : 0)
		result = 31 * result + (objectType != null ? objectType.hashCode() : 0)
		result = 31 * result + (sourcePath != null ? sourcePath.hashCode() : 0)
		return result
	}

	@Override
	String name() {
		return name
	}

	@Override
	String signature() {
		return signature
	}
}
