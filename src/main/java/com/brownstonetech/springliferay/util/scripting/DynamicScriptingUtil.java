package com.brownstonetech.springliferay.util.scripting;

import java.util.Map;
import java.util.Set;

import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import com.brownstonetech.springliferay.PortalExtUtil;
import com.brownstonetech.springliferay.PortletInvocation;
import com.brownstonetech.springliferay.exception.ErrorMessageException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.scripting.ScriptingException;
import com.liferay.portal.kernel.scripting.ScriptingHelperUtil;
import com.liferay.portal.kernel.scripting.ScriptingUtil;
import com.liferay.portal.kernel.util.ClassLoaderPool;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.theme.ThemeDisplay;

/**
 * This class provide support utilities for manage dynamic scripts.
 * The dynamic script is introduced in the spring liferay plugin
 * that use liferay DDMTemplate to store user customized scripts
 * dynamically.
 * All liferay portlets use groovy scripts can leverage this mechanism
 * to get runtime customizeable application logic.
 * 
 * @author Miles Huang
 *
 */
public class DynamicScriptingUtil {

	private static final String SCRIPT_CONTEXT_KEY = "com.brownstonetech.springliferay.script.context";
	
	/**
	 * Run an controller extension script with controller script content
	 * variables populated.
	 * 
	 * @param scriptGroupId DDMTemplate displayStyleGroupId
	 * @param scriptKey DDMTemplate displayStyle
	 * @param portletInvocation
	 * @param additionalVariables
	 * @param modelMap
	 * @param logger
	 * @throws ErrorMessageException
	 * @throws SystemException
	 */
	public static void runExtensionScript(long scriptGroupId, String scriptKey, PortletInvocation portletInvocation, 
			final Map<String,Object> additionalVariables, final Map<String, Object> modelMap,
			final Log logger)
			throws PortalException, SystemException {
		runScript(portletInvocation,
					scriptGroupId, scriptKey, new ScriptHandler() {

				public Set<String> getOutputVariables() { return null; }

				public void populateAdditionalScriptContext(
						Map<String, Object> scriptContext) {
					scriptContext.putAll(modelMap);
					scriptContext.putAll(additionalVariables);
					scriptContext.put("_log", logger);
				}

				public void handleScriptResult(Map<String, Object> result) {
				}
				
		});
	}
	
	public static void runScript(PortletInvocation portletInvocation,
			long scriptGroupId, String scriptKey, ScriptHandler handler)
					throws PortalException, SystemException {
		try {
			PortletRequest portletRequest = portletInvocation.getPortletRequest();
			PortletResponse portletResponse = portletInvocation.getPortletResponse();
			String portletId = portletInvocation.getPortletId();
			String cacheKey = ScriptCacheUtil.generateCacheKey(scriptGroupId,
					scriptKey);
			PortletPreferences preferences = portletRequest.getPreferences();
			CachedScript cachedScript = ScriptCacheUtil.getScript(cacheKey, 
					scriptGroupId, preferences, scriptKey, 
					portletId);
			Map<String, Object> scriptContext = null;
			if ( cachedScript != null ) {
				scriptContext = populateScriptingContext(portletInvocation.getPortletConfig(),
						portletRequest, portletResponse);
				handler.populateAdditionalScriptContext(scriptContext);
				Map<String, Object> ret = ScriptingUtil.eval(null, scriptContext,
						handler.getOutputVariables(), "groovy", cachedScript.getScript(),
						ClassLoaderPool.getContextName(PortalClassLoaderUtil.getClassLoader()),
						ClassLoaderPool.getContextName(Thread.currentThread().getContextClassLoader()));
				handler.handleScriptResult(ret);
			}
		} catch (ScriptingException e) {
			Throwable cause = e.getCause();
			if ( cause instanceof PortalException ) {
				PortalException msg = (PortalException) cause;
				throw msg;
			}
			if ( cause instanceof SystemException ) {
				SystemException msg = (SystemException) cause;
				throw msg;
			}
			throw new SystemException("Unexpected ScriptingException while running script: scriptGroupId="+scriptGroupId+", scriptKey="+scriptKey, e);
		}
	}
	
	private static Map<String, Object> populateScriptingContext(
			PortletConfig portletConfig,
			PortletRequest request, PortletResponse response) throws SystemException {
		
		@SuppressWarnings("unchecked")
		Map<String, Object> scriptContext = (Map<String, Object>)request.getAttribute(SCRIPT_CONTEXT_KEY);

		if ( scriptContext != null ) {
			return scriptContext;
		}
		PortletContext portletContext = portletConfig.getPortletContext();
		scriptContext = ScriptingHelperUtil.getPortletObjects(
					portletConfig, portletContext, request, response);
		ThemeDisplay themeDisplay = PortalExtUtil.getThemeDisplay(request);
		PermissionChecker permissionChecker = PortalExtUtil.getPermissionChecker(request);
		scriptContext.put("themeDisplay", themeDisplay);
		scriptContext.put("permissionChecker", permissionChecker);
		scriptContext.put("portletDisplay", themeDisplay.getPortletDisplay());
		scriptContext.put("portletRequest", request);
		scriptContext.put("portletResponse", response);
		request.setAttribute(SCRIPT_CONTEXT_KEY, scriptContext);
		
		return scriptContext;
	}
	
	
}
