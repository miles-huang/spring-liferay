package com.brownstonetech.springliferay.util.scripting;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.regex.Pattern;

import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.servlet.ServletContext;

import com.brownstonetech.springliferay.LiferayRequestContext;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.scripting.ScriptingException;
import com.liferay.portal.kernel.scripting.ScriptingHelperUtil;
import com.liferay.portal.kernel.scripting.ScriptingUtil;
import com.liferay.portal.kernel.util.ClassLoaderPool;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.documentlibrary.NoSuchFileEntryException;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;

/**
 * This class provide support utilities for manage dynamic scripts.
 * The dynamic script is introduced in the spring liferay plugin
 * that use liferay DL to store user customized scripts
 * dynamically.
 * All liferay portlets use groovy scripts can leverage this mechanism
 * to get runtime customizeable application logic.
 * 
 * @author Miles Huang
 *
 */
public class DynamicScriptingUtil {

	private static Pattern pattern = Pattern.compile("\\(.*\\)");
	private static Log _log = LogFactoryUtil.getLog(DynamicScriptingUtil.class);

	public static final String CUSTOM_TEMPLATE_PREF_PREFIX="CUSTOM-TEMPLATE:";
	public static final String CUSTOM_SCRIPT_PREF_PREFIX ="CUSTOM-SCRIPT:";
	private static final String SCRIPT_CONTEXT_KEY = "com.brownstonetech.springliferay.script.context";
	
	public static interface TemplateResolver {
		InputStream getTemplateAsStream(String templatePath) throws IOException, PortalException, SystemException;
	}
	
	/**
	 * This method return input stream of a given ftl template.
	 * By providing different implementation of TemplateResolver,
	 * we can search templates in different location, such as web-content folder of the plugin,
	 * or document library, or both.
	 * 
	 * @param resolver An external resolver that can resolve templatePath to template inputStream.
	 * @param templatePath
	 * @param search When search is true, this method will search the template
	 * from the specified directory up to the ancestor paths to find if there is
	 * some template matches.
	 * This search logic is same way as how the ftl template engine finding a
	 * template prefixed with path *
	 * @return null if the template is not available,
	 * or the input stream for reading found template.
	 * @throws IOException 
	 * @throws SystemException 
	 * @throws PortalException 
	 * @see #getStaticTemplateStream(ServletContext, String, boolean)
	 * @see #getTemplateStream(ServletContext, String, boolean)
	 */
	public static InputStream getTemplateStream(TemplateResolver resolver, String templatePath, boolean search) throws PortalException, SystemException, IOException {
		InputStream inputStream = null;
		if ( templatePath.startsWith("/") ) {
			templatePath = templatePath.substring(1);
		}
		String fileName = null;
		String path = null;
		if ( search ) {
			int pos = templatePath.lastIndexOf('/');
			if ( pos >= 0 ) {
				fileName = templatePath.substring(pos+1);
				path = templatePath.substring(0, pos);
			} else {
				path = StringPool.BLANK;
				fileName = templatePath;
			}
		}
		do {
			inputStream = resolver.getTemplateAsStream(templatePath);
			if ( inputStream == null && search ) {
				if (Validator.isNotNull(path)) {
					int pos = path.lastIndexOf('/');
					StringBuilder sb = new StringBuilder();
					if ( pos >= 0 ) {
						path = path.substring(0, pos);
						sb.append(path).append('/');
					} else {
						path = StringPool.BLANK;
					}
					templatePath = sb.append(fileName).toString();
				} else {
					break;
				}
			}
		} while ( search && inputStream == null );
		return inputStream;
	}

	/**
	 * This method will find template stream from "static" template locations,
	 * which is located in the portlet web application /WEB-INF/ directory.
	 * @param portletServletContext
	 * @param templatePath
	 * @param search
	 * @return null if the template is not found
	 * @throws IOException 
	 * @throws SystemException 
	 * @throws PortalException 
	 */
	public static InputStream getStaticTemplateStream(
			final ServletContext portletServletContext, String templatePath,
			boolean search) throws PortalException, SystemException, IOException {
		return getTemplateStream( new TemplateResolver() {

			public InputStream getTemplateAsStream(String templatePath) {
				// mimic com.surwing.struts2.liferay.views.freemarker.NSWebappTemplateLoader
				// Strip portlet tempalte namespace in the template path since the static resource
				// never support namespace.
				templatePath = stripNamespace(templatePath);
				InputStream inputStream = portletServletContext.getResourceAsStream("/WEB-INF/"+templatePath);
				return inputStream;
			}
		}, templatePath, search);
	}
	
	public static InputStream getTemplateStream(
			final ServletContext portletServletContext, final long scopeGroupId,
			final PortletPreferences preferences, String customTemplate,
			boolean search, final String tracePortletContext) throws PortalException, SystemException, IOException {
		return getTemplateStream( new TemplateResolver() {
			public InputStream getTemplateAsStream(String templatePath) throws IOException, PortalException, SystemException {
				InputStream inputStream;
				// mimic com.surwing.struts2.liferay.views.freemarker.DLTemplateLoader
				DLFileEntry fileEntry = getDLTemplateSource(scopeGroupId, preferences, templatePath, tracePortletContext);
				if ( fileEntry != null ) {
					inputStream = DLFileEntryLocalServiceUtil.getFileAsStream(0L, fileEntry.getFileEntryId(), fileEntry.getVersion(), false);
					return inputStream;
				}
				// try static resources if the template have not been customized at runtime.
				// Strip portlet tempalte namespace in the template path since the static resource
				// never support namespace.
				templatePath = stripNamespace(templatePath);
				// mimic com.surwing.struts2.liferay.views.freemarker.NSWebappTemplateLoader
				inputStream = portletServletContext.getResourceAsStream("/WEB-INF/"+templatePath);
				return inputStream;
			}
		}, customTemplate, search);
	}
	
