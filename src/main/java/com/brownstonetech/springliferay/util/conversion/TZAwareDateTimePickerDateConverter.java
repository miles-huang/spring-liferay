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

package com.brownstonetech.springliferay.util.conversion;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import com.liferay.portal.kernel.servlet.BrowserSnifferUtil;
import com.liferay.portal.util.PortalUtil;

public class TZAwareDateTimePickerDateConverter extends TZAwareDateTimeConverter {

	private static final String _SIMPLE_DATE_FORMAT_PATTERN_DMY = "dd/MM/yyyy";

	private static final String _SIMPLE_DATE_FORMAT_PATTERN_MDY = "MM/dd/yyyy";

	private static final String _SIMPLE_DATE_FORMAT_PATTERN_YMD = "yyyy/MM/dd";

	private static final String _SIMPLE_DATE_FORMAT_PATTERN_HTML5 = "yyyy-MM-dd";
	
	public static String getDateTimePickerPattern(PortletRequest portletRequest, Locale locale) {
		
		String simpleDateFormatPattern = _SIMPLE_DATE_FORMAT_PATTERN_MDY;
		
		HttpServletRequest request = PortalUtil.getHttpServletRequest(portletRequest);
		if (BrowserSnifferUtil.isMobile(request)) {
			simpleDateFormatPattern = _SIMPLE_DATE_FORMAT_PATTERN_HTML5;
			return simpleDateFormatPattern;
		}
		
		DateFormat shortDateFormat = DateFormat.getDateInstance(DateFormat.SHORT, locale);

		SimpleDateFormat shortDateFormatSimpleDateFormat = (SimpleDateFormat)shortDateFormat;

		String shortDateFormatSimpleDateFormatPattern = shortDateFormatSimpleDateFormat.toPattern();

		if (shortDateFormatSimpleDateFormatPattern.indexOf("y") == 0) {
			simpleDateFormatPattern = _SIMPLE_DATE_FORMAT_PATTERN_YMD;
		}
		else if (shortDateFormatSimpleDateFormatPattern.indexOf("d") == 0) {
			simpleDateFormatPattern = _SIMPLE_DATE_FORMAT_PATTERN_DMY;
		}
		return simpleDateFormatPattern;
	}
	
//	@Override
//	protected DateFormat[] getSupportedTimeFormat(Locale locale) {
//        DateFormat d1 = DateFormat.getTimeInstance(DateFormat.MEDIUM, locale);
//        DateFormat[] dfs = {d1};
//        return dfs; 
//	}
//	
//	@Override
//	protected DateFormat[] getSupportedDateFormat(Locale locale) {
//        DateFormat d1 = new SimpleDateFormat(getDateTimePickerPattern(locale));
//        DateFormat[] dfs = {d1};
//        return dfs;
//	}
//	
//	@Override
//	protected DateFormat[] getSupportedDateTimeFormat(Locale locale) {
//        DateFormat d1 = new SimpleDateFormat(getDateTimePickerPattern(locale));
//        DateFormat[] dfs = {d1};
//        return dfs;
//	}
//	
//	@Override
//	protected DateFormat getRenderTimeFormat(Locale locale) {
//        return DateFormat.getTimeInstance(DateFormat.MEDIUM, locale);
//	}
//	
//	@Override
//	protected DateFormat getRenderDateFormat(Locale locale) {
//		return DateFormat.getDateInstance(DateFormat.SHORT, locale);
//	}
//	
//	@Override
//	protected DateFormat getRenderDateTimeFormat(Locale locale) {
//		return DateFormat.getDateTimeInstance(DateFormat.SHORT,
//                DateFormat.MEDIUM,
//                locale);
//	}

}
