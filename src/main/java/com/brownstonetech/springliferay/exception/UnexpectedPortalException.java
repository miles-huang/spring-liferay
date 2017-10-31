/**
 * 
 * SURWING CONFIDENTIAL
 * 
 * Copyright (c) 2009-2013 Surwing Incorporated
 * All rights reserved.
 * 
 * NOTICE:  All information contained herein is, and remains the property
 * of Surwing Information Technology Inc. and its suppliers, if any. The
 * intellectual and technical concepts contained herein are proprietary to
 * Surwing Information Technology Inc. and its suppliers and may be covered
 * by China and Foreign Patents, patents in process, and are protected by
 * trade secret or copyright law.
 * Dissemination of this information or reproduction of this material is
 * strictly forbidden unless prior written permission is obtained from
 * Surwing Information Technology Inc.
 *
 */

package com.brownstonetech.springliferay.exception;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;

/**
 * This exception is a sub class of the {@link SystemException} that
 * wraps an unexpected {@link PortalException}.
 * <p>
 * SystemException should be considered application level RuntimeException:
 * Every service method should be declared to throw SystemException.
 * Thus a service method that throws UnexpectedPortalException needn't
 * declare in the service method throws declaration explicitly, but
 * more general SystemException instead, if there is not already.
 * <p>
 * This exception is used when implementing service methods that call
 * nested service methods.
 * Normally many PoralException is declared to be thrown by the nested
 * service method. But some of these PortalException is granted to
 * should never happen by the service method's business logic design.
 * <p>
 * In such circumstance, the developer should catch such unexpected PortalException,
 * and convert it to this exception, with cause parameter set to the original
 * PortalException.
 * <p>
 * The purpose of this mechanism is to prevent unnecessary Exception propagation:
 * Keep the service method exception declaration accurate to the business
 * level exception while preserve adequate information when technology level
 * (business level unaware) exception is actually thrown at runtime.
 * Such exception always mean some application design failure or hardware/software
 * environment failure (include database row relation constraint violation).
 * 
 * @author Miles Huang
 *
 */
public class UnexpectedPortalException extends SystemException {

	public static final String UNEXPECTED_EXCEPTION = "Unexpected Exception should never happen.";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * @param msg
	 * @param cause
	 */
	public UnexpectedPortalException(String msg, PortalException cause) {
		super(msg, cause);
	}

	/**
	 * @param cause
	 */
	public UnexpectedPortalException(PortalException cause) {
		super(UNEXPECTED_EXCEPTION, cause);
	}
	
	public PortalException getCauseException() {
		return (PortalException)getCause();
	}
	
}
