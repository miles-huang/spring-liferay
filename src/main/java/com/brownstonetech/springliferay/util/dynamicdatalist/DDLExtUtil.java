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

package com.brownstonetech.springliferay.util.dynamicdatalist;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.Format;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.dynamicdatalists.model.DDLRecord;
import com.liferay.portlet.dynamicdatalists.model.DDLRecordSet;
import com.liferay.portlet.dynamicdatalists.model.DDLRecordVersion;
import com.liferay.portlet.dynamicdatalists.util.DDLUtil;
import com.liferay.portlet.dynamicdatamapping.model.DDMStructure;
import com.liferay.portlet.dynamicdatamapping.storage.Field;
import com.liferay.portlet.dynamicdatamapping.storage.FieldConstants;
import com.liferay.portlet.dynamicdatamapping.storage.Fields;
import com.liferay.portlet.dynamicdatamapping.storage.StorageEngineUtil;
import com.liferay.portlet.dynamicdatamapping.util.DDMIndexerUtil;

public class DDLExtUtil extends DDLUtil {

	private static final Map<String,DDLRecordMetaFieldInfo> RESERVED_COLUMNS = new LinkedHashMap<String,DDLRecordMetaFieldInfo>();
	private static final Map<String,String> FIELD_DATA_TYPE_CLASSNAME;
	private static Log _log = LogFactoryUtil.getLog(DDLExtUtil.class);

//	private static Set<String> DYNAMIC_DATA_MAPPING_STRUCTURE_PRIVATE_FIELD_NAMES
//		= new HashSet<String>(
//			ListUtil.fromArray(PropsUtil.getArray(
//					PropsKeys.DYNAMIC_DATA_MAPPING_STRUCTURE_PRIVATE_FIELD_NAMES)));

	static {
		RESERVED_COLUMNS.put("displayIndex",
				new DDLRecordMetaFieldInfo("reservedDisplayIndex", "display-index", "int", null));
		RESERVED_COLUMNS.put("recordId",
				new DDLRecordMetaFieldInfo("reservedRecordId","id", "long", 
						com.liferay.portal.kernel.search.Field.ENTRY_CLASS_PK));
		RESERVED_COLUMNS.put("createUserId",
				new DDLRecordMetaFieldInfo("reservedCreateUserId","author", "long", 
						com.liferay.portal.kernel.search.Field.USER_ID));
		RESERVED_COLUMNS.put("createUserName",
				new DDLRecordMetaFieldInfo("reservedCreateUserName","author", "String",
						com.liferay.portal.kernel.search.Field.USER_NAME));
		RESERVED_COLUMNS.put("createDate",
				new DDLRecordMetaFieldInfo("reservedCreateDate","create-date", "java.util.Date",
						com.liferay.portal.kernel.search.Field.CREATE_DATE));
		RESERVED_COLUMNS.put("modifiedUserId",
				new DDLRecordMetaFieldInfo("reservedModifiedUserId","last-changed-by", "long",
						null));
		RESERVED_COLUMNS.put("modifiedUserName",
				new DDLRecordMetaFieldInfo("reservedModifiedUserName","last-changed-by", "String",
						null));
		RESERVED_COLUMNS.put("modifiedDate",
				new DDLRecordMetaFieldInfo("reservedModifiedDate","modified-date", java.util.Date.class.getName(),
						com.liferay.portal.kernel.search.Field.MODIFIED_DATE));
		RESERVED_COLUMNS.put("status",
				new DDLRecordMetaFieldInfo("reservedStatus","status", "int",
						com.liferay.portal.kernel.search.Field.STATUS));
		RESERVED_COLUMNS.put("uuid",
				new DDLRecordMetaFieldInfo("reservedUuid","uuid", "String",
						null));
		

		FIELD_DATA_TYPE_CLASSNAME = new HashMap<String,String>();
		FIELD_DATA_TYPE_CLASSNAME.put(FieldConstants.BOOLEAN, Boolean.class.getName());
		FIELD_DATA_TYPE_CLASSNAME.put(FieldConstants.DATE, java.util.Date.class.getName());
//		FIELD_DATA_TYPE_CLASSNAME.put(FieldConstants.DOCUMENT_LIBRARY, String.class.getName());
		FIELD_DATA_TYPE_CLASSNAME.put(FieldConstants.DOUBLE, Double.class.getName());
		// TODO: what happened to the FILE_UPLOAD field?
//		FIELD_DATA_TYPE_CLASSNAME.put(FieldConstants..FILE_UPLOAD, String.class.getName());
		FIELD_DATA_TYPE_CLASSNAME.put(FieldConstants.FLOAT, Float.class.getName());
		FIELD_DATA_TYPE_CLASSNAME.put(FieldConstants.INTEGER, Integer.class.getName());
		FIELD_DATA_TYPE_CLASSNAME.put(FieldConstants.LONG, Long.class.getName());
		FIELD_DATA_TYPE_CLASSNAME.put(FieldConstants.NUMBER, BigDecimal.class.getName());
		FIELD_DATA_TYPE_CLASSNAME.put(FieldConstants.SHORT, Short.class.getName());
//		FIELD_DATA_TYPE_CLASSNAME.put(FieldConstants.STRING, String.class.getName());
	}
	
