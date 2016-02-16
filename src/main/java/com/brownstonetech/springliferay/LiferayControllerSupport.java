package com.brownstonetech.springliferay;

import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.portlet.context.PortletConfigAware;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.theme.ThemeDisplay;

/**
 * Controller base class.
 * It put some common liferay specific request content information as model attributes
 * <ul>
 * <li>themeDisplay: The {@link ThemeDisplay} of current request.</li>
 * <li>permissionChecker: The {@link PermissionChecker} of current request.</li>
 * <li>liferayInvocationContext: The {@link PortletInvocation} of current invocation.</li>
 * </ul>
 * 
 * @author Miles Huang
 *
 */
public class LiferayControllerSupport implements PortletConfigAware {
	
	private static final String ERROR = "ERROR";
	private PortletConfig portletConfig;
	
	@Override
	public void setPortletConfig(PortletConfig portletConfig) {
		this.portletConfig = portletConfig;
	}

	@ExceptionHandler({ Exception.class })
	public String handleException(Exception exception) {
		_log.error("Unexcepted exception", exception);
		return "error_page";
	}
	
	/**
	 * This always existing modelAttribute is for binding global error messages.
	 * @return
	 */
	@ModelAttribute(SpringLiferayWebKeys.ERROR)
	public Object getError() {
		return ERROR;
	}

	@ModelAttribute(SpringLiferayWebKeys.THEME_DISPLAY)
	public ThemeDisplay themeDisplay(PortletRequest request) {
		return PortletInvocation.getThemeDisplay(request);
	}
	
	@ModelAttribute(SpringLiferayWebKeys.PERMISSION_CHECKER)
	public PermissionChecker permissionChecker(PortletRequest request) {
		try {
			return PortletInvocation.getPermissionChecker(request);
		} catch (SystemException e) {
			_log.error("Fail to get portletRequest associated permissionChecker", e);
		}
		return null;
	}
	
	@ModelAttribute(SpringLiferayWebKeys.PORTLET_INVOCATION)
	public PortletInvocation portletInvocation (
			PortletRequest portletRequest,
			PortletResponse portletResponse
			) throws SystemException {
		return new PortletInvocation(portletConfig, 
				portletRequest, portletResponse);
	}
	
	private static Log _log = LogFactoryUtil.getLog(LiferayControllerSupport.class);

}
