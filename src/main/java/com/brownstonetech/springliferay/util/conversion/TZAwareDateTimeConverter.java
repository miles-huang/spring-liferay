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
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

import java.sql.Time;
import java.sql.Timestamp;


public class TZAwareDateTimeConverter  {

	public static final String RFC3339_DATE_FORMAT="yyyy-MM-dd";
	public static final String RFC3339_TIME_FORMAT="HH:mm:ss";
	public static final String RFC3339_DATETIME_FORMAT="yyyy-MM-dd'T'HH:mm:ss";
	
//	/**
//	 * In trimTimePart mode, this converter will treat Calendar and Date object 
//	 * contains date only data (year, month and day without time part). Time part is set to 0:0:0.000 in
//	 * the context time zone.<p/>
//	 * Since other supported date class has clear definition of valid date fields
//	 * and time zone, their conversion are not influence by trimTimePart mode.<p/>
//	 * Subclass of this converter implementation can override this method to enable
//	 * timeTimePart mode.<p/>
//	 * If trimTimePart mode is set, setting to enable trimDatePart is ignored.
//	 * 
//	 * @return true: Enable timeTimePart mode
//	 * default: false
//	 */
//	protected boolean trimTimePart() {
//		return false;
//	}
//
//	
//	/**
//	 * In trimDatePart mode, this converter will treat Calendar and Date object 
//	 * contains time only data (hour, minute, second and millisecond without
//	 * date part). Date part is set to Jan 1,1970 in
//	 * the context time zone.<p/>
//	 * Since other supported date class has clear definition of valid date fields
//	 * and time zone, their conversion are not influence by trimDatePart mode.<p/>
//	 * Subclass of this converter implementation can override this method to enable
//	 * timeTimePart mode.<p/>
//	 * If trimTimePart mode is set, setting to enable trimDatePart is ignored.
//	 * 
//	 * @return true: Enable timeDatePart mode
//	 * default: false
//	 */
//	protected boolean trimDatePart() {
//		return false;
//	}
//	
//	protected abstract DateFormat[] getSupportedTimeFormat(Locale locale);
//	
//	protected abstract DateFormat[] getSupportedDateFormat(Locale locale);
//	
//	protected abstract DateFormat[] getSupportedDateTimeFormat(Locale locale);
//	
//	protected abstract DateFormat getRenderTimeFormat(Locale locale);
//	
//	protected abstract DateFormat getRenderDateFormat(Locale locale);
//	
//	protected abstract DateFormat getRenderDateTimeFormat(Locale locale);
//	
//	@SuppressWarnings({ "unchecked", "rawtypes" })
//	@Override
//	public Object convertFromString(Map context, String[] values, Class toClass) {
//		if ( values.length != 1 )
//			return super.performFallbackConversion(context, values, toClass);
//		
//		String sa = values[0];
//		if ( StringUtils.hasText(sa)) return null;
//
//		Date check = null;
//		Locale locale = getLocale(context);
//		TimeZone tz = getTimeZone(context);
//		DateFormat[] dfs = null;
//		if ( Timestamp.class==toClass ) {
//			dfs = this.getSupportedDateTimeFormat(locale);
//		} else if ( Calendar.class==toClass || Date.class==toClass )  {
//			if ( this.trimDatePart() ) { 
//				dfs = this.getSupportedTimeFormat(locale);
//			} else if (this.trimTimePart()){
//				dfs = this.getSupportedDateFormat(locale);
//			} else {
//				dfs = this.getSupportedDateTimeFormat(locale);
//			}
//		} else if ( Time.class==toClass ) {
//			dfs = this.getSupportedTimeFormat(locale);
//			tz = TimeZone.getTimeZone("GMT");
//		} else if ( java.sql.Date.class==toClass ) {
//			dfs = this.getSupportedDateFormat(locale);
//			tz = TimeZone.getDefault();
//		} else {
//	    	throw new TypeConversionException("Unsupported date class "+toClass);
//		}
//		
//        for (DateFormat df1 : dfs) {
//            df1.setTimeZone(tz);
//            try {
//                check = df1.parse(sa);
//                if (check != null) {
//                    break;
//                }
//            }
//            catch (ParseException ignore) {
//            }
//        }
//        if ( check == null ) {
//            throw new TypeConversionException("Can't parse string as date");
//        }
//        if ( Timestamp.class==toClass ) {
//			return new Timestamp(check.getTime());
//		} else if ( java.sql.Date.class==toClass ) {
//			return new java.sql.Date(check.getTime());
//		} else if ( Time.class==toClass ) {
//			return new Time(check.getTime());
//		}
//		Calendar result = Calendar.getInstance(tz,locale);
//		result.setTime(check);
//		if ( this.trimDatePart() ) {
//			result.clear(Calendar.YEAR);
//			result.clear(Calendar.MONTH);
//			result.clear(Calendar.DAY_OF_MONTH);
//		} else if ( this.trimTimePart() ) {
//			result.clear(Calendar.HOUR_OF_DAY);
//			result.clear(Calendar.MINUTE);
//			result.clear(Calendar.SECOND);
//			result.clear(Calendar.MILLISECOND);
//		}
//		if ( Date.class==toClass ) {
//			return result.getTime();
//		}
//		result.getTimeInMillis();
//		return result;
//	}
//
//	@SuppressWarnings({ "unchecked", "rawtypes" })
//	@Override
//	public String convertToString(Map context, Object value) {
//        if (value == null) {
//        	return null;
//        }
//		Locale locale = getLocale(context);
//        DateFormat df;
//        Long ts;
//        if (value instanceof Time) {
//            df = getRenderTimeFormat(locale);
//            df.setTimeZone(TimeZone.getTimeZone("GMT"));
//            ts = ((Time)value).getTime();
//        } else if (value instanceof java.sql.Date ) {
//            df = getRenderDateFormat(locale);
//            df.setTimeZone(TimeZone.getDefault());
//            ts = ((java.sql.Date)value).getTime();
//        } else if (value instanceof Timestamp ) {
//        	if ( trimTimePart() ) {
//        		df = getRenderDateFormat(locale);
//        	} else if ( trimDatePart() ) {
//        		df = getRenderTimeFormat(locale);
//        	} else {
//        		df = getRenderDateTimeFormat(locale);
//        	}
//        	df.setTimeZone(getTimeZone(context));
//        	ts = ((Timestamp)value).getTime();
//        } else if (value instanceof Calendar || value instanceof Date ) {
//        	if ( trimTimePart() ) {
//        		df = getRenderDateFormat(locale);
//        	} else if ( trimDatePart() ) {
//        		df = getRenderTimeFormat(locale);
//        	} else {
//        		df = getRenderDateTimeFormat(locale);
//        	}
//        	if ( value instanceof Calendar ) {
//        		df.setTimeZone(((Calendar)value).getTimeZone());
//                ts = ((Calendar)value).getTimeInMillis();
//        	} else {
//                df.setTimeZone(getTimeZone(context));
//                ts = ((Date)value).getTime();
//        	}
//        } else {
//        	throw new TypeConversionException("This covnerter should only be applied to Date and Calendar class");
//        }
//        String result = df.format(ts);
//		return result;
//	}
//
//    protected Locale getLocale(Map<String, Object> context) {
//        if (context == null) {
//            return Locale.getDefault();
//        }
//        Locale locale = ActionContext.getContext().getLocale();
//
//        if (locale == null) {
//            locale = Locale.getDefault();
//        }
//
//        return locale;
//    }
//
//    protected TimeZone getTimeZone(Map<String, Object> context) {
//        if (context != null) {
//        	ValueStack stack = ActionContext.getContext().getValueStack();
//        	TimeZone tz = null;
//        	tz = (TimeZone)stack.findValue("timeZone",TimeZone.class);
//        	if ( tz != null )
//        		return tz;
//        }
//        return TimeZone.getDefault();
//    }
    
}
