package com.brownstonetech.springliferay.util.search;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.TimeZoneThreadLocal;

public class SearchTermUtil {

	private static final String _INDEX_DATE_FORMAT_PATTERN = PropsUtil.get(
			PropsKeys.INDEX_DATE_FORMAT_PATTERN);
	
	public static String formatDateTime(Date value) {
		if (value == null) {
			return null;
		}
		DateFormat _dateFormat = new SimpleDateFormat(_INDEX_DATE_FORMAT_PATTERN);
		_dateFormat.setTimeZone(TimeZoneThreadLocal.getDefaultTimeZone());
		return _dateFormat.format(value);
	}
	
	public static String formatDateTime(Calendar value) {
		if (value == null) {
			return null;
		}
		DateFormat _dateFormat = new SimpleDateFormat(_INDEX_DATE_FORMAT_PATTERN);
		_dateFormat.setTimeZone(value.getTimeZone());
		return _dateFormat.format(value.getTime());
	}
	
	public static String formatDateTime(java.sql.Date value) {
		if (value == null) {
			return null;
		}
		DateFormat _dateFormat = new SimpleDateFormat(_INDEX_DATE_FORMAT_PATTERN);
		// java.sql.Date always use JVM default time zone
		_dateFormat.setTimeZone(TimeZone.getDefault());
		return _dateFormat.format(value.getTime());
	}
	
	public static String formatValue(Object value) {
		if ( value == null ) return null;

		if ( value instanceof Date
				|| value instanceof Timestamp ) {
			return formatDateTime((Date)value);
		}
		if ( value instanceof java.sql.Date ) {
			return formatDateTime((java.sql.Date)value);
		}
		if ( value instanceof Calendar) {
			return formatDateTime((Calendar)value);
		}
		return String.valueOf(value);
	}
	
}
