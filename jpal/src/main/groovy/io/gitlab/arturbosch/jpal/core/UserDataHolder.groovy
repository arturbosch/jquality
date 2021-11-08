package io.gitlab.arturbosch.jpal.core

import com.github.javaparser.ast.DataKey
import groovy.transform.PackageScope

/**
 * Based on JavaParser's Node class.
 *
 * @author Artur Bosch
 */
trait UserDataHolder {

	static INIT_VECTOR = 1

	@PackageScope
	Map<DataKey<?>, Object> userData

	def <M> M getData(final DataKey<M> key) {
		if (userData == null) {
			return null
		}
		return (M) userData.get(key)
	}

	def <M> void setData(DataKey<M> key, M object) {
		if (userData == null) {
			userData = new IdentityHashMap<>(INIT_VECTOR)
		}
		userData.put(key, object)
	}

	boolean containsData(DataKey<?> key) {
		if (userData == null) {
			return false
		}
		return userData.get(key) != null
	}
}
