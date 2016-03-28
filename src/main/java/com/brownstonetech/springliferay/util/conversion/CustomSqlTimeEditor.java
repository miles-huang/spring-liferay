package com.brownstonetech.springliferay.util.conversion;

import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * For {@link java.sql.Date} conversion
 * 
 * @author Miles Huang
 *
 */
public class CustomSqlTimeEditor extends CustomDateEditorSupport {

	public CustomSqlTimeEditor(boolean allowEmpty, DateFormat dateFormat) {
		this(allowEmpty, -1, dateFormat);
	}

	public CustomSqlTimeEditor(boolean allowEmpty, int exactDateLength, DateFormat dateFormat) {
		super(allowEmpty, exactDateLength, dateFormat);
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	@Override
	protected void setDateValue(Date date) {
		setValue(new java.sql.Time(date.getTime()));
	}

}
