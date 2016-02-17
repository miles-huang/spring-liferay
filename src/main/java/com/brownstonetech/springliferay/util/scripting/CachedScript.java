package com.brownstonetech.springliferay.util.scripting;

import java.io.Serializable;
import java.util.Date;

public class CachedScript implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4142939436283576036L;
	
	private long lastUpdateTime;
	private long lastCheckTime;
	private long expireCheckIntervalMs = 5000;
	private String script;
	
	public CachedScript(Date lastUpdateTime, CharSequence script) {
		this.lastUpdateTime = lastUpdateTime.getTime();
		this.script = script == null? null: script.toString();
	}
	
	public synchronized boolean isExpired(Date lastUpdateTime) {
		lastCheckTime = System.currentTimeMillis();
		return this.lastUpdateTime < lastUpdateTime.getTime();
	}
	
	public String getScript() {
		return script;
	}
	
	public long getLastUpdateTime() {
		return lastUpdateTime;
	}

	public synchronized boolean isExpireCheckRequired() {
		long currentTime = System.currentTimeMillis();
		return currentTime - lastCheckTime > expireCheckIntervalMs;
	}

}
