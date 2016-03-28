package com.brownstonetech.springliferay.component;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Iterator;
import java.util.TimeZone;

import javax.portlet.PortletRequest;

import org.springframework.stereotype.Component;

import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.taglib.aui.AUIUtil;

@Component("renderHelper")
public class RenderHelper implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String toString(Object o) {
		return String.valueOf(o);
	}

	public String toJSONString(Object o) {
		return JSONFactoryUtil.looseSerializeDeep(o);
	}
	
	public Iterator<String> retrieveSessionErrors(PortletRequest portletRequest) {
		Iterator<String> errors =
				SessionErrors.iterator(portletRequest);
		return errors;
	}
	
	public Iterator<String> retrieveSessionMessages(PortletRequest portletRequest) {
		Iterator<String> errors =
				SessionMessages.iterator(portletRequest);
		return errors;
	}

	public String auiBuildControlGroupCss(
			boolean inlineField, String inlineLabel, String wrapperCssClass,
			String baseType) {
		return AUIUtil.buildControlGroupCss(
				inlineField, inlineLabel, wrapperCssClass,
				baseType);
	}
	
	/**
	 * Get a java.sql.Date instance which represents same date as the specificied
	 * calendar value.
	 * 
	 * @param calendar
	 * @return
	 */
	public java.sql.Date getDate(Calendar calendar) {
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int date = calendar.get(Calendar.DAY_OF_MONTH);
		Calendar c = Calendar.getInstance(TimeZone.getDefault());
		c.set(year, month, date);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return new java.sql.Date(c.getTimeInMillis());
	}
	
	/**
	 * Get a java.sql.Date instance which represents same date as the specified
	 * date value in the specified time zone.
	 * <p>
	 * If the specified data value is already an instance of java.sql.Date,
	 * the java.sql.Date value is returned without any modification.
	 * 
	 * @param date The specified java.sql.Date value
	 * @param timeZone The specified time zone.
	 * 
	 * @return
	 */
	public java.sql.Date getDate(java.util.Date date, TimeZone timeZone) {
		if ( date instanceof java.sql.Date) {
			return (java.sql.Date)date;
		}
		Calendar calendar = Calendar.getInstance(timeZone);
		calendar.setTime(date);
		return getDate(calendar);
	}
	
}
