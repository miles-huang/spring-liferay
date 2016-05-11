package com.brownstonetech.springliferay.util.orm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.service.persistence.impl.BasePersistenceImpl;

/**
 * This Helper provides convenient template to do JPQL search.
 * 
 * @author Miles Huang
 *
 * @param <R> Query result class, normally it would be an entity model class.
 */
public abstract class JPQLQueryTemplate<R> extends BaseQueryTemplate {
	
	protected BasePersistenceImpl<?> persistence;
	
	/**
	 * Create a Query Helper that executes a JPQL SELECT or SELECT COUNT() query
	 * 
	 * @param persistence The persistence or finder instance who use this helper
	 * @param select The select clause without "SELECT" keyword.
	 * @param from The from clause without "FROM" keyword
	 * @param defaultObc Default OrderByComparator. If no obc is provided when call runList method, this obc is used.
	 * This parameter is nullable. Null defaultObc means use database default order when no obc is provided when runList.
	 */
	public JPQLQueryTemplate(BasePersistenceImpl<?> persistence,
			CharSequence select, CharSequence from,
			OrderByComparator defaultObc) {
		super(select, from, defaultObc);
		this.persistence = persistence;
	}

	/**
	 * Create a helper which use database default order when no
	 * obc is provided when runList.
	 * @param persistence
	 * @param select
	 * @param from
	 */
	public JPQLQueryTemplate(BasePersistenceImpl<?> persistence,
			CharSequence select,
			CharSequence from) {
		this(persistence, select, from, null);
	}
	
	@Override
	protected BaseQueryTemplate prepareCountQuery() {
		params = new ArrayList<Object>();
		query = new StringBuilder("SELECT COUNT(");
		query.append(select).append(')');
		commonFromWherePart();
		return this;
	}
	
	/**
	 * Run JPQL query return entities match the query criteria.
	 * @param start
	 * @param end
	 * @param obc
	 * @return The query result list.
	 * @throws SystemException
	 */
	@SuppressWarnings("unchecked")
	public final List<R> runList(int start, int end, OrderByComparator obc) throws SystemException {
		prepareListQuery(obc);
		
		Session session = null;
		List<R> list = null;
		try {
			session = persistence.openSession();
			Query q = session.createQuery(query.toString());
			applyParameters(q, params);

			list = (List<R>) QueryUtil.list(q, persistence.getDialect(), start, end);
			if ( _log.isDebugEnabled() ) {
				_log.debug("Execute query success:\n"+this.toString());
			}
		} catch (Exception e) {
			if ( _log.isErrorEnabled() ) {
				_log.error("Fail to execute query:\n"+this.toString());
			}
			throw persistence.processException(e);
		} finally {
			if (list == null) {
				list = Collections.emptyList();
			}
			if ( session != null ) {
				persistence.closeSession(session);
			}
		}
		return list;
	}

	/**
	 * Run JPQL query return count of the entities matches the query criteria.
	 * @return Number of entities found by the query.
	 * @throws SystemException
	 */
	public final int runCount() throws SystemException {
		prepareCountQuery();
		
		Session session = null;
		Long count = null;
		String qstr = query.toString();
		try {
			session = persistence.openSession();
			Query q = session.createQuery(qstr);
			applyParameters(q, params);
			count = (Long) q.uniqueResult();
			if ( _log.isDebugEnabled() ) {
				_log.debug("Execute query success:\n"+this.toString());
			}
		} catch (Exception e) {
			if ( _log.isErrorEnabled() ) {
				_log.error("Fail to execute query:\n"+this.toString());
			}
			throw persistence.processException(e);
		} finally {
			if (count == null) {
				count = Long.valueOf(0);
			}
			persistence.closeSession(session);
		}
		return count.intValue();
	}

}
