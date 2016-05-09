package com.brownstonetech.springliferay.util.search;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.brownstonetech.springliferay.exception.UnexpectedPortalException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.search.BooleanClause;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.BooleanQueryFactoryUtil;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.ParseException;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portlet.dynamicdatalists.model.DDLRecord;
import com.liferay.portlet.dynamicdatalists.model.DDLRecordSet;
import com.liferay.portlet.dynamicdatalists.service.DDLRecordLocalServiceUtil;
import com.liferay.portlet.dynamicdatalists.util.DDLUtil;
import com.liferay.portlet.dynamicdatamapping.model.DDMStructure;

public class DDLSearchUtil {

	public static List<DDLRecord> runDDLSearch(DDLRecordSet recordSet, Map<String,Object> searchCriteria,
			int start, int end) throws SystemException {
		Locale locale = LocaleUtil.fromLanguageId(recordSet.getDefaultLanguageId());
		SearchContext searchContext = prepareDDLSearchContext(recordSet.getUserId(), 
				recordSet, locale);
		
		DDMStructure structure;
		try {
			structure = recordSet.getDDMStructure();
		} catch (PortalException e1) {
			throw new UnexpectedPortalException(e1);
		}
		DDLSearchFormHelper helper = new DDLSearchFormHelper(
				structure, locale, searchContext);

		BooleanQuery additionalQuery = BooleanQueryFactoryUtil.create(searchContext);
		try {
			helper.combineFieldSearchTerms(additionalQuery, true, 
					searchCriteria);
		} catch (ParseException e1) {
			throw new SystemException("Invalid query criteria: "+JSONFactoryUtil.looseSerializeDeep(searchCriteria), e1);
		}
		
		searchContext.setBooleanClauses(new BooleanClause[]{
			new TransferBooleanClause(additionalQuery, BooleanClauseOccur.MUST)
		});
		
		searchContext.setStart(start);
		searchContext.setEnd(end);
		Hits hits = DDLRecordLocalServiceUtil.search(searchContext);
		int total = hits.getLength();
		if ( total > 0 ) {
			try {
				List<DDLRecord> results = DDLUtil.getRecords(hits);
				return results;
			} catch (Exception e) {
				throw new SystemException("Failed convert search hits to DDLRecord list", e);
			}
		}
		return Collections.emptyList();
	}
	
	private static SearchContext prepareDDLSearchContext(long userId, DDLRecordSet recordSet,
			Locale locale) {
		SearchContext searchContext = new SearchContext();
		searchContext.setCompanyId(recordSet.getCompanyId());
		searchContext.setGroupIds(new long[]{recordSet.getGroupId()});
//		searchContext.setLayout(layout);
		searchContext.setLocale(locale);
//		searchContext.setTimeZone(timeZone);
		searchContext.setUserId(userId);

		// DDLRecordSet
		searchContext.setAttribute("recordSetId", recordSet.getRecordSetId());
		searchContext.setAttribute(Field.STATUS, WorkflowConstants.STATUS_APPROVED);
		searchContext.setAttribute("paginationType", "regular");
		
		// Attributes

		Map<String, Serializable> attributes =
			new HashMap<String, Serializable>();
		searchContext.setAttributes(attributes);

		// Query config

		QueryConfig queryConfig = new QueryConfig();

		queryConfig.setLocale(locale);

		searchContext.setQueryConfig(queryConfig);

		return searchContext;
		
	}
	
}
