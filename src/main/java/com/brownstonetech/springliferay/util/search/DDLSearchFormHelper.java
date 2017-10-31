package com.brownstonetech.springliferay.util.search;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.brownstonetech.springliferay.util.dynamicdatalist.DDLExtUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.ParseException;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portlet.dynamicdatamapping.model.DDMStructure;

public class DDLSearchFormHelper extends SearchFormHelper {

	private DDMStructure ddmStructure;
	
	public static final String RECORD_SET_ID="recordSetId";
	public static final String RECORD_ID="recordId";
	
	private static Collection<String> DENIED_FIELD_NAMES = 
			Arrays.asList(
					".reserved"+StringUtil.upperCaseFirstLetter(Field.ASSET_CATEGORY_IDS),
					".reserved"+StringUtil.upperCaseFirstLetter(Field.ASSET_TAG_NAMES),
					".reserved"+StringUtil.upperCaseFirstLetter(Field.CATEGORY_ID),
					".reserved"+StringUtil.upperCaseFirstLetter(Field.COMPANY_ID),
					".reserved"+StringUtil.upperCaseFirstLetter(Field.ENTRY_CLASS_NAME),
					".reserved"+StringUtil.upperCaseFirstLetter(Field.FOLDER_ID),
					".reserved"+StringUtil.upperCaseFirstLetter(Field.GROUP_ID),
					".reserved"+StringUtil.upperCaseFirstLetter(Field.GROUP_ROLE_ID),
					".reserved"+StringUtil.upperCaseFirstLetter(Field.NODE_ID),
					".reserved"+StringUtil.upperCaseFirstLetter(Field.PORTLET_ID),
					".reserved"+StringUtil.upperCaseFirstLetter(Field.ROLE_ID),
					".reserved"+StringUtil.upperCaseFirstLetter(Field.ROOT_ENTRY_CLASS_PK),
					".reserved"+StringUtil.upperCaseFirstLetter(Field.SCOPE_GROUP_ID),
					".reserved"+StringUtil.upperCaseFirstLetter(Field.TYPE),
					".reserved"+StringUtil.upperCaseFirstLetter(Field.UID),
					".reserved"+StringUtil.upperCaseFirstLetter(RECORD_SET_ID),
					".reserved"+StringUtil.upperCaseFirstLetter(RECORD_ID)
					);

	public DDLSearchFormHelper(DDMStructure structure, Locale locale, SearchContext searchContext, String ... propertiesExclude ) {
		super(locale, searchContext, propertiesExclude);
		this.fieldNameBlackList.addAll(DENIED_FIELD_NAMES);
		this.ddmStructure = structure;
	}

	@Override
	protected String encodeNumericRangeIndexFieldName(String indexFieldName) {
		return indexFieldName+"_Number_sortable";
	}
	
	@Override
	protected String getIndexFieldName(String propertyPath, String propertyName, boolean like)
			throws PortalException, SystemException {
		if ( Validator.isNull(propertyPath)) {
			String indexFieldName = DDLExtUtil.getFieldIndexName(ddmStructure, propertyName, locale, like);
			if ( indexFieldName != null ) return indexFieldName;
		}
		return super.getIndexFieldName(propertyPath, propertyName, like);
	}

//	@Override
//	protected boolean isBean(String propertyPath) {
//		if ( ".reserved".equals(propertyPath) ) {
//			return true;
//		}
//		return super.isBean(propertyPath);
//	}

	/**
	 * This method process the passed in search terms as a map and generate
	 * query according to the search terms.
	 * <p>
	 * The key of search terms is treated as DDL record field name, with optional
	 * suffixes:
	 * <table summary="Special search field name suffix">
	 * <tr><th>Suffix</th><th>Description</th></tr>
	 * <tr><th></th><td>No suffix means equals match</td></tr>
	 * <tr><th>_eq</th><td>Same as no suffix equals match</td></tr>
	 * <tr><th>_lt</th><td>Less than criteria</td></tr>
	 * <tr><th>_gt</th><td>Greater than criteria</td></tr>
	 * <tr><th>_lte</th><td>Greater than or equals criteria</td></tr>
	 * <tr><th>_gte</th><td>Less than or equals criteria</td></tr>
	 * <tr><th>_like</th><td>Like criteria</td></tr>
	 * </table>
	 * 
	 * 
	 * @param andOperator if true all of the given criteria in the searchTerms must matches
	 * @param searchTerms The serchTerms map, each entry in the map provides a match criteria.
	 * @return A boolean query which represents the given search terms.
	 * @throws ParseException
	 */
	public BooleanQuery createFieldSearchTerms(boolean andOperator,
			Map<String, Object> searchTerms) throws ParseException {
		Set<String> blackList = Collections.emptySet();
		BooleanQuery dynaFieldQuery = createQuery(StringPool.BLANK, 
				andOperator, searchTerms, blackList);
		return dynaFieldQuery;
	}

	public BooleanQuery createReservedFieldSearchTerms(boolean andOperator,
			Map<String, Object> searchTerms) throws ParseException {
		Set<String> blackList = Collections.emptySet();
		BooleanQuery dynaFieldQuery = createQuery(".reserved", 
				andOperator, searchTerms, blackList);
		return dynaFieldQuery;
	}

	public void combineFieldSearchTerms(BooleanQuery additionalQuery, boolean andOperator,
			Map<String, Object> searchTerms) throws ParseException {
		BooleanQuery combined = createFieldSearchTerms(andOperator, searchTerms);
		combineAllTo(additionalQuery, combined);
	}
	
	public void combineReservedFieldSearchTerms(BooleanQuery additionalQuery, boolean andOperator,
			Map<String, Object> searchTerms) throws ParseException {
		BooleanQuery combined = createReservedFieldSearchTerms(andOperator, searchTerms);
		combineAllTo(additionalQuery, combined);
	}
	
}
