package com.brownstonetech.springliferay;

import javax.portlet.PortletRequest;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.theme.ThemeDisplay;

public class LiferayControllerSupport {
	
	private static final String ERROR = "ERROR";
	
	@ExceptionHandler({ Exception.class })
	public String handleException(Exception exception) {
		_log.error("Unexcepted exception", exception);
		return "error_page";
	}
	
	/**
	 * This always existing modelAttribute is for binding global error messages.
	 * @return
	 */
	@ModelAttribute(SpringIntegrationWebKeys.ERROR)
	public Object getError() {
		return ERROR;
	}

	@ModelAttribute("themeDisplay")
	public ThemeDisplay themeDisplay(PortletRequest request) {
		return getThemeDisplay(request);
	}
	
	@ModelAttribute("permissionChecker")
	public PermissionChecker permissionChecker(PortletRequest request) {
		try {
			return getPermissionChecker(request);
		} catch (SystemException e) {
			_log.error("Fail to get portletRequest associated permissionChecker", e);
		}
		return null;
	}
	
	public static PermissionChecker getPermissionChecker(PortletRequest request) throws SystemException {
		PermissionChecker permissionChecker = getThemeDisplay(request).getPermissionChecker();
		return permissionChecker;
	}

	public static ThemeDisplay getThemeDisplay(PortletRequest request) {
		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(WebKeys.THEME_DISPLAY);
		if (themeDisplay == null ) throw new IllegalStateException("ThemeDisplay not defined in request attribute");
		return themeDisplay;
	}
	
	private static Log _log = LogFactoryUtil.getLog(LiferayControllerSupport.class);

}
