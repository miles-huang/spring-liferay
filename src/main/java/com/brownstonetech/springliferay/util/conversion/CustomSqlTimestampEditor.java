package com.brownstonetech.springliferay.util.conversion;

import java.text.DateFormat;
import java.util.Date;

/**
 * For {@link java.sql.Date} conversion
 * 
 * @author Miles Huang
 *
 */
public class CustomSqlTimestampEditor extends CustomDateEditorSupport {

	public CustomSqlTimestampEditor(boolean allowEmpty, DateFormat ... dateFormat) {
		super(allowEmpty, dateFormat);
	}

	public CustomSqlTimestampEditor(boolean allowEmpty, int exactDateLength, DateFormat ... dateFormat) {
		super(allowEmpty, exactDateLength, dateFormat);
	}

	@Override
	protected void setDateValue(Date date) {
		setValue(new java.sql.Timestamp(date.getTime()));
	}

}