	/**
	 * Strip namespace from the ftl template path if the template path contains ftl namespace.
	 * @param namespacedTemplatePath
	 * @return stripped ftl template path
	 * or the original path if it doesn't contains namespace.
	 */
	public static String stripNamespace(String namespacedTemplatePath) {
		String ret = namespacedTemplatePath;
		if ( ret.indexOf('(') < 0 ) {
			return ret;
		}
		
		ret = pattern.matcher(namespacedTemplatePath).replaceAll("");
		return ret;
	}

	/**
	 * Get template from Document Library as an FileEntry if there is such define.
	 * @param scopeGroupId portletScopeGroupId.
	 * @param preference Portlet preference of the portlet.
	 * @param namespacedTemplatePath
	 * @param tracePortletContext
	 * @return null if the customized template is not found in the DL, for
	 * the specified portlet.
	 * Otherwise the fileEntry that contains the customized template is returned.
	 * @throws IOException
	 */
	public static DLFileEntry getDLTemplateSource(long scopeGroupId, PortletPreferences preference,
			String namespacedTemplatePath, String tracePortletContext) throws IOException, PortalException, SystemException {
		// This template loader can only deal with namespaced names
		if ( namespacedTemplatePath.indexOf('(') == -1 ) {
			return null;
		}
		String name = DynamicScriptingUtil.stripNamespace(namespacedTemplatePath);
		if ( _log.isDebugEnabled() ) {
			_log.debug("Call findTemplateSource(\""+namespacedTemplatePath+"\", finding path is \""+name+"\"");
		}
		String uuid = preference.getValue(CUSTOM_TEMPLATE_PREF_PREFIX+name, null);
		if ( Validator.isNotNull(uuid) ) {
			try {
				DLFileEntry fileEntry = DLFileEntryLocalServiceUtil.getDLFileEntryByUuidAndGroupId(uuid, scopeGroupId);
				return fileEntry;
			} catch (NoSuchFileEntryException e) {
				_log.warn("Overriden FTL template "+name+" is missing. Please re-configure the portlet "+tracePortletContext);
			}
		}
		return null;
	}
	
	public static String getDLScriptParameterName(String scriptPath) {
		// strip leading slash
		StringBuilder sb = new StringBuilder();
		sb.append(CUSTOM_SCRIPT_PREF_PREFIX);
		if ( !scriptPath.startsWith("/") ) {
			sb.append('/');
		}
		sb.append(scriptPath);
		return sb.toString();
	}
	
	public static DLFileEntry getDLScriptSource(long scopeGroupId, PortletPreferences preference,
			String scriptName, String tracePortletContext) throws PortalException, SystemException {
		
		String parameterName = getDLScriptParameterName(scriptName);
		String uuid = preference.getValue(parameterName, null);
		if ( Validator.isNotNull(uuid) ) {
			try {
				DLFileEntry fileEntry = DLFileEntryLocalServiceUtil.getFileEntryByUuidAndGroupId(uuid, scopeGroupId);
				return fileEntry;
			} catch (NoSuchFileEntryException e) {
				_log.warn("Custom script :"+scriptName+" is missing. Please re-configure the portlet "+tracePortletContext);
			}
		}
		return null;
	}
	
	public static InputStream getDLScriptStream(DLFileEntry fileEntry)
			throws PortalException, SystemException {
		InputStream inputStream;
		inputStream = DLFileEntryLocalServiceUtil.getFileAsStream(0L, fileEntry.getFileEntryId(), fileEntry.getVersion(), false);
		return inputStream;
	}
	
	public static void runScript(LiferayRequestContext portletInvocation,
			String scriptPath, ScriptHandler handler)
					throws ScriptingException, SystemException {
		PortletRequest portletRequest = portletInvocation.getPortletRequest();
		PortletResponse portletResponse = portletInvocation.getPortletResponse();
		String portletId = portletInvocation.getPortletId();
		String cacheKey = ScriptCacheUtil.generateCacheKey(portletInvocation.getPlid(),
				portletId, scriptPath);
		PortletPreferences preferences = portletRequest.getPreferences();
		CachedScript cachedScript = ScriptCacheUtil.getScript(cacheKey, 
				portletInvocation.getScopeGroupId(), preferences, scriptPath, 
				portletId);
		Map<String, Object> scriptContext = null;
		if ( cachedScript != null ) {
			scriptContext = populateScriptingContext(portletInvocation.getPortletConfig(),
					portletRequest, portletResponse);
			handler.populateAdditionalScriptContext(scriptContext);
			// TODO: check if we can simplify it
			Map<String, Object> ret = ScriptingUtil.eval(null, scriptContext,
					handler.getOutputVariables(), "groovy", cachedScript.getScript(),
					ClassLoaderPool.getContextName(PortalClassLoaderUtil.getClassLoader()),
					ClassLoaderPool.getContextName(Thread.currentThread().getContextClassLoader()));
			handler.handleScriptResult(ret);
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
		ThemeDisplay themeDisplay = LiferayRequestContext.getThemeDisplay(request);
		PermissionChecker permissionChecker = LiferayRequestContext.getPermissionChecker(request);
		scriptContext.put("themeDisplay", themeDisplay);
		scriptContext.put("permissionChecker", permissionChecker);
		scriptContext.put("portletDisplay", themeDisplay.getPortletDisplay());
		scriptContext.put("portletRequest", request);
		scriptContext.put("portletResponse", response);
		request.setAttribute(SCRIPT_CONTEXT_KEY, scriptContext);
		
		return scriptContext;
	}
	
	
}
