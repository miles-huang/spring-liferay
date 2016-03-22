package com.brownstonetech.springliferay.util;

public abstract class CachedObjectSupport implements ExpirationValidateable {

	private long lastExpirationValidationTime = 0L;
	private long expireCheckInterval;
	private static final long EXPIRATION_CHECK_INTERVAL = 5000;
	
	public CachedObjectSupport(long expireCheckInterval) {
		this.expireCheckInterval = expireCheckInterval;
	}
	
	@Override
	public boolean isExpired() {
		long current = System.currentTimeMillis();
		long lastChecked = lastExpirationValidationTime;
		lastExpirationValidationTime = current;
		if ( current - lastChecked > expireCheckInterval) {
			return doExpirationCheck();
		}
		return false;
	}
	
	protected abstract boolean doExpirationCheck();

}