	private static class DDLRecordMetaFieldInfo {
		private String modelFieldName;
		private String resourceKey;
		private String fieldDataType;
		private String indexFieldName;
		
		public DDLRecordMetaFieldInfo(String modelFieldName,
				String resourceKey, String fieldType, String indexFieldName ) {
			this.modelFieldName = modelFieldName;
			this.resourceKey = resourceKey;
			this.fieldDataType = fieldType;
			this.indexFieldName = indexFieldName;
		}
		
		@SuppressWarnings("unused")
		public String getModelFieldName() {
			return modelFieldName;
		}

		public String getResourceKey() {
			return resourceKey;
		}

		public String getFieldDataType() {
			return fieldDataType;
		}

		public String getIndexFieldName() {
			return indexFieldName;
		}

	}
	
	/**
	 * Get a data model for the given DDL record for render template access.
	 *
	 * TODO Miles make the returned Map as an lazy loading Map to optimize performance.
	 * TODO Miles make the model map cached in session. Navigation from list to view/edit should
	 * be able to reuse the parsed result. However update detection need be smart (expire check: versionId + timestamp).
	 * 
	 * @param themeDisplay
	 * @param record
	 * @param latestRecordVersion
	 * @return the render model
	 * @throws PortalException
	 * @throws SystemException
	 */
	public static Map<String, TemplateNode> getRecordRenderModel( ThemeDisplay themeDisplay,
			DDLRecord record, boolean latestRecordVersion) throws PortalException, SystemException {

		Map<String, TemplateNode> dataModel = new LinkedHashMap<String, TemplateNode>();

		DDLRecordSet recordSet = record.getRecordSet();

		// TODO Miles structure might be filtered by display template contained fields?
		DDMStructure ddmStructure = recordSet.getDDMStructure();

		DDLRecordVersion recordVersion = record.getRecordVersion();

		if (latestRecordVersion) {
			recordVersion = record.getLatestRecordVersion();
		}

//		getStructureFields(
//				themeDisplay, columns, ddmStructure);
		
		TemplateNode fieldNode = new TemplateNode(themeDisplay, "reservedDisplayIndex",
				String.valueOf(record.getDisplayIndex()), FieldConstants.INTEGER, 
				DDMFieldTypes.TYPE_INT_NUMBER, record.getDisplayIndex(), "reservedDisplayIndex");
		dataModel.put(fieldNode.getName(), fieldNode);
		
		fieldNode = new TemplateNode(themeDisplay, "reservedRecordId",
				String.valueOf(record.getRecordId()), FieldConstants.LONG,
				DDMFieldTypes.TYPE_INT_NUMBER, record.getRecordId(), "reservedRecordId");
		dataModel.put(fieldNode.getName(), fieldNode);
		
		fieldNode = new TemplateNode(themeDisplay, "reservedCreateUserId",
				String.valueOf(record.getUserId()), FieldConstants.LONG,
				DDMFieldTypes.TYPE_INT_NUMBER, record.getUserId(), "reservedCreateUserId");
		dataModel.put(fieldNode.getName(), fieldNode);

		String userName = PortalUtil.getUserName(record.getUserId(), record.getUserName());
		fieldNode = new TemplateNode(themeDisplay, "reservedCreateUserName",
				userName, FieldConstants.STRING,
				DDMFieldTypes.TYPE_TEXT, userName, "reservedCreateUserName");
		dataModel.put(fieldNode.getName(), fieldNode);

		Format dateFormatDateTime = FastDateFormatFactoryUtil.getDateTime(
				themeDisplay.getLocale(), themeDisplay.getTimeZone());

		String date = dateFormatDateTime.format(record.getCreateDate());
		fieldNode = new TemplateNode(themeDisplay, "reservedCreateDate",
				date, FieldConstants.DATE,
				DDMFieldTypes.TYPE_DDM_DATE, record.getCreateDate(), "reservedCreateDate");
		dataModel.put(fieldNode.getName(), fieldNode);

		String uuid = record.getUuid();
		fieldNode = new TemplateNode(themeDisplay, "reservedUuid",
				uuid, FieldConstants.STRING,
				DDMFieldTypes.TYPE_TEXT, uuid, "reservedUuid");
		dataModel.put(fieldNode.getName(), fieldNode);
		
		long groupId = record.getGroupId();
		fieldNode = new TemplateNode(themeDisplay, "reservedGroupId",
				String.valueOf(groupId), FieldConstants.LONG,
				DDMFieldTypes.TYPE_INT_NUMBER, groupId, "reservedGroupId");
		dataModel.put(fieldNode.getName(), fieldNode);
		
		fieldNode = new TemplateNode(themeDisplay, "reservedModifiedUserId",
				String.valueOf(recordVersion.getUserId()), FieldConstants.LONG,
				DDMFieldTypes.TYPE_INT_NUMBER, recordVersion.getUserId(), "reservedModifiedUserId");
		dataModel.put(fieldNode.getName(), fieldNode);

		userName = PortalUtil.getUserName(recordVersion.getUserId(), recordVersion.getUserName());
		fieldNode = new TemplateNode(themeDisplay, "reservedModifiedUserName",
				userName, FieldConstants.STRING,
				DDMFieldTypes.TYPE_TEXT, userName, "reservedModifiedUserName");
		dataModel.put(fieldNode.getName(), fieldNode);

		date = dateFormatDateTime.format(record.getModifiedDate());
		fieldNode = new TemplateNode(themeDisplay, "reservedModifiedDate",
				date, FieldConstants.DATE,
				DDMFieldTypes.TYPE_DDM_DATE, record.getModifiedDate(), "reservedModifiedDate");
		dataModel.put(fieldNode.getName(), fieldNode);

		String status = LanguageUtil.get(themeDisplay.getLocale(), 
				WorkflowConstants.getStatusLabel(recordVersion.getStatus()));
		fieldNode = new TemplateNode(themeDisplay, "reservedStatus",
				status, FieldConstants.STRING,
				DDMFieldTypes.TYPE_TEXT, status, "reservedStatus");
		dataModel.put(fieldNode.getName(), fieldNode);
		
		Fields fieldsModel = StorageEngineUtil.getFields(
				recordVersion.getDDMStorageId());

		Map<String, Map<String, String>> fieldsMap = ddmStructure.getFieldsMap(themeDisplay.getLanguageId());

		for (Map<String, String> fieldMap : fieldsMap.values()) {
			String dataType = fieldMap.get(FieldConstants.DATA_TYPE);
			String label = fieldMap.get(FieldConstants.LABEL);
			String name = fieldMap.get(FieldConstants.NAME);
			String data = StringPool.BLANK;
			String type = fieldMap.get(FieldConstants.TYPE);
			Serializable value = null;
			
			if (type==null)
				continue;
			
			if (fieldsModel.contains(name)) {
				Field field = fieldsModel.get(name);
				data = field.getRenderedValue(themeDisplay.getLocale());
				Locale locale = themeDisplay.getLocale();
				Set<Locale> availableLocales = field.getAvailableLocales();
				if ( !availableLocales.contains(locale)) {
					locale = field.getDefaultLocale();
					if ( !availableLocales.contains(locale)) {
						locale = LocaleUtil.getSiteDefault();
						if ( !availableLocales.contains(locale)) {
							if ( availableLocales.size()>0) 
								locale = availableLocales.iterator().next();
						}
					}
				}
				if ( locale != null ) {
					value = field.getValue(locale);
				}
				if ( value == null ) {
					if (_log.isDebugEnabled()) {
						String structureName = ddmStructure.getName();
						String fieldName = field.getName();
						_log.debug("field.getValue(defaultLocale) is null: Structure "+structureName+"."+fieldName+", defaultLocale="+
								field.getDefaultLocale()
								+", avaliableLocales="+field.getAvailableLocales()
								+", valuesMap="+field.getValuesMap());
					}
				}
			}

			fieldNode = new TemplateNode(themeDisplay, name, data, type, dataType, value, label);
			dataModel.put(name, fieldNode);
			
		}
		
		return dataModel;
	}

