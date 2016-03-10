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

package com.brownstonetech.springliferay.freemarker;

import java.io.IOException;
import java.io.Reader;

import com.brownstonetech.springliferay.freemarker.ADTTemplateLoaderUtil.TemplateSource;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import freemarker.cache.TemplateLoader;

/**
 * @author Miles Huang
 *
 */
public class ApplicationDisplayTemplateLoader implements TemplateLoader {

	private static Log _log = LogFactoryUtil.getLog(ApplicationDisplayTemplateLoader.class);

	@Override
	public Object findTemplateSource(String name) throws IOException {
		return ADTTemplateLoaderUtil.findTemplateSource(name);
	}

	public long getLastModified(Object templateSource) {
		if ( _log.isDebugEnabled() ) {
			_log.debug("Call getLastModified templateSource="+templateSource.toString());
		}
		TemplateSource src = (TemplateSource)templateSource;
		return ADTTemplateLoaderUtil.getLastModified(src);
	}

	@Override
	public Reader getReader(Object templateSource, String encoding)
			throws IOException {
		if ( _log.isDebugEnabled() ) {
			_log.debug("Call getReader templateSource="+templateSource.toString()+", encoding="+encoding);
		}
		TemplateSource src = (TemplateSource)templateSource;
		return ADTTemplateLoaderUtil.getReader(src);
	}

	@Override
	public void closeTemplateSource(Object templateSource) throws IOException {
		if ( _log.isDebugEnabled() ) {
			_log.debug("Call cloaseTemplateSource templateSource="+templateSource.toString());
		}
	}

}
