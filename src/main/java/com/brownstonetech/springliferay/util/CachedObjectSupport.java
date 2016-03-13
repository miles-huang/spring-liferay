package com.brownstonetech.springliferay.util;

public abstract class CachedObjectSupport implements ExpirationValidateable {

	private long lastExpirationValidationTime = 0L;
	private static final long EXPIRATION_CHECK_INTERVAL = 5000;
	
	@Override
	public boolean isExpired() {
		long current = System.currentTimeMillis();
		long lastChecked = lastExpirationValidationTime;
		lastExpirationValidationTime = current;
		if ( current - lastChecked > EXPIRATION_CHECK_INTERVAL) {
			return doExpirationCheck();
		}
		return false;
	}
	
	protected abstract boolean doExpirationCheck();

}