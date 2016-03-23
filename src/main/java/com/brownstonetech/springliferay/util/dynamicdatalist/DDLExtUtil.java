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
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
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

public class DDLExtUtil extends DDLUtil {

	private static final DDLRecordMetaFieldInfo[] RESERVED_COLUMNS;
	private static final Map<String,DDLRecordMetaFieldInfo> RESERVED_COLUMNS_MAP;
	private static final Map<String,String> FIELD_DATA_TYPE_CLASSNAME;
	private static Map<String, StructureRenderModelTypeHandler> handlers;
	private static Log _log = LogFactoryUtil.getLog(DDLExtUtil.class);

	public static final Map<String, String> STATUS_OPTIONS;
	public static final String STRUCTURE_FIELD_OPTIONS = "options";
	
	static {
		STATUS_OPTIONS = new LinkedHashMap<String,String>();
		STATUS_OPTIONS.put(String.valueOf(WorkflowConstants.STATUS_ANY), WorkflowConstants.LABEL_ANY);
		STATUS_OPTIONS.put(String.valueOf(WorkflowConstants.STATUS_APPROVED), WorkflowConstants.LABEL_APPROVED);
		STATUS_OPTIONS.put(String.valueOf(WorkflowConstants.STATUS_PENDING), WorkflowConstants.LABEL_PENDING);
		STATUS_OPTIONS.put(String.valueOf(WorkflowConstants.STATUS_DRAFT), WorkflowConstants.LABEL_DRAFT);
		STATUS_OPTIONS.put(String.valueOf(WorkflowConstants.STATUS_EXPIRED), WorkflowConstants.LABEL_EXPIRED);
		STATUS_OPTIONS.put(String.valueOf(WorkflowConstants.STATUS_DENIED), WorkflowConstants.LABEL_DENIED);
		STATUS_OPTIONS.put(String.valueOf(WorkflowConstants.STATUS_INACTIVE), WorkflowConstants.LABEL_INACTIVE);
		STATUS_OPTIONS.put(String.valueOf(WorkflowConstants.STATUS_INCOMPLETE), WorkflowConstants.LABEL_INCOMPLETE);
		STATUS_OPTIONS.put(String.valueOf(WorkflowConstants.STATUS_SCHEDULED), WorkflowConstants.LABEL_SCHEDULED);
		STATUS_OPTIONS.put(String.valueOf(WorkflowConstants.STATUS_IN_TRASH), WorkflowConstants.LABEL_IN_TRASH);

		RESERVED_COLUMNS = new DDLRecordMetaFieldInfo[] {
				new DDLRecordMetaFieldInfo("displayIndex", "display-index", DDMFieldTypes.TYPE_INT_NUMBER, FieldConstants.INTEGER,null),
				new DDLRecordMetaFieldInfo("recordId","id", DDMFieldTypes.TYPE_INT_NUMBER, FieldConstants.LONG, 
						com.liferay.portal.kernel.search.Field.ENTRY_CLASS_PK),
				new DDLRecordMetaFieldInfo("createUserId","author", DDMFieldTypes.TYPE_INT_NUMBER, FieldConstants.LONG, 
						com.liferay.portal.kernel.search.Field.USER_ID),
				new DDLRecordMetaFieldInfo("createUserName","author", DDMFieldTypes.TYPE_TEXT, FieldConstants.STRING,
						com.liferay.portal.kernel.search.Field.USER_NAME),
				new DDLRecordMetaFieldInfo("createDate","create-date", DDMFieldTypes.TYPE_DDM_DATE, FieldConstants.DATE,
						com.liferay.portal.kernel.search.Field.CREATE_DATE),
				new DDLRecordMetaFieldInfo("modifiedUserId","last-changed-by", DDMFieldTypes.TYPE_INT_NUMBER, FieldConstants.LONG,
						null),
				new DDLRecordMetaFieldInfo("modifiedUserName","last-changed-by", DDMFieldTypes.TYPE_TEXT, FieldConstants.STRING,
						null),
				new DDLRecordMetaFieldInfo("modifiedDate","modified-date", DDMFieldTypes.TYPE_DDM_DATE, FieldConstants.DATE,
						com.liferay.portal.kernel.search.Field.MODIFIED_DATE),
				new DDLRecordMetaFieldInfo("status","status", DDMFieldTypes.TYPE_INT_NUMBER, FieldConstants.INTEGER, 
						com.liferay.portal.kernel.search.Field.STATUS,STATUS_OPTIONS),
				new DDLRecordMetaFieldInfo("uuid","uuid", DDMFieldTypes.TYPE_TEXT, FieldConstants.STRING,
						null)
		};
		
		RESERVED_COLUMNS_MAP = new LinkedHashMap<String,DDLRecordMetaFieldInfo>();
		for (DDLRecordMetaFieldInfo fieldInfo: RESERVED_COLUMNS) {
			RESERVED_COLUMNS_MAP.put(fieldInfo.getModelFieldName(), fieldInfo);
		}

		FIELD_DATA_TYPE_CLASSNAME = new HashMap<String,String>();
		FIELD_DATA_TYPE_CLASSNAME.put(FieldConstants.BOOLEAN, Boolean.class.getName());
		FIELD_DATA_TYPE_CLASSNAME.put(FieldConstants.DATE, java.util.Date.class.getName());
//		FIELD_DATA_TYPE_CLASSNAME.put(FieldConstants.DOCUMENT_LIBRARY, String.class.getName());
		FIELD_DATA_TYPE_CLASSNAME.put(FieldConstants.DOUBLE, Double.class.getName());
		FIELD_DATA_TYPE_CLASSNAME.put(FieldConstants.FLOAT, Float.class.getName());
		FIELD_DATA_TYPE_CLASSNAME.put(FieldConstants.INTEGER, Integer.class.getName());
		FIELD_DATA_TYPE_CLASSNAME.put(FieldConstants.LONG, Long.class.getName());
		FIELD_DATA_TYPE_CLASSNAME.put(FieldConstants.NUMBER, BigDecimal.class.getName());
		FIELD_DATA_TYPE_CLASSNAME.put(FieldConstants.SHORT, Short.class.getName());
//		FIELD_DATA_TYPE_CLASSNAME.put(FieldConstants.STRING, String.class.getName());
		
		handlers = new HashMap<String, StructureRenderModelTypeHandler>();
		StructureRenderModelTypeHandler selectHandler = new SelectRadioHandler();
		registerHandler(DDMFieldTypes.TYPE_SELECT, selectHandler);
		registerHandler(DDMFieldTypes.TYPE_RADIO, selectHandler);
	}
	
