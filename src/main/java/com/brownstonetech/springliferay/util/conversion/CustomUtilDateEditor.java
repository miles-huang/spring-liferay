package com.brownstonetech.springliferay.util.conversion;

import java.text.DateFormat;
import java.util.Date;

/**
 * For {@link java.sql.Date} conversion
 * 
 * @author Miles Huang
 *
 */
public class CustomUtilDateEditor extends CustomDateEditorSupport {

	public CustomUtilDateEditor(boolean allowEmpty, DateFormat ... dateFormat) {
		super(allowEmpty, dateFormat);
	}

	public CustomUtilDateEditor(boolean allowEmpty, int exactDateLength, DateFormat ... dateFormat) {
		super(allowEmpty, exactDateLength, dateFormat);
	}

	@Override
	protected void setDateValue(Date date) {
		setValue(date);
	}

}
