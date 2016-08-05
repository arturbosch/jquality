package io.gitlab.arturbosch.smartsmells.util;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * External from SmartUtils 0.2.5
 *
 * Defines a generic cache.
 *
 * @author artur
 */
public abstract class Cache<K, V> {

	/**
	 * Internal representation of the cache.
	 */
	protected final Map<K, V> cache = new ConcurrentHashMap<>();

	public void reset() {
		cache.clear();
	}

	public V verifyAndReturn(K value) {
		return cache.getOrDefault(value, defaultValue());
	}

	public void putPair(K key, V value) {
		Objects.requireNonNull(key, "Key must not be null!");
		Objects.requireNonNull(value, "Value must not be null!");
		cache.put(key, value);
	}

	public int size() {
		return cache.size();
	}

	/**
	 * The value which will be returned if the cache does not contain the requested key. <br/>
	 * Override by concrete caches for different default values
	 * 
	 * @return the default value
	 */
	public V defaultValue() {
		return null;
	}

}
