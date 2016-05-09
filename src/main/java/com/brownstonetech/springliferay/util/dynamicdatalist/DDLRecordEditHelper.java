package com.brownstonetech.springliferay.util.dynamicdatalist;

import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.service.ServiceContext;

public class DDLRecordEditHelper {

	public static final String FIELDS_DISPLAY_NAME = "_fieldsDisplay";
	public static final String INSTANCE_SEPARATOR = "_INSTANCE_";
	public static final String FIELD_NAMESPACE = "";
	
	private ServiceContext serviceContext;
	
	public DDLRecordEditHelper(ServiceContext serviceContext) {
		this.serviceContext = serviceContext;
	}
	
	public void setFieldValue(String fieldName, String fieldValue) {
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
		serviceContext.setAttribute(attributeName,
				fieldValue);
	}

}
