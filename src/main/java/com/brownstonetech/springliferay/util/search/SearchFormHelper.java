package com.brownstonetech.springliferay.util.search;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.cglib.beans.BeanMap;

import com.liferay.portal.kernel.dao.search.DisplayTerms;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.BooleanQueryFactoryUtil;
import com.liferay.portal.kernel.search.ParseException;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.TermQuery;
import com.liferay.portal.kernel.search.TermQueryFactoryUtil;
import com.liferay.portal.kernel.search.TermRangeQueryFactoryUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;

/**
 * This helper class is used to generate search query terms based on
 * given DisplayTerms bean.
 * This class is not thread safe.
 *  
 * @author Miles Huang
 *
 */
public class SearchFormHelper {

	protected static Log _log = LogFactoryUtil.getLog(SearchFormHelper.class);
	
	protected Set<String> fieldNameBlackList;
	protected SearchContext searchContext;
	protected Locale locale;
	
	private static final Collection<String> DISPLAY_TERM_PROPERTY_EXCLUDES= 
			Arrays.asList(
					"."+DisplayTerms.KEYWORDS,
					"."+DisplayTerms.ADVANCED_SEARCH,
					"."+DisplayTerms.AND_OPERATOR
			);

	private static final Set<String> INTERNAL_PROPERTIES= new HashSet<String>(
			Arrays.asList(
					"class", // POJO bean class
					"metaClass" // POGO bean meta class
					));
	
	public SearchFormHelper(Locale locale, SearchContext searchContext, String...propertiesExclude) {
		// display term base class defined properties
		fieldNameBlackList = new HashSet<String>(DISPLAY_TERM_PROPERTY_EXCLUDES);
		// POJO/POGO bean special properties
		fieldNameBlackList.addAll(INTERNAL_PROPERTIES);
		if ( propertiesExclude != null ) {
			for ( String propertyExcludes: propertiesExclude) {
				fieldNameBlackList.add("."+propertyExcludes);
			}
		}
		this.locale = locale;
		this.searchContext = searchContext;
	}
	
	/**
	 * This method process the passed in DisplayTerms model.
	 * If the advancedSearch is set in the DisplayTerms model,
	 * The corresponding search query terms is generated and 
	 * added into the dynaFieldQuery.
	 * 
	 * @param additionalQuery
	 * @param searchTerms
	 * @throws ParseException 
	 */
	public void addAdvancedSearchQuery(BooleanQuery additionalQuery,
			DisplayTerms searchTerms) throws ParseException {
		
		boolean advancedSearch = searchTerms.isAdvancedSearch();
		if ( ! advancedSearch ) return;
		searchContext.setKeywords(null);
		
		BeanMap map = BeanMap.create(searchTerms);
		
		@SuppressWarnings("unchecked")
		Map<String,Object> map2 = (Map<String,Object>)map;
		BooleanQuery dynaFieldQuery = createQuery(StringPool.BLANK, 
				searchTerms.isAndOperator(), map2, fieldNameBlackList);

		combineAllTo(additionalQuery, dynaFieldQuery);
	}

	/**
	 * Create an empty BooleanQuery which is used for combine queries later.
	 * @return
	 */
	public BooleanQuery createBooleanQuery() {
		return BooleanQueryFactoryUtil.create(searchContext);
	}

	/**
	 * Combine sourceQueries into the destQuery, which only requires
	 * ANY ONE of the query listed in the sourceQueries matches.
	 * <p>
	 * If sourceQueries is empty (null or no any query clause defined in all sourceQueries), the destQuery is
	 * not changed.
	 * </p>
	 * 
	 * @param destQuery
	 * @param sourceQueries
	 * @throws ParseException
	 */
	public void combineAnyTo(BooleanQuery destQuery, BooleanQuery ...sourceQueries ) throws ParseException {
		if ( sourceQueries == null || sourceQueries.length == 0) return;
		BooleanQuery combined = createBooleanQuery();
		for ( BooleanQuery sourceQuery: sourceQueries) {
			if ( sourceQuery == null ) continue;
			if ( sourceQuery.clauses().size() == 0 ) continue;
			if ( sourceQuery.clauses().size() == 1 && sourceQuery.clauses().get(0).getBooleanClauseOccur() != BooleanClauseOccur.MUST_NOT) {
				// simplify in this special case
				combined.add(sourceQuery.clauses().get(0).getQuery(), BooleanClauseOccur.SHOULD);
			} else {
				combined.add(sourceQuery, BooleanClauseOccur.SHOULD);
			}
		}
		// Do further simplify on generated combined query
		if ( combined.clauses().size() == 0 ) return;
		destQuery.add(combined, BooleanClauseOccur.MUST);
	}
	