	/**
	 * Get structure defined fields as a map.
	 * Reserved columns are not included in the map.
	 * @param ddmStructure
	 * @param languageId
	 * @return
	 * Fields map. The key of the map is field name. The value of the map is field's label
	 * in the given language Id.
	 * @throws SystemException 
	 * @throws PortalException 
	 */
	public static Map<String, String> getStructureDefinedFields(
			DDMStructure ddmStructure, String languageId) throws PortalException, SystemException {
		return getStructureDefinedFields(ddmStructure, languageId, false);
	}
	
	/**
	 * Get structure defined fields as a map.
	 * Reserved columns are not included in the map.
	 * @param ddmStructure
	 * @param languageId
	 * @param includePrivate
	 * @return
	 * Fields map. The key of the map is field name. The value of the map is field's label
	 * in the given language Id.
	 * @throws SystemException 
	 * @throws PortalException 
	 */
	public static Map<String, String> getStructureDefinedFields(
			DDMStructure ddmStructure, String languageId, boolean includePrivate) throws PortalException, SystemException {
		Map<String,String> fields = new LinkedHashMap<String,String>(); 
		Map<String, Map<String, String>> fieldsMap = ddmStructure.getFieldsMap(
				languageId);
		for (Map<String, String> fieldMap : fieldsMap.values()) {
			String label = fieldMap.get(FieldConstants.LABEL);
			String name = fieldMap.get(FieldConstants.NAME);
			if ( !includePrivate ) {
//				Following logic was get from LP6.0, and is replaced by new logic get 
//				from LP 6.2.x
				if (GetterUtil.getBoolean(fieldMap.get(FieldConstants.PRIVATE))) {
					continue;
				}
			}
			fields.put(name, label);
		}
		return fields;
	}
	
