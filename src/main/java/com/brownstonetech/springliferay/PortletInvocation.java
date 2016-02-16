package com.brownstonetech.springliferay;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.Locale;

import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.ServletContext;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletBagPool;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;

/**
 * The PortletInvocation contains context information of current portlet
 * request invocation.
 * <p>
 * Following information can be retrieved from PortletInvocation:
 * </p>
 * <ul>
 * <li>Portlet Request</li>
 * <li>Portlet Response</li>
 * <li>Portlet Config</li>
 * <li>ThemeDisplay</li>
 * <li>Permission Checker</li>
 * </ul>
 * @author Miles Huang
 */
public class PortletInvocation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Log _log = LogFactoryUtil.getLog(PortletInvocation.class);
	
	
	private PortletRequest portletRequest;
	private PortletResponse portletResponse;
	private PortletConfig portletConfig;
	
	private WeakReference<ServletContext> servletContext;
	
	private ThemeDisplay themeDisplay;
	private PermissionChecker permissionChecker;
	
	public PortletInvocation(PortletConfig portletConfig,
			PortletRequest portletRequest,
			PortletResponse portletResponse) throws SystemException {
		if ( portletRequest == null ) {
			throw new NullPointerException("portletRequest is null");
		}
		this.portletRequest = portletRequest;
		this.portletResponse = portletResponse;
		this.portletConfig = portletConfig;
		this.permissionChecker = getPermissionChecker(portletRequest);
	}
	
	public PortletRequest getPortletRequest() {
		return portletRequest;
	}
	
	public PortletResponse getPortletResponse() {
		return portletResponse;
	}
	
	public PortletConfig getPortletConfig() {
		return portletConfig;
	}
	
	public ThemeDisplay getThemeDisplay() {
		if ( themeDisplay == null ) themeDisplay = getThemeDisplay(portletRequest);
		return themeDisplay;
	}
	
	public Locale getLocale() {
		return getThemeDisplay().getLocale();
	}
	
	public PermissionChecker getPermissionChecker() {
		return permissionChecker;
	}
	
	public String getURLCurrent() {
		return getThemeDisplay().getURLCurrent();
	}

	public String getPortletId() {
		return getThemeDisplay().getPortletDisplay().getId();
	}

	public long getPlid() {
		return getThemeDisplay().getPlid();
	}
	
	public long getScopeGroupId() {
		return getThemeDisplay().getScopeGroupId();
	}

	public long getCompanyId() {
		return getThemeDisplay().getCompanyId();
	}

	public static ThemeDisplay getThemeDisplay(PortletRequest request) {
		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(WebKeys.THEME_DISPLAY);
		if (themeDisplay == null ) throw new IllegalStateException("ThemeDisplay not defined in request attribute");
		return themeDisplay;
	}

	public static PortletConfig getPortletConfig(PortletRequest request) throws SystemException, PortletException {
		ThemeDisplay themeDisplay = getThemeDisplay(request);
		String portletId = themeDisplay.getPortletDisplay().getId();
		ServletContext servletContext = PortletBagPool.get(portletId).getServletContext();
		PortletConfig portletConfig = PortalUtil.getPortletConfig(themeDisplay.getCompanyId(), portletId, servletContext);
		return portletConfig;
	}

	public static ServletContext getServletContext(PortletRequest request) {
		ThemeDisplay themeDisplay = getThemeDisplay(request);
		String portletId = themeDisplay.getPortletDisplay().getId();
		ServletContext servletContext = PortletBagPool.get(portletId).getServletContext();
		return servletContext;
	}

	public ServletContext getServletContext() {
		ServletContext context = null;
		if ( servletContext != null ) context = servletContext.get();
		if ( context != null) {
			return context;
		}
		// Lazy load servletContext
		String portletId = getPortletId();
		context = PortletBagPool.get(portletId).getServletContext();
		servletContext = new WeakReference<ServletContext>(context);
		return context;
	}
	
	public static PermissionChecker getPermissionChecker(PortletRequest request) throws SystemException {
		PermissionChecker permissionChecker = getThemeDisplay(request).getPermissionChecker();
		return permissionChecker;
	}
	
	public static String getPortletUniqueId(PortletRequest portletRequest) {
		ThemeDisplay themeDisplay = getThemeDisplay(portletRequest);
		long plid = themeDisplay.getPlid();
		String portletId = themeDisplay.getPortletDisplay().getId();
		String portletUniqueId = new StringBundler(3).append(portletId).append("_").append(plid).toString();
		return portletUniqueId;
	}
	
	public static void hidePortlet(PortletRequest portletRequest) {
		if ( portletRequest instanceof RenderRequest) {
			portletRequest.setAttribute(WebKeys.PORTLET_CONFIGURATOR_VISIBILITY, true);
		}	
	}

	/**
	 * Dynamically set the portlet title.
	 * This method can only be called in render phase.
	 * @param title
	 */
	public static void setPortletTitle(PortletResponse renderResponse, String title) {
		if ( !(renderResponse instanceof RenderResponse)) {
			_log.warn("Call setPortletTitle has no effect on phase other than render phase");
		}
		((RenderResponse)renderResponse).setTitle(title);
	}
	
}
