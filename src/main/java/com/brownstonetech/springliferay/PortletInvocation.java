package com.brownstonetech.springliferay;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.Locale;

import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.servlet.ServletContext;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.portlet.PortletBagPool;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.theme.ThemeDisplay;

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
	
	
	private PortletRequest portletRequest;
	private PortletResponse portletResponse;
	private PortletConfig portletConfig;
	private String tplNS;
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
		this.permissionChecker = PortalExtUtil.getPermissionChecker(portletRequest);
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
		if ( themeDisplay == null ) themeDisplay = 
				PortalExtUtil.getThemeDisplay(portletRequest);
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
	
	public String getRootPortletId() {
		return getThemeDisplay().getPortletDisplay().getRootPortletId();
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

	public ServletContext getServletContext() {
		ServletContext context = null;
		if ( servletContext != null ) context = servletContext.get();
		if ( context != null) {
			return context;
		}
		// Lazy load servletContext
		String rootPortletId = getRootPortletId();
		context = PortletBagPool.get(rootPortletId).getServletContext();
		servletContext = new WeakReference<ServletContext>(context);
		return context;
	}
	
	public void hidePortlet() {
		PortalExtUtil.hidePortlet(portletRequest);
	}
	
	public void setPortletTitle(String title) {
		PortalExtUtil.setPortletTitle(portletResponse, title);
	}

	public String getTplNS() {
		if ( tplNS != null ) return tplNS;
		String instanceId = getThemeDisplay().getPortletDisplay().getInstanceId();
		StringBuilder sb = new StringBuilder(20).append('(').append(getPlid());
		if ( instanceId != null ) {
			sb.append('_').append(instanceId);
		}
		sb.append(')');
		tplNS = sb.toString();
		return tplNS;
	}

	public String getPortletUniqueId() {
		return PortalExtUtil.getPortletUniqueId(portletRequest);
	}

}