	/**
	 * Get DDL record's fields as a map.
	 * The reserved fields and structure defined fields are all included in the map.
	 * @param record
	 * @param languageId
	 * @return
	 * Fields map. The key of the map is field name. The value of the map is field's label
	 * in the given language Id.
	 * @throws PortalException
	 * @throws SystemException
	 */
	public static Map<String,String> getColumns(DDLRecord record, String languageId) throws PortalException, SystemException {
		DDLRecordSet recordSet = record.getRecordSet();
		return getColumns(recordSet, languageId);
	}

	/**
	 * Get DDL record set's fields as a map.
	 * The reserved fields and structure defined fields are all included in the map.
	 * 
	 * @param recordSet
	 * @param languageId
	 * @return
	 * Fields map. The key of the map is field name. The value of the map is field's label
	 * in the given language Id.
	 * @throws PortalException
	 * @throws SystemException
	 */
	public static Map<String,String> getColumns(DDLRecordSet recordSet, String languageId) throws PortalException, SystemException {
		DDMStructure ddmStructure = recordSet.getDDMStructure();
		return getColumns(ddmStructure, languageId);
	}

	/**
	 * Get DDL structure's fields as a map.
	 * The reserved fields and structure defined fields are all included in the map.
	 * 
	 * @param ddmStructure
	 * @param languageId
	 * @return
	 * Fields map. The key of the map is field name. The value of the map is field's label
	 * in the given language Id.
	 * @throws SystemException 
	 * @throws PortalException 
	 */
	public static Map<String,String> getColumns(DDMStructure ddmStructure, String languageId) throws PortalException, SystemException {
		Map<String,String> columns = getStructureDefinedFields(ddmStructure, languageId); 
		addReservedColumns(columns, languageId);
		return columns;
	}
	
