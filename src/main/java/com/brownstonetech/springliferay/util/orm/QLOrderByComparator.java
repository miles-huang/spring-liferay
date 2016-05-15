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
import java.math.BigInteger;
import java.util.Date;

import com.liferay.portal.kernel.bean.BeanPropertiesUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.StringUtil;
//import com.surwing.swgcms.orm.DAOUtil;

/**
 * 
 * A comparator that can adapt to various entity bean
 * 
 * @author Miles Huang
 *
 */
public class QLOrderByComparator extends OrderByComparator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5835017308776216936L;
	
	public static final String ASC="ASC";
	public static final String DESC="DESC";
	
	private String orderBy;
	private String[] orderByFields;
	
	protected boolean[] asc;
	protected String[] propertyNames;
	
	
	/**
	 * 
	 * @param orderByClause SQL like order by clause (not include "order by")
	 */
	public QLOrderByComparator(String orderByClause) {
		String[] fields = orderByClause.split("\\s*,\\s*");
		if ( fields.length == 0 ) throw new IllegalArgumentException("orderBy is empty");
		orderByFields = new String[fields.length];
		propertyNames = new String[fields.length];
		asc = new boolean[fields.length];
		for ( int i=0; i<fields.length; i++) {
			String[] temp = fields[i].split("\\s+",2);
			String orderByCol = temp[0];
			orderByFields[i]=orderByCol;

			int index = orderByCol.lastIndexOf('.');
			if ( index == -1 ) {
				propertyNames[i] = orderByCol;
			} else {
				propertyNames[i] = orderByCol.substring(index+1);
			}
			
			if ( temp.length==1 ) {
				asc[i] = true;
			} else {
				temp[1]=temp[1].trim();
				if (temp[1].equalsIgnoreCase(DESC)) {
					asc[i] = false;
				} else if (temp[1].equalsIgnoreCase(ASC)){
					asc[i] = true;
				} else {
					throw new IllegalArgumentException("Illegal order by clause: "+fields[i]);
				}
			}
		}
		this.orderBy = orderByClause;
	}
	
	/**
	 * Create a single field order by comparator
	 * @param orderByCol
	 * @param asc
	 */
	public QLOrderByComparator(String orderByCol, boolean asc) {
		this(orderByCol+" "+(asc?ASC:DESC));
	}
	
	/**
	 * Create a single field order by comparator
	 * @param orderByCol
	 * @param orderByType
	 */
	public QLOrderByComparator(String orderByCol, String orderByType) {
		this(orderByCol, ASC.equalsIgnoreCase(StringUtil.trim(orderByType)));
	}

	@Override
	public int compare(Object obj1, Object obj2) {
		for ( int i=0; i<propertyNames.length; i++) {
			int ret = compareProperty(obj1, obj2, propertyNames[i], asc[i]);
			if ( ret != 0 ) {
				return ret;
			}
		}
		return 0;
	}

	protected int compareValue(Object key1, Object key2, boolean asc) {
		if (key1==null||key2==null) {
			throw new NullPointerException();
		}
		int value;
		if ( key1 instanceof String && key2 instanceof String ) {
			value = ((String)key1).toLowerCase().compareTo(
					((String)key2).toLowerCase());
		} else if ( key1 instanceof BigDecimal && key2 instanceof BigDecimal ) {
			BigDecimal v1 = (BigDecimal)key1;
			BigDecimal v2 = (BigDecimal)key2;
			value = v1.compareTo(v2);
		} else if ( key1 instanceof BigInteger && key2 instanceof BigInteger ) {
			value = ((BigInteger)key1).compareTo((BigInteger)key2);
		} else if ( key1 instanceof Number && key2 instanceof Number ) {
			if (key1 instanceof Double || key1 instanceof Float || key2 instanceof Double || key2 instanceof Float) {
				Double v1 = ((Number)key1).doubleValue();
				Double v2 = ((Number)key2).doubleValue();
				value = v1.compareTo(v2);
			} else {
				Long v1 = ((Number)key1).longValue();
				Long v2 = ((Number)key2).longValue();
				value = v1.compareTo(v2);
			}
		} else if ( key1 instanceof Date && key2 instanceof Date ) {
			Long ts1 = ((Date)key1).getTime();
			Long ts2 = ((Date)key2).getTime();
			value = ts1.compareTo(ts2);
		} else {
			String v1 = key1.toString();
			String v2 = key2.toString();
			value = v1.compareToIgnoreCase(v2);
		}

		if (asc) {
			return value;
		}
		else {
			return -value;
		}
	}
	
	protected int compareProperty(Object obj1, Object obj2, String propertyName, boolean asc) {
		Object key1;
		Object key2;

		key1 = BeanPropertiesUtil.getObject(obj1, propertyName);
		key2 = BeanPropertiesUtil.getObject(obj2, propertyName);
		return compareValue(key1, key2, asc);
	}

	@Override
	public String getOrderBy() {
		return orderBy;
	}

	@Override
	public String[] getOrderByFields() {
		return orderByFields;
	}

	@Override
	public boolean isAscending() {
		return asc[0];
	}
	
	@Override
	public String toString() {
		return getOrderBy();
	}

}
