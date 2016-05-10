package com.brownstonetech.springliferay.util.dynamicdatalist;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.service.ServiceContext;

public class DDLRecordEditHelper {

	public static final String FIELDS_DISPLAY_NAME = "_fieldsDisplay";
	public static final String INSTANCE_SEPARATOR = "_INSTANCE_";
	public static final String FIELD_NAMESPACE = "";
	
	private ServiceContext serviceContext;
	private TimeZone timeZone;
	
	public DDLRecordEditHelper(ServiceContext serviceContext) {
		this.serviceContext = serviceContext;
		this.timeZone = serviceContext.getTimeZone();
		if ( timeZone == null ) {
			timeZone = TimeZone.getDefault();
		}
	}
	
	public void setFieldValue(String fieldName, Object fieldRawValue) {
		/*
		 * Logic is come from DDMImpl#getFieldNames(
		 * String fieldNamespace, String fieldName,
		 * ServiceContext serviceContext)
		 */
		String displayFields = (String)serviceContext.getAttribute(FIELD_NAMESPACE + FIELDS_DISPLAY_NAME);
		String[] fieldsDisplayValues = StringUtil.split(displayFields);
		String attributeName = fieldName;
		if (fieldsDisplayValues.length > 0) {
			// The "instanced" attributeName is only required when
			// the fieldsDisplay attribute contains a list 
			// (This means submit by DDM Form Template generated form)
			StringBuilder sb = new StringBuilder();
			String instancedAttributeName = null;
			// First try to find first "instanced" attribute matching with
			// masterKeyField and remove extra matching field names
			for (String namespacedFieldName : fieldsDisplayValues) {
				String fieldNameValue = StringUtil.extractFirst(
					namespacedFieldName, INSTANCE_SEPARATOR);

				if (fieldNameValue.equals(attributeName)) {
					if ( instancedAttributeName != null ) {
						// discard all matched namespacedFieldName
						// except the first one
						continue;
					}
					instancedAttributeName = namespacedFieldName;
				}
				if ( sb.length() >0) sb.append(',');
				sb.append(namespacedFieldName);
			}
			if ( instancedAttributeName == null) {
				// The masterKeyField is missing from the fieldsDisplayValues list,
				// in this case generate an "instanced" name attribute and
				// add it to the list.
				instancedAttributeName = attributeName+INSTANCE_SEPARATOR+StringUtil.randomId();
				if (sb.length() > 0) sb.append(',');
				sb.append(instancedAttributeName);
			}
			serviceContext.setAttribute(FIELD_NAMESPACE + FIELDS_DISPLAY_NAME, sb.toString());
			// replace attributeName with the resolved "instanced" attribute name
			attributeName = instancedAttributeName;
		}
		// Handle Date
//		if ( fieldRawValue == null ) return;
		if ( fieldRawValue instanceof java.sql.Date) {
			setDateFieldValue(attributeName, (Date)fieldRawValue, TimeZone.getDefault());
			return;
		} else if ( fieldRawValue instanceof Date ) {
			setDateFieldValue(attributeName, (Date)fieldRawValue, timeZone);
			return;
		}
		String fieldValue = StringPool.BLANK;
		if ( fieldRawValue != null ) {
			fieldValue = String.valueOf(fieldRawValue);
		}
		serviceContext.setAttribute(attributeName,
				fieldValue);
	}

	private void setDateFieldValue(String attributeName, Date date, TimeZone timeZone) {
		Calendar cal = Calendar.getInstance(timeZone);
		cal.setTime(date);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		serviceContext.setAttribute(attributeName +"Year", String.valueOf(year));
		serviceContext.setAttribute(attributeName +"Month", String.valueOf(month));
		serviceContext.setAttribute(attributeName +"Day", String.valueOf(day));
	}

}