	/**
	 * Combine sourceQuery into the destQuery which requires the given sourceQuery criteria NOT matches.
	 * <p>
	 * If sourceQuery is empty (null or no any query clause defined in the sourceQuery), the destQuery is
	 * not changed.
	 * </p>
	 * 
	 * @param destQuery
	 * @param sourceQuery
	 * @throws ParseException
	 */
	public void combineNotTo(BooleanQuery destQuery, BooleanQuery sourceQuery ) throws ParseException {
		if ( sourceQuery.clauses().size() == 0 ) return;
		destQuery.add(sourceQuery, BooleanClauseOccur.MUST_NOT);
	}
	
	/**
	 * Combine sourceQueries into the destQuery, which requires all of the source queries must match.
	 * 
	 * @param destQuery
	 * @param sourceQueries
	 * @throws ParseException
	 */
	public void combineAllTo(BooleanQuery destQuery, BooleanQuery ...sourceQueries ) throws ParseException {
		if ( sourceQueries == null ) return;
		for ( BooleanQuery sourceQuery: sourceQueries) {
			if ( sourceQuery == null ) continue;
			if ( sourceQuery.clauses().size() == 0) continue;
			destQuery.add(sourceQuery, BooleanClauseOccur.MUST);
		}
	}
	
	protected BooleanQuery createQuery(String path, 
			boolean andOperator, Map<String, Object> searchTerms,
			Set<String> blackList) throws ParseException {

		BooleanQuery dynaFieldQuery = BooleanQueryFactoryUtil.create(searchContext);

		Map<String,Map<String,Object>> aggregatedRangeCriteria = new HashMap<String,Map<String,Object>>();
		
		processMap(dynaFieldQuery, andOperator, path, searchTerms, blackList, aggregatedRangeCriteria);
		
		addRangeQueryTerms(dynaFieldQuery, andOperator, aggregatedRangeCriteria);
		
		return dynaFieldQuery;
		
	}

	private void processBean(BooleanQuery dynaFieldQuery,
			boolean andOperator,  String path, Object searchFormBean,
			Set<String> blackList, 
			Map<String,Map<String,Object>> aggregatedRangeCriteria) throws ParseException {
		BeanMap map = BeanMap.create(searchFormBean);
		@SuppressWarnings("unchecked")
		Map<String, Object> map2 = map;
		processMap(dynaFieldQuery, andOperator, path, map2, blackList, aggregatedRangeCriteria);
	}
	
	private void processMap(BooleanQuery dynaFieldQuery,
			boolean andOperator, String path, Map<String, Object> map, 
			Set<String> blackList,
			Map<String,Map<String,Object>> aggregatedRangeCriteria) throws ParseException {
		Iterator<String> it = map.keySet().iterator();
		while ( it.hasNext() ) {
			String temp = it.next();
			String[] ret = stripOperator(temp);
			String operator = ret[0];
			String propertyName = ret[1];
			String propertyPath = new StringBuilder(path).append('.').append(propertyName).toString();
			if ( blackList != null ) {
				if ( blackList.contains(propertyName)) continue;
				if ( blackList.contains(propertyPath)) continue;
			}
			Object value = map.get(temp);
			if ( isBean(propertyPath) ) {
				// recurisve process bean
				processBean(dynaFieldQuery, andOperator, propertyPath, value,
						blackList, aggregatedRangeCriteria);
			} else {
				try {
					processField(dynaFieldQuery, andOperator, path, propertyName,
							operator, value, aggregatedRangeCriteria);
				} catch (Exception e) {
					_log.error("Failed to process field, path="+path
							+", propertyName="+propertyName
							+", operator="+operator
							+", value="+value, e);
				}
			}
		}
	}
	
	/**
	 * Child class should override this method if additional child bean properties
	 * is added to the search DisplayTerms bean.
	 * Do not forget to call super method to process inherited bean path.
	 * 
	 * @param propertyPath The property path started with ".", include the property name
	 * @return
	 */
	protected boolean isBean(String propertyPath) {
		// TODO: process expando prefix
		return false;
	}

