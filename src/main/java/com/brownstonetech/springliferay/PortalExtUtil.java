package com.brownstonetech.springliferay;

import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.ServletContext;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletConfig;
import com.liferay.portal.kernel.portlet.PortletBagPool;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;

/**
 * 
 * Utility class that provide methods to do 
 * portlet related information retrieval.
 * 
 * @author Miles Huang
 *
 */
public class PortalExtUtil {
	
	/**
	 * Get themeDisplay from portletRequest object.
	 * 
	 * @param request
	 * @return
	 */
	public static ThemeDisplay getThemeDisplay(PortletRequest request) {
		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(WebKeys.THEME_DISPLAY);
		if (themeDisplay == null ) throw new IllegalStateException("ThemeDisplay not defined in request attribute");
		return themeDisplay;
	}

	/**
	 * Get portletConfig from portletRequest object.
	 * 
	 * @param portletRequest
	 * @return
	 */
//	public static PortletConfig getPortletConfig(PortletRequest request) throws SystemException, PortletException {
//		ServletContext servletContext = getServletContext(request);
//		ThemeDisplay themeDisplay = getThemeDisplay(request);
//		String portletId = themeDisplay.getPortletDisplay().getId();
//		PortletConfig portletConfig = PortalUtil.getPortletConfig(themeDisplay.getCompanyId(), portletId, servletContext);
//		return portletConfig;
//	}
	public static LiferayPortletConfig getPortletConfig(PortletRequest portletRequest) {
		LiferayPortletConfig liferayPortletConfig =
				(LiferayPortletConfig)portletRequest.getAttribute(
						JavaConstants.JAVAX_PORTLET_CONFIG);
		return liferayPortletConfig;
	}

	/**
	 * Get ServletContext of the invoking portlet from portletRequest object.
	 * 
	 * @param request
	 * @return
	 */
	public static ServletContext getServletContext(PortletRequest request) {
//		ThemeDisplay themeDisplay = getThemeDisplay(request);
//		String portletId = PortalUtil.getPortletId(request);
		String rootPortletId = getRootPortletId(request);
		if ( rootPortletId != null) {
			ServletContext servletContext = PortletBagPool.get(rootPortletId).getServletContext();
			return servletContext;
		}
		return null;
	}

	public static String getRootPortletId(PortletRequest portletRequest) {
		LiferayPortletConfig portletConfig = getPortletConfig(portletRequest);
		if ( portletConfig != null ) {
			return portletConfig.getPortlet().getRootPortletId();
		}
		throw new IllegalStateException("Can't get portletConfig from portletRequest.");
	}
	
	/**
	 * Get PermissionChecker from a portletRequest object.
	 * 
	 * @param request
	 * @return
	 * @throws SystemException
	 */
	public static PermissionChecker getPermissionChecker(PortletRequest request) throws SystemException {
		PermissionChecker permissionChecker = getThemeDisplay(request).getPermissionChecker();
		return permissionChecker;
	}
	
	/**
	 * Get a portletUniqueId from a portletRequest object.
	 * <p>
	 * PortletUniqueId contains portletId, and plid of the layout it resides in
	 * thus it will be unique across the whole Liferay server.
	 * <p>
	 * Notice the portletId returned from portletDisplay is only
	 * unique in one layoutset. If you exported a page setting and then later
	 * imports the page setting into
	 * another layoutset ( or website, company) in the same liferay server,
	 * the portletId would became duplicated. 
	 * 
	 * @param portletRequest
	 * @return
	 */
	public static String getPortletUniqueId(PortletRequest portletRequest) {
		ThemeDisplay themeDisplay = getThemeDisplay(portletRequest);
		long plid = themeDisplay.getPlid();
		String portletId = PortalUtil.getPortletId(portletRequest);
		return getPortletUniqueId(portletId, plid);
	}
	
	public static String getPortletUniqueId(String portletId, long plid) {
		String portletUniqueId = new StringBundler(3).append(portletId).append("_").append(plid).toString();
		return portletUniqueId;
	}
	
	/**
	 * Get a portletUniqueId for a company scoped portlet configuration
	 * @param companyId
	 * @param rootPortletId
	 * @return
	 */
	public static String getPortletUniqueId(long companyId, String rootPortletId) {
		String portletUniqueId = new StringBundler(4)
				.append(rootPortletId).append("_")
				.append("CO").append(companyId).toString();
		return portletUniqueId;
	}
	
	/**
	 * Hide the portlet from display.
	 * Notice this method only applicable for Render phase portlet request.
	 * Calling this method in other phase has no effect.
	 * 
	 * @param portletRequest
	 */
	public static void hidePortlet(PortletRequest portletRequest) {
		if ( portletRequest instanceof RenderRequest) {
			portletRequest.setAttribute(WebKeys.PORTLET_CONFIGURATOR_VISIBILITY, true);
		} else {
			_log.warn("Call hidePortlet has no effect on phase other than render phasae");
		}
	}

	/**
	 * Dynamically set the portlet title.
	 * This method can only be called in render phase.
	 * @param renderResponse
	 * @param title
	 */
	public static void setPortletTitle(PortletResponse renderResponse, String title) {
		if ( !(renderResponse instanceof RenderResponse)) {
			_log.warn("Call setPortletTitle has no effect on phase other than render phase");
			return;
		}
		((RenderResponse)renderResponse).setTitle(title);
	}

	public static String getTplNS(PortletRequest portletRequest) {
		ThemeDisplay themeDisplay = getThemeDisplay(portletRequest);
		String instanceId = themeDisplay.getPortletDisplay().getInstanceId();
		StringBuilder sb = new StringBuilder(20).append('(').append(themeDisplay.getPlid());
		if ( instanceId != null ) {
			sb.append('_').append(instanceId);
		}
		sb.append(')');
		String tplNS = sb.toString();
		return tplNS;
	}
	
	private static Log _log = LogFactoryUtil.getLog(PortalExtUtil.class);

}
