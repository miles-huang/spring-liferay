package com.brownstonetech.springliferay.exception;

import com.liferay.portal.kernel.exception.PortalException;

public class ErrorMessageException extends PortalException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ErrorMessageException() {
	}

	public ErrorMessageException(String msg) {
		super(msg);
	}

	public ErrorMessageException(Throwable cause) {
		super(cause);
	}

	public ErrorMessageException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
