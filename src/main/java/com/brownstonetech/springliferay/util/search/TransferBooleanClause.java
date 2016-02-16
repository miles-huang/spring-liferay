package com.brownstonetech.springliferay.util.search;

import com.liferay.portal.kernel.search.BooleanClause;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Query;

public class TransferBooleanClause implements BooleanClause {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BooleanClauseOccur booleanClauseOccur;
	private Query query;
	
	public TransferBooleanClause(Query query, BooleanClauseOccur booleanClauseOccur) {
		this.booleanClauseOccur = booleanClauseOccur;
		this.query = query;
	}
	
	public BooleanClauseOccur getBooleanClauseOccur() {
		return booleanClauseOccur;
	}

	public Query getQuery() {
		return query;
	}
	
}
