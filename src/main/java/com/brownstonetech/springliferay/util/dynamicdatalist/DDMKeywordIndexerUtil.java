package com.brownstonetech.springliferay.util.dynamicdatalist;

import java.io.Serializable;
import java.util.Locale;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portlet.dynamicdatamapping.model.DDMStructure;
import com.liferay.portlet.dynamicdatamapping.storage.Field;
import com.liferay.portlet.dynamicdatamapping.storage.Fields;

import com.liferay.portlet.dynamicdatamapping.util.DDMIndexerUtil;

/**
 * Add support for case insensitive index support for keyword fields
 * 
 * @author Miles Huang
 *
 */
public class DDMKeywordIndexerUtil {
	
	private static Log _log = LogFactoryUtil.getLog(DDMKeywordIndexerUtil.class);
	
	public static final String DDM_LC_FIELD_PREFIX = "ddmlc";
	public static final String DDM_FIELD_SEPARATOR = StringPool.FORWARD_SLASH;
	
	/**
	 * Simply add an indexed single value keyword field to the document.
	 * Logic is same as
	 * {@link com.liferay.portal.kernel.search.DocumentImpl#createKeywordField(String, String, boolean)}
	 * but that method is not public accessible.
	 * 
	 * @param document
	 * @param name
	 * @param value
	 * @param lowerCase
	 */
	public static void addKeyword(Document document, String name, String value, boolean lowerCase) {
		if (lowerCase && Validator.isNotNull(value)) {
			value = StringUtil.toLowerCase(value);
		}

		com.liferay.portal.kernel.search.Field field = new com.liferay.portal.kernel.search.Field(name, value);
		document.add(field);

		for (String fieldName : com.liferay.portal.kernel.search.Field.UNSCORED_FIELD_NAMES) {
			if (StringUtil.equalsIgnoreCase(name, fieldName)) {
				field.setBoost(0);
			}
		}
	}
	
	/**
	 * Simply add an indexed multi-value keyword field to the document.
	 * @param document
	 * @param name
	 * @param values
	 * @param lowerCase
	 */
	public static void addKeyword(Document document, String name, String[] values, boolean lowerCase) {
		if ( lowerCase && values != null ) {
			String[] lc = new String[values.length];
			for (int i = 0; i < values.length; i++) {
				lc[i] = StringUtil.toLowerCase(values[i]);
			}
			values = lc;
		}
		document.addKeyword(name,  values);
	}
	
	public static String encodeName(long ddmStructureId, String fieldName, Locale locale, boolean lowerCase) {
		if ( !lowerCase ) {
			return DDMIndexerUtil.encodeName(ddmStructureId, fieldName, locale);
		}
		
		StringBundler sb = new StringBundler(7);

		sb.append(DDM_LC_FIELD_PREFIX);
		sb.append(ddmStructureId);
		sb.append(DDM_FIELD_SEPARATOR);
		sb.append(fieldName);

		if (Validator.isNotNull(locale)) {
			sb.append(StringPool.UNDERLINE);
			sb.append(LocaleUtil.toLanguageId(locale));
		}

		return sb.toString();
	}
	
	public static void addAttributes(
			Document document, DDMStructure ddmStructure, Fields fields) {

		long groupId = GetterUtil.getLong(
				document.get(com.liferay.portal.kernel.search.Field.GROUP_ID));

		Locale[] locales = LanguageUtil.getAvailableLocales(groupId);

		for (Field field : fields) {
			try {
				String indexType = ddmStructure.getFieldProperty(
						field.getName(), "indexType");

				String structureKey = ddmStructure.getStructureKey();

				if (structureKey.equals("TIKARAWMETADATA")) {
					indexType = "text";
				}

				if (Validator.isNull(indexType) || !indexType.equals("keyword")) {
					continue;
				}
				Locale[] applicableLocales = locales;
				if ( DDLExtUtil.isFieldLocalizable(ddmStructure, field.getName())) {
					applicableLocales = new Locale[]{LocaleUtil.getSiteDefault()};
				}
				for (Locale locale : applicableLocales) {
					String name = encodeName(
							ddmStructure.getStructureId(), 
							field.getName(), locale, true);

					Serializable value = field.getValue(locale);

					if (value instanceof Object[]) {
						String[] valuesString = ArrayUtil.toStringArray(
								(Object[])value);
						addKeyword(document, name, valuesString, true);
					} else if (value instanceof String ) {
						String valueString = String.valueOf(value);
						valueString = valueString == null? null
								:valueString.toLowerCase(locale);
						String type = field.getType();

						if (type.equals(DDMFieldTypes.TYPE_RADIO) ||
								type.equals(DDMFieldTypes.TYPE_SELECT)) {

							JSONArray jsonArray =
									JSONFactoryUtil.createJSONArray(valueString);

							String[] stringArray = ArrayUtil.toStringArray(
									jsonArray);

							addKeyword(document, name, stringArray, true);
						}
						else {
							if (type.equals(DDMFieldTypes.TYPE_DDM_TEXT_HTML)) {
								valueString = HtmlUtil.extractText(valueString);
							}

							addKeyword(document, name, valueString, true);
						}
					}
				}
			}
			catch (Exception e) {
				if (_log.isWarnEnabled()) {
					_log.warn(e, e);
				}
			}
		}

	}

}
