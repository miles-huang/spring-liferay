package com.brownstonetech.springliferay.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * This class is used as base class for creating a
 * Hash object to transfer key/value based collection data
 * to template engine easily.
 * Subclass should implement the {@link #get(Object)} method
 * to provide key related value dynamically.
 * 
 * @author miles
 *
 * @param <K>
 * @param <V>
 */
public abstract class EasyHashBase<K, V> implements Map<K, V> {

	public int size() {
		return 0;
	}

	public boolean isEmpty() {
		return false;
	}

	public boolean containsKey(Object key) {
		return true;
	}

	public boolean containsValue(Object value) {
		return false;
	}

	public V put(K key, V value) {
		return value;
	}

	public void clear() {
	}

	public Set<K> keySet() {
		return Collections.emptySet();
	}

	public Collection<V> values() {
		return Collections.emptyList();
	}

	public V remove(Object key) {
		return null;
	}

	public void putAll(Map<? extends K, ? extends V> m) {
	}

	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return Collections.emptySet();
	}

}