	private static void addReservedColumns(Map<String,String> columns, String languageId) {
		Locale locale = LocaleUtil.fromLanguageId(languageId);
		for ( String key: RESERVED_COLUMNS.keySet() ) {
			DDLRecordMetaFieldInfo temp = RESERVED_COLUMNS.get(key);
			String fieldName = temp.modelFieldName;
			String value = LanguageUtil.get(locale, temp.getResourceKey());
			columns.put(fieldName, value);
		}
	}
	
	public static String getRecordMetaFieldDataType(String fieldName) {
		DDLRecordMetaFieldInfo info = RESERVED_COLUMNS.get(fieldName);
		if ( info == null ) return null;
		return info.getFieldDataType();
	}

	public static String getRecordMetaFieldIndexName(String fieldName) {
		DDLRecordMetaFieldInfo info = RESERVED_COLUMNS.get(fieldName);
		if ( info == null ) return null;
		return info.getIndexFieldName();
	}
		
	public static String getRecordFieldClassName(DDLRecordSet recordSet, String fieldName)
			throws PortalException, SystemException {
		DDMStructure structure = recordSet.getDDMStructure();
		String fieldDataType = structure.getFieldDataType(fieldName);
		String className = FIELD_DATA_TYPE_CLASSNAME.get(fieldDataType);
		if ( className == null ) {
			return String.class.getName();
		}
		return className;
	}
	
	public static String getRecordFieldIndexName(DDMStructure structure, String fieldName, Locale locale, boolean ignoreCase)
			throws PortalException, SystemException {
		long structureId = structure.getStructureId();
		// TODO: do we still need extension to index keyword field ignore case?
//		try {
//			if ( isKeywordStringField(structure, fieldName )) {
//				return DDMKeywordIndexerUtil.encodeName(structureId, fieldName, ignoreCase);
//			}
			return DDMIndexerUtil.encodeName(structureId, fieldName, locale);
//		} catch (StructureFieldException e) {
//			return null;
//		}
	}

//	TODO: implement according to LP6.2 field setting
//	public static boolean isKeywordStringField(DDMStructure structure,
//			String fieldName) throws PortalException, SystemException {
//		structure.getField
//		String type = structure.getFieldType(fieldName);
//		String dataType = structure.getFieldDataType(fieldName);
//		if ( FieldConstants.STRING.equals(dataType)
//				&& !"textarea".equals(type)) {
//			return true;
//		}
//		return false;
//	}
	
}