	private void processField(BooleanQuery dynaFieldQuery,
			boolean andOperator,
			String propertyPath,
			String propertyName, String operator, Object value,
			Map<String,Map<String,Object>> aggregatedRangeCriteria) throws PortalException, SystemException {
		boolean like = operator.equals("like");
		String indexFieldName = getIndexFieldName(propertyPath, propertyName, like);
		if ( indexFieldName != null) {
			addFieldQuery(dynaFieldQuery, andOperator,
					indexFieldName, value, operator, aggregatedRangeCriteria);
		}
	}

	/**
	 * By default only expando fields is processed by this class.
	 * Child class should override this method to provide search engine indexed field
	 * name for additional searchable property.
	 * A property in DisplayTerms is specified by a propertyPath and propertyName.
	 * Property path is from the DisplayTerms to the bean directly defines
	 * the property.
	 * @param propertyPath path of the property.
	 * If the property is a top level property in the DisplayTerms bean, the propertyPath
	 * is blank (StringPool.BLANK), otherwise it is started with a "." and following the path
	 * to the bean directly defines the property.
	 * @param propertyName the property name of the field. If the property defined in the
	 * search form bean contains search operator suffix, such suffix is not included in the
	 * passed in propertyName parameter.
	 * @param lowerCase is the search need to use lower case indexed field for example like search
	 * @return the index field name for the search field.
	 * @throws SystemException 
	 * @throws PortalException 
	 */
	protected String getIndexFieldName(String propertyPath, String propertyName, boolean lowerCase) throws PortalException, SystemException {
		if ( propertyPath.equals(".expando")) {
			// TODO: process expando fields
		}
		return null;
	}

	private void addFieldQuery(BooleanQuery dynaFieldQuery,
			boolean andOperator, 
			String indexFieldName,
			Object value, String operator,
			Map<String,Map<String,Object>> aggregatedRangeCriteria) throws ParseException {
		String value1 = SearchTermUtil.formatValue(value);
		if ( Validator.isNotNull(value1)) {
			if ( "eq".equals(operator) ) {
				addQueryTerm(dynaFieldQuery, andOperator, indexFieldName, value1);
			} else if ( "like".equals(operator) ) {
				addLikeQueryTerm(dynaFieldQuery, andOperator, indexFieldName, value1);
			} else {
				Map<String,Object> param = aggregatedRangeCriteria.get(indexFieldName);
				if ( param == null ) {
					param = new HashMap<String,Object>();
					aggregatedRangeCriteria.put(indexFieldName, param);
				}
				// Put original value into the map so that process code knows
				// the type of the value and do range criteria process 
				param.put(operator, value);
			}
		}
	}

	private void addRangeQueryTerms(BooleanQuery dynaFieldQuery,
			boolean andOperator, Map<String,Map<String,Object>> aggregatedRangeCriteria) {
		for ( String indexFieldName: aggregatedRangeCriteria.keySet()) {
			Map<String,Object> rangeCriteria = aggregatedRangeCriteria.get(indexFieldName);
			Object lowerTerm = rangeCriteria.get("gt");
			boolean includesLower = false;
			if ( lowerTerm == null ) {
				lowerTerm = rangeCriteria.get("gte");
				includesLower = true;
			}
			boolean includesUpper = false;
			Object upperTerm = rangeCriteria.get("lt");
			if ( upperTerm == null ) {
				includesUpper = true;
				upperTerm = rangeCriteria.get("lte");
			}
			if ( lowerTerm != null || upperTerm != null ) {
				addRangeQueryTerm(aggregatedRangeCriteria,
						dynaFieldQuery,
						andOperator,
						indexFieldName, lowerTerm, upperTerm,
						includesLower, includesUpper);
			}
		}
	}
	
