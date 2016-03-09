package com.brownstonetech.springliferay.util.scripting;

import java.io.Serializable;

public class CachedCompiledClass implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String sourceHash;
	private long lastUpdateTime;
	private Class<?> compiledClass;
	
	public CachedCompiledClass(String sourceHash, long lastUpdateTime, Class<?> compiledClass) {
		this.sourceHash = sourceHash;
		this.compiledClass = compiledClass;
		this.lastUpdateTime = lastUpdateTime;
	}
	
	public String getSourceHash() {
		return sourceHash;
	}
	
	public Class<?> getCompiledClass() {
		return compiledClass;
	}
	
	public long getLastUpdateTime() {
		return lastUpdateTime;
	}
}