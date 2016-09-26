package com.brownstonetech.springliferay.util.scripting;

import java.util.Arrays;
import java.util.HashSet;
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
import com.liferay.portal.kernel.log.LogFactoryUtil;
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
	private static Log _log = LogFactoryUtil.getLog(DynamicScriptingUtil.class);
	
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
		runCachedScript(portletInvocation,
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
	
	public static void runCachedScript(PortletInvocation portletInvocation,
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
			if ( cachedScript != null ) {
				String script = cachedScript.getScript();
				runScriptForRequest(portletInvocation, portletRequest, portletResponse, script, handler);
			}
		} catch (PortalException e) {
			throw new PortalException("Unexpected ScriptingException while running script: scriptGroupId="+scriptGroupId+", scriptKey="+scriptKey, e);
		} catch (SystemException e) {
			throw new SystemException("Unexpected ScriptingException while running script: scriptGroupId="+scriptGroupId+", scriptKey="+scriptKey, e);
		}
	}

	public static void runScriptForRequest(PortletInvocation portletInvocation, PortletRequest portletRequest,
			PortletResponse portletResponse, String script, ScriptHandler handler)
			throws SystemException, PortalException {
		Map<String, Object> scriptContext = populateScriptingContext(portletInvocation.getPortletConfig(),
				portletRequest, portletResponse);
		handler.populateAdditionalScriptContext(scriptContext);
		runScript(scriptContext, script, handler);
	}
	
	public static void runScript(Map<String,Object> scriptContext, String script, ScriptHandler handler)
			throws SystemException, PortalException {
		try {
			Map<String, Object> ret = ScriptingUtil.eval(null, scriptContext,
					handler.getOutputVariables(), "groovy", script,
					ClassLoaderPool.getContextName(Thread.currentThread().getContextClassLoader()),
					ClassLoaderPool.getContextName(PortalClassLoaderUtil.getClassLoader())
					);
			handler.handleScriptResult(ret);
		} catch (ScriptingException e) {
			if ( _log.isDebugEnabled() ) {
				_log.debug("Error when running script: "+script);
			}
			Throwable cause = e.getCause();
			if ( cause instanceof PortalException ) {
				PortalException msg = (PortalException) cause;
				throw msg;
			}
			if ( cause instanceof SystemException ) {
				SystemException msg = (SystemException) cause;
				throw msg;
			}
			throw new SystemException("Unexpected ScriptingException while running script", e);
		}
	}
	
	public static Map<String, Object> evalScript(Map<String,Object> scriptContext, String script, final Log logger, final String...outputVariables)
			throws SystemException, PortalException {
		@SuppressWarnings("unchecked")
		final Map<String,Object>[] ret = new Map[1];
		runScript(scriptContext, script, new ScriptHandler() {
			public Set<String> getOutputVariables() { 
				if ( outputVariables.length == 0 ) return null;
				Set<String> outputVariablesSet = new HashSet<String>(Arrays.asList(outputVariables));
				return outputVariablesSet;
			}

			public void populateAdditionalScriptContext(
					Map<String, Object> scriptContext) {
				scriptContext.put("_log", logger);
			}

			public void handleScriptResult(Map<String, Object> result) {
				ret[0] = result;
			}

		});
		return ret[0];
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