	public static class DDLRecordMetaFieldInfo {
		private String fieldName;
		private String modelFieldName;
		private String resourceKey;
		private String fieldDataType;
		private String type;
		private String indexFieldName;
		private Map<String,String> options;
		
		public DDLRecordMetaFieldInfo(String fieldName, 
				String resourceKey, String type, String fieldDataType, String indexFieldName ) {
			this.fieldName = fieldName;
			this.modelFieldName = "reserved"+StringUtil.upperCaseFirstLetter(fieldName);
			this.resourceKey = resourceKey;
			this.fieldDataType = fieldDataType;
			this.indexFieldName = indexFieldName;
			this.type = type;
		}
		
		public DDLRecordMetaFieldInfo(String fieldName, 
				String resourceKey, String type, String fieldDataType, String indexFieldName, Map<String,String> options) {
			this(fieldName, resourceKey, type, fieldDataType, indexFieldName);
			this.options = options;
		}

		/**
		 * Column name defined in DDLRecord entity
		 * @return
		 */
		public String getFieldName() {
			return fieldName;
		}

		/**
		 * Model field name used is rendering template. It's fieldName add "reserved" prefix
		 * @return
		 */
		public String getModelFieldName() {
			return modelFieldName;
		}
		
		/**
		 * Field name used by search engine indexing the DDLRecord field
		 * @return
		 */
		public String getIndexFieldName() {
			return indexFieldName;
		}

		public String getResourceKey() {
			return resourceKey;
		}

		public String getFieldDataType() {
			return fieldDataType;
		}

		public String getType() {
			return type;
		}
		
		public Map<String,String> getOptions() {
			return options;
		}
	}
	
	public interface StructureRenderModelTypeHandler {
		public void handle(Map<String, Serializable> structureFieldModel, 
				DDMStructure ddmStructure, String languageId);
	}

