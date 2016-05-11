package com.brownstonetech.springliferay.util.orm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.Type;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.service.persistence.impl.BasePersistenceImpl;

/**
 * This Helper provides convenient template to do JPQL search.
 * 
 * @author Miles Huang
 *
 * @param <R> Query result class, normally it would be an entity model class.
 */
public abstract class SQLQueryTemplate<R> extends BaseQueryTemplate {

	protected BasePersistenceImpl<?> persistence;

	/**
	 * Create a helper which execute SQL SELECT or SELECT COUNT() query.
	 * 
	 * @param persistence The persistence or finder instance who use this helper
	 * @param select The select clause without "SELECT" keyword.
	 * @param from The from clause without "FROM" keyword
	 * @param defaultObc Default OrderByComparator. If no obc is provided when call runList method, this obc is used.
	 * This parameter is nullable. Null defaultObc means use database default order when no obc is provided when runList.
	 */
	public SQLQueryTemplate(BasePersistenceImpl<?> persistence,
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
	public SQLQueryTemplate(BasePersistenceImpl<?> persistence,
			CharSequence select,
			CharSequence from) {
		this(persistence, select, from, null);
	}

	@Override
	protected BaseQueryTemplate prepareCountQuery() {
		params = new ArrayList<Object>();
		query = new StringBuilder("SELECT COUNT(");
		query.append(select).append(") AS ").append(BasePersistenceImpl.COUNT_COLUMN_NAME).append(' ');
		commonFromWherePart();
		return this;
	}
	
	/**
	 * Child class must implement this method to specify Entity Implementing class
	 * by alias if this query returns an entity list
	 * For example:
	 * void specifyEntityAliase(SQLQuery query) {
	 *     query.addEntity("p", ProductImpl.class);
	 * }
	 * where "p" is the select entity's alias, and ProductImpl.class is the entity's
	 * implementation class.
	 * 
	 * @param query
	 */
	protected abstract void specifyEntityAliase(SQLQuery query);

	/**
	 * Run SQL query return entities match the query criteria.
	 * @param start
	 * @param end
	 * @param obc
	 * @return The query result list.
	 * @throws SystemException
	 */
	@SuppressWarnings("unchecked")
	public List<R> runList(int start, int end, OrderByComparator obc) throws SystemException {
		prepareListQuery(obc);

		Session session = null;
		List<R> list = null;
		try {
			session = persistence.openSession();
			SQLQuery q = session.createSQLQuery(query.toString());
			applyParameters(q, params);

			specifyEntityAliase(q);

			list = (List<R>)QueryUtil.list(
					q, persistence.getDialect(), start, end);
			
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
	 * Run query return count of the entities matches the query criteria.
	 * @return Number of entities found by the query.
	 * @throws SystemException
	 */
	public int runCount() throws SystemException {
		prepareCountQuery();
		
		Session session = null;
		Long count = 0L;
		String qstr = query.toString();
		try {
			session = persistence.openSession();
			SQLQuery q = session.createSQLQuery(qstr);
			applyParameters(q, params);

			q.addScalar(BasePersistenceImpl.COUNT_COLUMN_NAME, Type.LONG);
			
			if ( _log.isDebugEnabled() ) {
				_log.debug("Execute query success:\n"+this.toString());
			}
			
			@SuppressWarnings("unchecked")
			Iterator<Long> itr = q.list().iterator();

			if (itr.hasNext()) {
				count = itr.next();

				if (count != null) {
					return count.intValue();
				}
			}
			
		} catch (Exception e) {
			if ( _log.isErrorEnabled() ) {
				_log.error("Fail to execute SQL query:\n"+this.toString());
			}
			throw persistence.processException(e);
		} finally {
			persistence.closeSession(session);
		}
		return count.intValue();
	}
	
}
