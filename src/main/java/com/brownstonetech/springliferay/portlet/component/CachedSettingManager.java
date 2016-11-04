package com.brownstonetech.springliferay.portlet.component;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

import javax.servlet.ServletContext;

import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

import com.brownstonetech.springliferay.util.ExpirationValidateable;
import com.brownstonetech.springliferay.util.SimpleCache;

@Component
public class CachedSettingManager implements ServletContextAware {

	private static final String ATTR_KEY="LSPR_CACHED_PORTLET_SETTINGS";

	private ServletContext servletContext;
	
	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
	
	/* (non-Javadoc)
	 * @see com.brownstonetech.em.config.CachedSettingManagerInterface#expire(java.lang.String)
	 */
	public void expire(String portletUniqueId) {
		Map<String, Serializable> cache = getCache();
		cache.remove(portletUniqueId);
	}

	/* (non-Javadoc)
	 * @see com.brownstonetech.em.config.CachedSettingManagerInterface#add(java.lang.String, java.io.Serializable)
	 */
	public void add(String portletUniqueId, Serializable setting ) {
		Map<String, Serializable> cache = getCache();
		cache.put(portletUniqueId, setting );
	}
	
	/* (non-Javadoc)
	 * @see com.brownstonetech.em.config.CachedSettingManagerInterface#get(java.lang.String)
	 */
	public <T extends Serializable> T get(String portletUniqueId) {
		Map<String, Serializable> cache = getCache();
		@SuppressWarnings("unchecked")
		T s = (T)cache.get(portletUniqueId);
		if ( s instanceof ExpirationValidateable ) {
			ExpirationValidateable validator = (ExpirationValidateable)s;
			if (validator.isExpired()) {
				cache.remove(portletUniqueId);
				return null;
			}
		}
		return s;
	}
	
	private Map<String, Serializable> getCache() {
		@SuppressWarnings("unchecked")
		Map<String, Serializable> cache = 
				(Map<String, Serializable>)servletContext.getAttribute(ATTR_KEY);
		if ( cache == null ) {
			cache = Collections.synchronizedMap(new SimpleCache<String, Serializable>(100));
			servletContext.setAttribute(ATTR_KEY, cache);
		}
		return cache;
	}

	public static void expire(ServletContext servletContext, String portletUniqueId) {
		@SuppressWarnings("unchecked")
		Map<String, Serializable> cache = 
				(Map<String, Serializable>)servletContext.getAttribute(ATTR_KEY);
		if ( cache == null ) {
			return;
		}
		cache.remove(portletUniqueId);
	}
}