	public static void registerHandler(String type, StructureRenderModelTypeHandler handler) {
		handlers.put(type, handler);
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
		
		TemplateNode fieldNode = new TemplateNode(themeDisplay, null, "reservedDisplayIndex",
				String.valueOf(record.getDisplayIndex()), FieldConstants.INTEGER, 
				DDMFieldTypes.TYPE_INT_NUMBER, record.getDisplayIndex(), "reservedDisplayIndex");
		dataModel.put(fieldNode.getName(), fieldNode);
		
		fieldNode = new TemplateNode(themeDisplay, null, "reservedRecordId",
				String.valueOf(record.getRecordId()), FieldConstants.LONG,
				DDMFieldTypes.TYPE_INT_NUMBER, record.getRecordId(), "reservedRecordId");
		dataModel.put(fieldNode.getName(), fieldNode);
		
		fieldNode = new TemplateNode(themeDisplay, null, "reservedCreateUserId",
				String.valueOf(record.getUserId()), FieldConstants.LONG,
				DDMFieldTypes.TYPE_INT_NUMBER, record.getUserId(), "reservedCreateUserId");
		dataModel.put(fieldNode.getName(), fieldNode);

		String userName = PortalUtil.getUserName(record.getUserId(), record.getUserName());
		fieldNode = new TemplateNode(themeDisplay, null, "reservedCreateUserName",
				userName, FieldConstants.STRING,
				DDMFieldTypes.TYPE_TEXT, userName, "reservedCreateUserName");
		dataModel.put(fieldNode.getName(), fieldNode);

		Format dateFormatDateTime = FastDateFormatFactoryUtil.getDateTime(
				themeDisplay.getLocale(), themeDisplay.getTimeZone());

		String date = dateFormatDateTime.format(record.getCreateDate());
		fieldNode = new TemplateNode(themeDisplay, null, "reservedCreateDate",
				date, FieldConstants.DATE,
				DDMFieldTypes.TYPE_DDM_DATE, record.getCreateDate(), "reservedCreateDate");
		dataModel.put(fieldNode.getName(), fieldNode);

		String uuid = record.getUuid();
		fieldNode = new TemplateNode(themeDisplay, null, "reservedUuid",
				uuid, FieldConstants.STRING,
				DDMFieldTypes.TYPE_TEXT, uuid, "reservedUuid");
		dataModel.put(fieldNode.getName(), fieldNode);
		
		long groupId = record.getGroupId();
		fieldNode = new TemplateNode(themeDisplay, null, "reservedGroupId",
				String.valueOf(groupId), FieldConstants.LONG,
				DDMFieldTypes.TYPE_INT_NUMBER, groupId, "reservedGroupId");
		dataModel.put(fieldNode.getName(), fieldNode);
		
		fieldNode = new TemplateNode(themeDisplay, null, "reservedModifiedUserId",
				String.valueOf(recordVersion.getUserId()), FieldConstants.LONG,
				DDMFieldTypes.TYPE_INT_NUMBER, recordVersion.getUserId(), "reservedModifiedUserId");
		dataModel.put(fieldNode.getName(), fieldNode);

		userName = PortalUtil.getUserName(recordVersion.getUserId(), recordVersion.getUserName());
		fieldNode = new TemplateNode(themeDisplay, null, "reservedModifiedUserName",
				userName, FieldConstants.STRING,
				DDMFieldTypes.TYPE_TEXT, userName, "reservedModifiedUserName");
		dataModel.put(fieldNode.getName(), fieldNode);

		date = dateFormatDateTime.format(record.getModifiedDate());
		fieldNode = new TemplateNode(themeDisplay, null, "reservedModifiedDate",
				date, FieldConstants.DATE,
				DDMFieldTypes.TYPE_DDM_DATE, record.getModifiedDate(), "reservedModifiedDate");
		dataModel.put(fieldNode.getName(), fieldNode);

		String status = LanguageUtil.get(themeDisplay.getLocale(), 
				WorkflowConstants.getStatusLabel(recordVersion.getStatus()));
		fieldNode = new TemplateNode(themeDisplay, null, "reservedStatus",
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

			fieldNode = new TemplateNode(themeDisplay, ddmStructure, name, data, type, dataType, value, label);
			dataModel.put(name, fieldNode);
			
		}
		
		return dataModel;
	}

//	/**
//	 * Get structure defined fields as a map.
//	 * Reserved columns are not included in the map.
//	 * @param ddmStructure
//	 * @param languageId
//	 * @return
//	 * Fields map. The key of the map is field name. The value of the map is field's label
//	 * in the given language Id.
//	 * @throws SystemException 
//	 * @throws PortalException 
//	 * @deprecated Replaced by {@link #getStructureRenderModel(DDMStructure, String, boolean)}
//	 */
//	public static Map<String, String> getStructureDefinedFields(
//			DDMStructure ddmStructure, String languageId) throws PortalException, SystemException {
//		return getStructureDefinedFields(ddmStructure, languageId, false);
//	}
//	
//	/**
//	 * Get structure defined fields as a map.
//	 * Reserved columns are not included in the map.
//	 * @param ddmStructure
//	 * @param languageId
//	 * @param includePrivate
//	 * @return
//	 * Fields map. The key of the map is field name. The value of the map is field's label
//	 * in the given language Id.
//	 * @throws SystemException 
//	 * @throws PortalException 
//	 * @deprecated Replaced by {@link #getStructureRenderModel(DDMStructure, String, boolean)}
//	 */
//	public static Map<String, String> getStructureDefinedFields(
//			DDMStructure ddmStructure, String languageId, boolean includePrivate) throws PortalException, SystemException {
//		Map<String,String> fields = new LinkedHashMap<String,String>(); 
//		Map<String, Map<String, String>> fieldsMap = ddmStructure.getFieldsMap(
//				languageId);
//		for (Map<String, String> fieldMap : fieldsMap.values()) {
//			String label = fieldMap.get(FieldConstants.LABEL);
//			String name = fieldMap.get(FieldConstants.NAME);
//			if ( !includePrivate ) {
//				if (GetterUtil.getBoolean(fieldMap.get(FieldConstants.PRIVATE))) {
//					continue;
//				}
//			}
//			fields.put(name, label);
//		}
//		return fields;
//	}

