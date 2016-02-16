package com.brownstonetech.springliferay.util.scripting;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.util.Date;

import javax.portlet.PortletPreferences;

import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.SingleVMPoolUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;

public class ScriptCacheUtil implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Log _log = LogFactoryUtil.getLog(DynamicScriptingUtil.class);
	private static final String GROOVY_SCRIPT_CACHE_KEY="swgcms.scripting.cache";
	
	private static final CachedScript NO_SCRIPT_DEFINED = new CachedScript(new Date(0L), null);

	public static CachedScript getScript(String cacheKey, long scopeGroupId, 
			PortletPreferences preference, String scriptPath, String tracePortletContext) {
		PortalCache<Serializable, Object> scriptCache
			= SingleVMPoolUtil.getCache(GROOVY_SCRIPT_CACHE_KEY);
		CachedScript cachedScript;
		try {
			cachedScript = (CachedScript)scriptCache.get(cacheKey);
		} catch (ClassCastException e1) {
			// This is caused by plugin reload, need to invalidate
			// old cached script
			scriptCache.remove(cacheKey);
			cachedScript = null;
		}
		DLFileEntry scriptSource = null;

		try {
			if (cachedScript != null) {

				if ( cachedScript == NO_SCRIPT_DEFINED ) {
					return null;
				}

				if ( cachedScript.isExpireCheckRequired() ) {
					scriptSource = DynamicScriptingUtil.getDLScriptSource(scopeGroupId, preference, scriptPath, tracePortletContext);

					if ( scriptSource == null ) {
						scriptCache.put(cacheKey, NO_SCRIPT_DEFINED);
						return null;
					}

					Date lastUpdateTime = scriptSource.getModifiedDate();
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
			if ( scriptSource == null ) {
				scriptSource = DynamicScriptingUtil.getDLScriptSource(scopeGroupId, preference, scriptPath, tracePortletContext);
			}

			if ( scriptSource != null ) {
				
				// load script from script source
				InputStream stream = DynamicScriptingUtil.getDLScriptStream(scriptSource);
				try {
					Reader reader = new InputStreamReader(stream, "UTF-8");
					StringBuilder builder = new StringBuilder();
					char[] buffer = new char[1024];
					int size = 0;
					while ( size >= 0 ) {
						size = reader.read(buffer);
						if ( size > 0 ) {
							builder.append(buffer,0,size);
						}
					}
					String script = builder.toString().trim();
					if ( Validator.isNotNull(script)) {
						// create cached script for cache
						cachedScript = new CachedScript(scriptSource.getModifiedDate(), script);
					}
				} finally {
					stream.close();
				}
			}
		} catch (Exception e) {
			_log.error("Failed to load script, scriptName="+scriptPath+". Please re-configure the portlet "+tracePortletContext, e);
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

	public static String generateCacheKey(long plid, String portletId, String scriptPath) {
		StringBuilder temp = new StringBuilder().
				append(plid).append('/').append(portletId);
		if ( !scriptPath.startsWith("/")) {
			temp.append('/');
		}
		temp.append(scriptPath);
		return temp.toString();
	}
}