	private void addRangeQueryTerm(Map<String,Map<String,Object>> aggregatedRangeCriteria,
			BooleanQuery dynaFieldQuery,
			boolean andOperator,
			String indexFieldName, Object lowerTerm, Object upperTerm,
			boolean includesLower, boolean includesUpper) {
		String strLowerTerm = SearchTermUtil.formatValue(lowerTerm);
		String strUpperTerm = SearchTermUtil.formatValue(upperTerm);
		Class<?> valueClass;
		if ( strLowerTerm == null ) {
			valueClass = upperTerm.getClass();
		} else {
			valueClass = lowerTerm.getClass();
		}
		
		// Lucene doesn't support open end query
		// We will deal with it by ourself
		if ( Validator.isNull(strLowerTerm) ) {
			if ( java.util.Date.class.isAssignableFrom(valueClass)) {
				strLowerTerm = "00000000000000";
			} else if ( Number.class.isAssignableFrom(valueClass)) {
				strLowerTerm = String.valueOf(Long.MIN_VALUE);
			} else {
				strLowerTerm = "!"; // lowest normal character in ASCII table.
			}
		}
		if ( Validator.isNull(strUpperTerm) ) {
			if ( java.util.Date.class.isAssignableFrom(valueClass)) {
				strUpperTerm = "99999999999999";
			} else if ( Number.class.isAssignableFrom(valueClass)) {
				strUpperTerm = String.valueOf(Long.MAX_VALUE);
			} else {
				strUpperTerm = String.valueOf(Character.MAX_VALUE); // dirty but should work in most case.
			}
		}
		
		boolean lower = includesLower;
		boolean upper = includesUpper;
		// Lucene doesn't support not same boundary for upper and lower,
		// We will deal with it by ourself
		if ( includesLower != includesUpper ) {
			lower = true;
			upper = true;
		}
_log.info("Search parameter value class is "+valueClass);
		if (Number.class.isAssignableFrom(valueClass)) {
			indexFieldName=encodeNumericRangeIndexFieldName(indexFieldName);
		}
_log.info("indexFieldName="+indexFieldName);
		Query term = TermRangeQueryFactoryUtil.create(searchContext, indexFieldName,
				strLowerTerm, strUpperTerm, lower, upper);
		try {
			if ( includesLower != includesUpper ) {
				BooleanQuery query = BooleanQueryFactoryUtil.create(searchContext);
				query.add(term, BooleanClauseOccur.MUST);
				TermQuery exclusive;
				if ( !includesLower ) {
					exclusive =TermQueryFactoryUtil.create(searchContext, indexFieldName, strLowerTerm);
				} else {
					exclusive = TermQueryFactoryUtil.create(searchContext, indexFieldName, strUpperTerm);
				}
				query.add(exclusive, BooleanClauseOccur.MUST_NOT);
				term = query;
			}
			dynaFieldQuery.add(term, andOperator? BooleanClauseOccur.MUST:BooleanClauseOccur.SHOULD);
		} catch (ParseException e) {
			_log.error("Failed to add range query for field "+indexFieldName, e);
		}
	}

	protected String encodeNumericRangeIndexFieldName(String indexFieldName) {
		return indexFieldName;
	}

	private void addLikeQueryTerm(BooleanQuery dynaFieldQuery, boolean andOperator,
			String indexFieldName, String value1) {
		value1 = value1.replaceAll("%", StringPool.BLANK);
		if ( andOperator ) {
			dynaFieldQuery.addRequiredTerm(indexFieldName, value1, true);
		} else {
			try {
				dynaFieldQuery.addTerm(indexFieldName, value1, true);
			} catch (ParseException e) {
				_log.error("Can't add search term for field "+indexFieldName, e);
			}
		}
	}

	private void addQueryTerm(BooleanQuery dynaFieldQuery, boolean andOperator, 
			String indexFieldName, String value1) throws ParseException {
//		String escapedValue = luceneEscape(value1);
		TermQuery query = TermQueryFactoryUtil.create(searchContext, indexFieldName, value1);
		dynaFieldQuery.add(query, andOperator? BooleanClauseOccur.MUST: BooleanClauseOccur.SHOULD);
/*		if ( andOperator ) {
			dynaFieldQuery.addRequiredTerm(indexFieldName, value1);
		} else {
			dynaFieldQuery.addExactTerm(indexFieldName, value1);
		}*/
	}

	protected String[] stripOperator(String parameterName) {
		String [] ret = new String[2];
		if ( parameterName.endsWith("_eq")
				|| parameterName.endsWith("_lt")
				|| parameterName.endsWith("_lte")
				|| parameterName.endsWith("_gt")
				|| parameterName.endsWith("_gte")
				|| parameterName.endsWith("_like")) {
			int pos = parameterName.lastIndexOf('_');
			ret[0] = parameterName.substring(pos+1);
			ret[1] = parameterName.substring(0,pos);
		} else {
			ret[0] = "eq";
			ret[1] = parameterName;
		}
		return ret;
	}
	
}
