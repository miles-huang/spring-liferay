package com.brownstonetech.springliferay.util.scripting;

import java.io.Serializable;
import java.util.Date;

import javax.portlet.PortletPreferences;

import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.SingleVMPoolUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portlet.dynamicdatamapping.model.DDMTemplate;
import com.liferay.portlet.portletdisplaytemplate.util.PortletDisplayTemplateUtil;

/**
 * Cache script in VM cache. 
 * Loading script from DDMTemplate implementation and Cache the loaded
 * script in Liferay VM cache.
 * @author Miles Huang
 */
public class ScriptCacheUtil implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Log _log = LogFactoryUtil.getLog(DynamicScriptingUtil.class);
	private static final String GROOVY_SCRIPT_CACHE_KEY="swgcms.scripting.cache";
	
	private static final CachedScript NO_SCRIPT_DEFINED = new CachedScript(new Date(0L), null);

	public static CachedScript getScript(String cacheKey, long scopeGroupId, 
			PortletPreferences preference, String scriptKey, String tracePortletContext) {
		PortalCache<Serializable, Object> scriptCache
			= SingleVMPoolUtil.getCache(GROOVY_SCRIPT_CACHE_KEY);
		CachedScript cachedScript;
		try {
			cachedScript = (CachedScript)scriptCache.get(cacheKey);
		} catch (ClassCastException e1) {
			// This might caused by plugin reload, need to invalidate
			// old cached script
			_log.info("Detect CaheScript class reload, discard old cached script.");
			scriptCache.remove(cacheKey);
			cachedScript = null;
		}
		
		DDMTemplate ddmTemplate = null;

		try {
			if (cachedScript != null) {

				if ( cachedScript == NO_SCRIPT_DEFINED ) {
					return null;
				}

				if ( cachedScript.isExpireCheckRequired() ) {
					ddmTemplate = PortletDisplayTemplateUtil.fetchDDMTemplate(scopeGroupId,
							scriptKey);
					if (ddmTemplate == null) {
						scriptCache.put(cacheKey, NO_SCRIPT_DEFINED);
						return null;
					}

					Date lastUpdateTime = ddmTemplate.getModifiedDate();
					if ( cachedScript.isExpired(lastUpdateTime)) {
						
						// cached script has expired, cleanup the cache
						scriptCache.remove(cacheKey);
						cachedScript = null;
					}
				}

				// cached script still valid?
				if ( cachedScript != null ) {
					return cachedScript;
				}
			}

			// script cache for this key is not found
			cachedScript = NO_SCRIPT_DEFINED;
			
			// avoid load script source twice
			if ( ddmTemplate == null ) {
				ddmTemplate = PortletDisplayTemplateUtil.fetchDDMTemplate(scopeGroupId,
						scriptKey);
			}

			if ( ddmTemplate != null ) {
				String script = ddmTemplate.getScript();
				if ( Validator.isNotNull(script)) {
					cachedScript = new CachedScript(ddmTemplate.getModifiedDate(), ddmTemplate.getScript());
				}
			}
		} catch (Exception e) {
			_log.error("Failed to load script, groupId="+scopeGroupId+", scriptKey="+scriptKey+". Please re-configure the portlet "+tracePortletContext, e);
			cachedScript = NO_SCRIPT_DEFINED;
		}
		
		// store cachedScript into cache
		scriptCache.put(cacheKey, cachedScript);
		
		// only return valid cached script
		if ( cachedScript == NO_SCRIPT_DEFINED ) {
			return null;
		}
		return cachedScript;
	}
	
	public static void invalidateScript(String cacheKey) {
		PortalCache<Serializable, Object> scriptCache
			= SingleVMPoolUtil.getCache(GROOVY_SCRIPT_CACHE_KEY);
		scriptCache.remove(cacheKey);
	}

	public static String generateCacheKey(long scriptGroupId, String scriptKey) {
		StringBuilder temp = new StringBuilder().
				append(scriptGroupId).append('/').append(scriptKey);
		return temp.toString();
	}
}
