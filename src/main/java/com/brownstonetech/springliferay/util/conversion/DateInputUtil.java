package com.brownstonetech.springliferay.util.conversion;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.WebDataBinder;

import com.liferay.portal.kernel.servlet.BrowserSnifferUtil;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;

public class DateInputUtil {

	/**
	 * The format returned is always consistent with 
	 * liferay-ui:input-date used date format.
	 * 
	 * @param request
	 * @param locale
	 * @return
	 */
	public static DateFormat getDateInputFormat(HttpServletRequest request, Locale locale) {
		String simpleDateFormatPattern = getDatePattern(request, locale);

		DateFormat format = new SimpleDateFormat(simpleDateFormatPattern, locale);
		return format;
	}

	private static String getDatePattern(HttpServletRequest request, Locale locale) {
		String simpleDateFormatPattern = _SIMPLE_DATE_FORMAT_PATTERN_MDY;

		if (BrowserSnifferUtil.isMobile(request)) {
			simpleDateFormatPattern = _SIMPLE_DATE_FORMAT_PATTERN_HTML5;
		}
		else {
			DateFormat shortDateFormat = DateFormat.getDateInstance(DateFormat.SHORT, locale);

			SimpleDateFormat shortDateFormatSimpleDateFormat = (SimpleDateFormat)shortDateFormat;

			String shortDateFormatSimpleDateFormatPattern = shortDateFormatSimpleDateFormat.toPattern();

			if (shortDateFormatSimpleDateFormatPattern.indexOf("y") == 0) {
				simpleDateFormatPattern = _SIMPLE_DATE_FORMAT_PATTERN_YMD;
			}
			else if (shortDateFormatSimpleDateFormatPattern.indexOf("d") == 0) {
				simpleDateFormatPattern = _SIMPLE_DATE_FORMAT_PATTERN_DMY;
			}
		}
		return simpleDateFormatPattern;
	}
	
	private static String getTimePattern(HttpServletRequest servletRequest, Locale locale) {
		String simpleDateFormatPattern = _SIMPLE_TIME_FORMAT_PATTERN_ISO;

		if (BrowserSnifferUtil.isMobile(servletRequest)) {
			simpleDateFormatPattern = _SIMPLE_TIME_FORMAT_PATTERN_HTML5;
		}
		else if (DateUtil.isFormatAmPm(locale)) {
			simpleDateFormatPattern = _SIMPLE_TIME_FORMAT_PATTERN;
		}
		return simpleDateFormatPattern;
	}
	
	public static DateFormat getTimeInputFormat(HttpServletRequest servletRequest, Locale locale) {
		String simpleDateFormatPattern = getTimePattern(servletRequest, locale);
		DateFormat dateFormat = new SimpleDateFormat(simpleDateFormatPattern, locale);
		return dateFormat;
	}
	
	public static DateFormat getDateTimeInputFormat(HttpServletRequest servletRequest, Locale locale) {
		String datePattern = getDatePattern(servletRequest, locale);
		String timePattern = getTimePattern(servletRequest, locale);
		StringBundler sb = new StringBundler(3);
		sb.append(datePattern).append(' ').append(timePattern);
		DateFormat dateFormat = new SimpleDateFormat(sb.toString(), locale);
		return dateFormat;
	}

	
	public static void initDatePropertyEditors(WebDataBinder binder, PortletRequest portletRequest) {
		HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(portletRequest);
		Locale locale = Locale.getDefault();
		TimeZone timeZone = TimeZone.getDefault();
		ThemeDisplay themeDisplay = (ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);
		if ( themeDisplay != null ) {
			locale = themeDisplay.getLocale();
			timeZone = themeDisplay.getTimeZone();
		}
		DateFormat dateFormat = getDateInputFormat(servletRequest, locale);
		binder.registerCustomEditor(java.sql.Date.class, 
				new CustomSqlDateEditor(true, dateFormat));
		
		dateFormat = getTimeInputFormat(servletRequest, locale);
		binder.registerCustomEditor(java.sql.Time.class,
				new CustomSqlTimeEditor(true, dateFormat));
		
		dateFormat = getDateInputFormat(servletRequest, locale);
		dateFormat.setTimeZone(timeZone);
		DateFormat dateFormat1 = getDateTimeInputFormat(servletRequest, locale);
		dateFormat1.setTimeZone(timeZone);
		binder.registerCustomEditor(java.sql.Timestamp.class,
				new CustomSqlTimestampEditor(true,
						dateFormat1, dateFormat));

		dateFormat = getDateInputFormat(servletRequest, locale);
		dateFormat.setTimeZone(timeZone);
		dateFormat1 = getDateTimeInputFormat(servletRequest, locale);
		dateFormat1.setTimeZone(timeZone);
		binder.registerCustomEditor(java.util.Date.class,
				new CustomUtilDateEditor(true,
						dateFormat1, dateFormat));
		
	}
	
	private static final String _SIMPLE_DATE_FORMAT_PATTERN_DMY = "dd/MM/yyyy";

	private static final String _SIMPLE_DATE_FORMAT_PATTERN_HTML5 = "yyyy-MM-dd";

	private static final String _SIMPLE_DATE_FORMAT_PATTERN_MDY = "MM/dd/yyyy";

	private static final String _SIMPLE_DATE_FORMAT_PATTERN_YMD = "yyyy/MM/dd";

	private static final String _SIMPLE_TIME_FORMAT_PATTERN = "hh:mm a";

	private static final String _SIMPLE_TIME_FORMAT_PATTERN_HTML5 = "HH:mm";

	private static final String _SIMPLE_TIME_FORMAT_PATTERN_ISO = "HH:mm";

}
