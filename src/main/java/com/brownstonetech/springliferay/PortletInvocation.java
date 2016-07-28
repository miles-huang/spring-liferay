package com.brownstonetech.springliferay;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.Locale;
import java.util.TimeZone;

import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.WindowState;
import javax.servlet.ServletContext;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletBagPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.User;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.PortletURLFactoryUtil;

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
	private TimeZone userTimeZone;
	private TimeZone companyTimeZone;
		
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
		String portletId = getThemeDisplay().getPpid();
		if ( Validator.isNull(portletId)) {
			portletId = getThemeDisplay().getPortletDisplay().getId();
		}
		if ( Validator.isNull(portletId)) {
			_log.warn("Can't parse portletId for request: "+this.getURLCurrent()
				+"\nthemeDisplay: "+JSONFactoryUtil.looseSerializeDeep(themeDisplay));
		}
		return portletId;
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

	public WindowState getWindowState() {
		WindowState windowState = portletRequest.getWindowState();
		return windowState;
	}
	
	public boolean isWindowStateExclusive() {
		return LiferayWindowState.EXCLUSIVE.equals(getWindowState());
	}
	
	public boolean isWindowStateMaximized() {
		return WindowState.MAXIMIZED.equals(getWindowState());
	}
	
	public boolean isWindowStateMinimized() {
		return WindowState.MINIMIZED.equals(getWindowState());
	}
	
	public boolean isWindowStateNormal() {
		return WindowState.NORMAL.equals(getWindowState());
	}
	
	public boolean isWindowStatePopup() {
		return LiferayWindowState.POP_UP.equals(getWindowState());
	}
	
	public boolean isLayoutPrototypeLinkActive() {
		return themeDisplay.getLayout().isLayoutPrototypeLinkActive();
	}
	
	public boolean isSignedIn() {
		return themeDisplay.isSignedIn();
	}

	public TimeZone getUserTimeZone() {
		if ( userTimeZone != null ) {
			return userTimeZone;
		}
		if ( isSignedIn() ) {
			User user = themeDisplay.getUser();
			userTimeZone = user.getTimeZone();
		}
		if ( userTimeZone == null ) {
			userTimeZone = getCompanyTimeZone();
		}
		return userTimeZone;
	}
	
	public TimeZone getCompanyTimeZone() {
		if ( companyTimeZone != null ) {
			return companyTimeZone;
		}
		try {
			companyTimeZone = themeDisplay.getCompany().getTimeZone();
		} catch (Exception e) {
			_log.warn("Can't get timeZone from company", e);
		}
		if ( companyTimeZone == null ) {
			companyTimeZone = TimeZone.getDefault();
		}
		return companyTimeZone;
	}
	
	public LiferayPortletURL createRenderURL() {
		LiferayPortletURL url = PortletURLFactoryUtil.create(
				portletRequest,
				getPortletId(), getPlid(), 
				PortletRequest.RENDER_PHASE);
		try {
			url.setWindowState(portletRequest.getWindowState());
			url.setPortletMode(portletRequest.getPortletMode());
		} catch (Exception e) {
			_log.error(e,e);
		}
		return url;
	}
	
	private static Log _log = LogFactoryUtil.getLog(PortletInvocation.class);

	
}
