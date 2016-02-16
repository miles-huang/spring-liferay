package com.brownstonetech.springliferay.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class SimpleCache<K,V> extends LinkedHashMap<K,V> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final int capacity;

	public SimpleCache(int capacity) {
		super(capacity + 1, 1.1f, true);
		this.capacity = capacity;
	}

	@Override
	protected boolean removeEldestEntry(Map.Entry<K,V> eldest) {
		return size() > capacity;
	}	
}
