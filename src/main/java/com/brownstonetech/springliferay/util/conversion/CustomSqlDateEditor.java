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
public class CustomSqlDateEditor extends CustomDateEditorSupport {

	public CustomSqlDateEditor(boolean allowEmpty, DateFormat dateFormat) {
		this(allowEmpty, -1, dateFormat);
	}

	public CustomSqlDateEditor(boolean allowEmpty, int exactDateLength, DateFormat dateFormat) {
		super(allowEmpty, exactDateLength, dateFormat);
		dateFormat.setTimeZone(TimeZone.getDefault());
	}

	@Override
	protected void setDateValue(Date date) {
		setValue(new java.sql.Date(date.getTime()));
	}

}
