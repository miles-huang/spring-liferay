package com.brownstonetech.springliferay.search;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import com.brownstonetech.springliferay.util.PrimitiveArrayUtil;
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
		PropertyDescriptor[] descriptors = BeanUtils.getPropertyDescriptors(DisplayTerms.class);
		for ( PropertyDescriptor descriptor: descriptors ) {
			baseProperties.add(descriptor.getName());
		}
		baseProperties.add("metaClass");
	}
	
	public GenericSearchContainer(PortletRequest portletRequest, PortletURL iteratorURL, S searchTerm, int defaultDelta) {
		this(portletRequest, DEFAULT_CUR_PARAM, searchTerm, iteratorURL, defaultDelta);
	}

	public GenericSearchContainer(
			PortletRequest portletRequest, String curParam, S searchTerm,
			PortletURL iteratorURL, int defaultDelta) {
		super(
				portletRequest, searchTerm,
				searchTerm, curParam, defaultDelta,
				iteratorURL, null, null);

		BeanWrapper beanWrapper = new BeanWrapperImpl(searchTerm);
		for ( PropertyDescriptor pd: beanWrapper.getPropertyDescriptors() ) {
			String propertyName = pd.getName();
			if ( baseProperties.contains(propertyName)) continue;
			Object propertyValue = beanWrapper.getPropertyValue(propertyName);
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
		if ( propertyValue == null ) return;
		if ( propertyValue instanceof Collection ) {
			Collection<?> c = (Collection<?>)propertyValue;
			if ( c.size() > 0 ) {
				List<String> params = new ArrayList<String>(c.size());
				Iterator<?> iter = c.iterator();
				while ( iter.hasNext()) {
					Object e = iter.next();
					if ( e == null ) continue;
					params.add(String.valueOf(e));
				}
				if ( params.size() > 0 ) {
					addParameter(iteratorURL, propertyName, params);
				}
			}
			return;
		}
		if ( propertyValue.getClass().isArray()) {
			List<String> list = PrimitiveArrayUtil.arrayToStringList(propertyValue);
			if ( list != null && list.size() > 0 ) {
				addParameter(iteratorURL, propertyName, list);
			}
			return;
		}
		iteratorURL.setParameter(
				propertyName, String.valueOf(propertyValue));
	}

	protected void addParameter(PortletURL iteratorURL, String propertyName, List<String> propertyValues) {
		if ( propertyValues == null || propertyValues.size() == 0 ) return;
		iteratorURL.setParameter(
				propertyName, propertyValues.toArray(new String[propertyValues.size()]));
	}
}
