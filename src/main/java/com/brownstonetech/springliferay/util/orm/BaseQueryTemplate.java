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

package com.brownstonetech.springliferay.util.orm;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.CalendarUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Validator;

public abstract class BaseQueryTemplate {

	protected static Log _log = LogFactoryUtil.getLog(BaseQueryTemplate.class);
	protected List<Object> params;
	protected StringBuilder query;
	protected CharSequence select;
	protected CharSequence from;
	protected OrderByComparator defaultObc;

	public BaseQueryTemplate(CharSequence select,
			CharSequence from,
			OrderByComparator defaultObc) {
		this.select = select;
		this.from = from;
		this.defaultObc = defaultObc;
	}

	/**
	 * <p>Subclass must implement this method to provide WHERE clause of 
	 * the query.</p>
	 * <p>When adding where clause require query parameter, add the parameter
	 * value to the params list. Each 
	 * '?' place holder in the return query clause must correspond to a
	 * parameter in the params list in right order.</p>
	 * <p>If the query from clause contains join part, and requires parameter,
	 * add the required parameter into the params list before any parameter
	 * for the where clause.</p>
	 * @param params the list receive query query parameter.
	 * @return the query where clause without WHERE keyword.
	 */
	protected abstract CharSequence getWhere(List<Object> params);

	protected abstract BaseQueryTemplate prepareCountQuery();

	protected void commonFromWherePart() {
		query.append(" FROM ").append(from);
		CharSequence where = getWhere(params);
		if ( Validator.isNotNull(where) ) {
			query.append(" WHERE ").append(where);
		}
	}

	@Override
	public String toString() {
		return new StringBuilder(1024)
			.append("Query: ").append(query)
			.append("\nQuery param: ").append(params).toString();
	}

	/**
	 * Add a expression (?,?,?) in query string, and add collectionParam to params list,
	 * if input collectionParam is not empty.
	 * @param expression
	 * @param query
	 * @param resultParams
	 * @param inputParams
	 */
	protected void addParams(String expression, StringBuilder query, List<Object> resultParams,
			Collection<?> inputParams) {
		if ( inputParams != null && ! inputParams.isEmpty() ) {
			query.append(expression).append(" (");
			for ( int i = 0; i < inputParams.size(); i++ ) {
				if ( i > 0 ) query.append(", ");
				query.append('?');
			}
			resultParams.addAll(inputParams);
			query.append(')');
		}
	}

	protected BaseQueryTemplate prepareListQuery(OrderByComparator obc) {
		params = new ArrayList<Object>();
		query = new StringBuilder("SELECT ");
		query.append(select);
		commonFromWherePart();
		if ( obc == null ) obc = defaultObc;
		if ( obc != null ) {
			query.append(" ORDER BY ").append(obc.getOrderBy());
		}
		return this;
	}

	public CharSequence getQuery() {
		return query;
	}
	
	public List<Object> getParams() {
		return Collections.unmodifiableList(params);
	}

	public static void applyParameters(Query q, List<?> params) throws SystemException {
		QueryPos qPos = QueryPos.getInstance(q);
		int i = 0;
		for ( Object param: params ) {
			if ( param == null || Boolean.class.isAssignableFrom(param.getClass())) {
				qPos.add((Boolean)param);
			} else if ( String.class.isAssignableFrom(param.getClass())) {
				qPos.add((String)param);
			} else if ( Long.class.isAssignableFrom(param.getClass())) {
				qPos.add((Long)param);
			} else if ( Integer.class.isAssignableFrom(param.getClass())) {
				qPos.add((Integer)param);
			} else if ( Short.class.isAssignableFrom(param.getClass())) {
				qPos.add((Short)param);
			} else if ( Timestamp.class.isAssignableFrom(param.getClass())) {
				qPos.add((Timestamp)param);
			} else if ( Date.class.isAssignableFrom(param.getClass())) {
				qPos.add(CalendarUtil.getTimestamp((Date)param));
			} else if ( BigDecimal.class.isAssignableFrom(param.getClass())) {
				qPos.add((BigDecimal)param);
			} else if ( Double.class.isAssignableFrom(param.getClass())) {
				qPos.add((Double)param);
			} else if ( Float.class.isAssignableFrom(param.getClass())) {
				qPos.add((Float)param);
			} else {
				throw new SystemException("Unsupported query param["+i+"]:"+param.getClass()+"value="+String.valueOf(param));
			}
			i++;
		}
		
	}
	
}