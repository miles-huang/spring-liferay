package com.brownstonetech.springliferay.search;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Iterator;

import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;

import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.beanutils.PropertyUtils;

import com.liferay.portal.kernel.dao.search.DisplayTerms;
import com.liferay.portal.kernel.dao.search.SearchContainer;

/**
 * 
 * @author Miles Huang
 *
 * @param <E>
 * @param <S>
 */
public class GenericSearchContainer<E,S extends DisplayTerms> extends SearchContainer<E> {

	private static final HashSet<String> baseProperties;
	
	static {
		baseProperties = new HashSet<String>();
		PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(DisplayTerms.class);
		for ( PropertyDescriptor descriptor: descriptors ) {
			baseProperties.add(descriptor.getName());
		}
	}
	
	public GenericSearchContainer(PortletRequest portletRequest, PortletURL iteratorURL, S searchTerm) {
		this(portletRequest, DEFAULT_CUR_PARAM, searchTerm, iteratorURL);
	}

	public GenericSearchContainer(
			PortletRequest portletRequest, String curParam, S searchTerm,
			PortletURL iteratorURL) {
		super(
				portletRequest, searchTerm,
				searchTerm, curParam, DEFAULT_DELTA,
				iteratorURL, null, null);

		BeanMap map = new BeanMap();
		Iterator<String> propertyIter = map.keyIterator();
		while ( propertyIter.hasNext() ) {
			String propertyName = propertyIter.next();
			if ( baseProperties.contains(propertyName)) continue;
			Object propertyValue = map.get(propertyName);
			addParameter(iteratorURL, propertyName, propertyValue);
		}
	}

	/**
	 * This is the default implementation which simply put search field's string
	 * value as parameter.
	 * <p>
	 * Child class can overwrite this method to provide complex search form field
	 * for example Date field, or sub bean handling.
	 * </p>
	 * @param iteratorURL
	 * @param propertyName
	 * @param propertyValue
	 */
	protected void addParameter(PortletURL iteratorURL, String propertyName, Object propertyValue) {
		iteratorURL.setParameter(
				propertyName, String.valueOf(propertyValue));
	}
	
}