	/**
	 * Get structure defined fields as a map.
	 * Reserved columns are not included in the map.
	 * 
	 * @param ddmStructure
	 * @param languageId
	 * @param includePrivate
	 * @return
	 * Fields map. The key of the map is field name. The value of the map is field's meta
	 * data: name, label, type, dataType, options
	 * @throws PortalException
	 * @throws SystemException
	 */
	public static Map<String, Map<String, Serializable>> getStructureDefinedColumns(
			DDMStructure ddmStructure, String languageId, boolean includePrivate) throws PortalException, SystemException {
		Map<String, Map<String, String>> fieldsMap = ddmStructure.getFieldsMap(
				languageId);
		Map<String, Map<String, Serializable>> ret
			= new LinkedHashMap<String, Map<String,Serializable>>(fieldsMap.size());
		for ( String fieldName : fieldsMap.keySet()) {
			Map<String, String> fieldMap = fieldsMap.get(fieldName);
			if ( !includePrivate ) {
				if (GetterUtil.getBoolean(fieldMap.get(FieldConstants.PRIVATE))) {
					continue;
				}
			}
			Map<String, Serializable> structureFieldModel = new HashMap<String, Serializable>(fieldMap);
			ret.put(fieldName, structureFieldModel);
			String type = fieldMap.get(FieldConstants.TYPE);
			StructureRenderModelTypeHandler handler = handlers.get(type);
			if ( handler != null ) {
				handler.handle(structureFieldModel, ddmStructure, languageId);
			}
		}
		return ret;
		
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
	public static Map<String,Map<String,Serializable>> getColumns(DDLRecord record, String languageId) throws PortalException, SystemException {
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
	public static Map<String,Map<String,Serializable>> getColumns(DDLRecordSet recordSet, String languageId) throws PortalException, SystemException {
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
	public static Map<String,Map<String,Serializable>> getColumns(DDMStructure ddmStructure, String languageId) throws PortalException, SystemException {
		Map<String,Map<String, Serializable>> columns = getStructureDefinedColumns(ddmStructure, languageId, false); 
		addReservedColumns(columns, languageId);
		return columns;
	}
	
	private static void addReservedColumns(Map<String,Map<String,Serializable>> columns, String languageId) {
		Locale locale = LocaleUtil.fromLanguageId(languageId);
		for ( DDLRecordMetaFieldInfo temp: RESERVED_COLUMNS ) {
			String fieldName = temp.getModelFieldName();
			Map<String, Serializable> columnModel = new LinkedHashMap<String, Serializable>();
			columnModel.put(FieldConstants.NAME, fieldName);
			columnModel.put(FieldConstants.LABEL, LanguageUtil.get(locale, temp.getResourceKey()));
			columnModel.put(FieldConstants.TYPE, temp.getType());
			columnModel.put(FieldConstants.DATA_TYPE, temp.getFieldDataType());
			Map<String, String> options = temp.getOptions();
			if ( options != null ) {
				LinkedHashMap<String,String> localized = new LinkedHashMap<String,String>(options.size());
				for ( Map.Entry<String,String> optionEntry: options.entrySet()) {
					String localizedLabel = LanguageUtil.get(locale, optionEntry.getValue());
					localized.put(optionEntry.getKey(), localizedLabel);
				}
				columnModel.put(STRUCTURE_FIELD_OPTIONS, localized);
			}
			columns.put(fieldName, columnModel);
		}
	}
	
//	public static String getRecordMetaFieldDataType(String fieldName) {
//		DDLRecordMetaFieldInfo info = RESERVED_COLUMNS.get(fieldName);
//		if ( info == null ) return null;
//		String className = FIELD_DATA_TYPE_CLASSNAME.get(info.getFieldDataType());
//		if ( className == null ) {
//			return String.class.getName();
//		}
//		return className;
//	}
//
//	public static String getRecordMetaFieldIndexName(String fieldName) {
//		DDLRecordMetaFieldInfo info = RESERVED_COLUMNS.get(fieldName);
//		if ( info == null ) return null;
//		return info.getIndexFieldName();
//	}

	/**
	 * Get the java class name as string of the type matching with
	 * the specified field.
	 * <p>This method also handles reserved field anmes for DDLRecord
	 * </p>
	 * 
	 * @param recordSet
	 * @param fieldName
	 * @return
	 * @throws PortalException
	 * @throws SystemException
	 */
	public static String getFieldClassName(DDLRecordSet recordSet, String fieldName)
			throws PortalException, SystemException {
		String fieldDataType;
		if ( RESERVED_COLUMNS_MAP.containsKey(fieldName)) {
			// this is a reserved field, get answer from RESERVED_COLUMNS
			DDLRecordMetaFieldInfo info = RESERVED_COLUMNS_MAP.get(fieldName);
			fieldDataType = info.getFieldDataType();
		} else {
			DDMStructure structure = recordSet.getDDMStructure();
			fieldDataType = structure.getFieldDataType(fieldName);
		}
		String className = FIELD_DATA_TYPE_CLASSNAME.get(fieldDataType);
		if ( className == null ) {
			return String.class.getName();
		}
		return className;
	}

	/**
	 * Get the specified field's index field name used by search engine index.
	 * This method also handles reserved field names for DDLRecord 
	 * 
	 * @param structure
	 * @param fieldName
	 * @param locale
	 * @param ignoreCase
	 * @return
	 * @throws PortalException
	 * @throws SystemException
	 */
	public static String getFieldIndexName(DDMStructure structure, String fieldName, Locale locale, boolean ignoreCase)
			throws PortalException, SystemException {
		long structureId = structure.getStructureId();
		if ( RESERVED_COLUMNS_MAP.containsKey(fieldName)) {
			// this is a reserved field, get answer from RESERVED_COLUMNS
			DDLRecordMetaFieldInfo info = RESERVED_COLUMNS_MAP.get(fieldName);
			return info.getIndexFieldName();
		}
		return DDMKeywordIndexerUtil.encodeName(structureId, fieldName, locale, ignoreCase);
	}

	public static boolean isFieldKeywordIndexType(DDMStructure ddmStructure,
			String fieldName) throws PortalException, SystemException {
		String indexType = ddmStructure.getFieldProperty(
				fieldName, "indexType");

		String structureKey = ddmStructure.getStructureKey();

		if (structureKey.equals("TIKARAWMETADATA")) {
			indexType = "text";
		}

		if (Validator.isNull(indexType)) {
			return false;
		}
		
		return indexType.equals("keyword");
	}

	public static boolean isFieldLocalizable(DDMStructure ddmStructure, String fieldName)
		throws PortalException, SystemException {
		boolean localizable = GetterUtil.getBoolean(
				ddmStructure.getFieldProperty(fieldName, "localizable"), true);
		return localizable;
	}
	
}
