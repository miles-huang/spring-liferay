package com.brownstonetech.springliferay.component;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Iterator;
import java.util.TimeZone;

import javax.portlet.PortletRequest;

import org.springframework.stereotype.Component;

import com.liferay.portal.NoSuchRoleException;
import com.liferay.portal.NoSuchUserException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.RoleConstants;
import com.liferay.portal.model.User;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.service.UserGroupRoleLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.taglib.aui.AUIUtil;

@Component("renderHelper")
public class RenderHelper implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Log _log = LogFactoryUtil.getLog(RenderHelper.class);

	public String toString(Object o) {
		return String.valueOf(o);
	}

	public String toJSONString(Object o) {
		return JSONFactoryUtil.looseSerializeDeep(o);
	}
	
	public Iterator<String> retrieveSessionErrors(PortletRequest portletRequest) {
		Iterator<String> errors =
				SessionErrors.iterator(portletRequest);
		return errors;
	}
	
	public Iterator<String> retrieveSessionMessages(PortletRequest portletRequest) {
		Iterator<String> errors =
				SessionMessages.iterator(portletRequest);
		return errors;
	}

	public String auiBuildControlGroupCss(
			boolean inlineField, String inlineLabel, String wrapperCssClass,
			String baseType) {
		return AUIUtil.buildControlGroupCss(
				inlineField, inlineLabel, wrapperCssClass,
				baseType);
	}
	
	/**
	 * Get a java.sql.Date instance which represents same date as the specificied
	 * calendar value.
	 * 
	 * @param calendar
	 * @return
	 */
	public java.sql.Date getDate(Calendar calendar) {
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int date = calendar.get(Calendar.DAY_OF_MONTH);
		Calendar c = Calendar.getInstance(TimeZone.getDefault());
		c.set(year, month, date);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return new java.sql.Date(c.getTimeInMillis());
	}
	
	/**
	 * Get a java.sql.Date instance which represents same date as the specified
	 * date value in the specified time zone.
	 * <p>
	 * If the specified data value is already an instance of java.sql.Date,
	 * the java.sql.Date value is returned without any modification.
	 * 
	 * @param date The specified java.sql.Date value
	 * @param timeZone The specified time zone.
	 * 
	 * @return
	 */
	public java.sql.Date getDate(java.util.Date date, TimeZone timeZone) {
		if ( date instanceof java.sql.Date) {
			return (java.sql.Date)date;
		}
		Calendar calendar = Calendar.getInstance(timeZone);
		calendar.setTime(date);
		return getDate(calendar);
	}

	public User getUser(Object userIdObj) {
		if ( userIdObj == null ) {
			_log.debug("UserId is null]");
			return null;
		}
		Long userId = null;
		if ( userIdObj instanceof Number) {
			userId = ((Number)userIdObj).longValue();
		} else if ( userIdObj instanceof String ) {
			try {
				userId = Long.valueOf((String)userIdObj);
			} catch (NumberFormatException e) {
				_log.warn("Invalid userId, it is not a number:"+userIdObj);
				return null;
			}
		}
		if ( userId == null ) {
			_log.warn("Not supported userId type:"+userIdObj.getClass().getName());
			return null;
		}
		try {
			User user = UserLocalServiceUtil.getUser(userId);
			return user;
		} catch (NoSuchUserException e) {
			_log.debug("User is not found, userId="+userId);
			return null;
		} catch (PortalException e) {
			_log.error("Fail to fetch user domain object, userId="+userId, e);
			return null;
		} catch (SystemException e) {
			_log.error("Fail to fetch user domain object, userId="+userId, e);
			return null;
		}
	}
	
	public boolean isUserHasRole(Object userObj, String roleName) {
		User user = getUser(userObj);
		if ( user == null ) return false;
		long userId = user.getUserId();
		long companyId = user.getCompanyId();
		try {
			try {
				Role role = RoleLocalServiceUtil.getRole(companyId, roleName);
				if ( role.getType() != RoleConstants.TYPE_REGULAR ) {
					_log.warn("Only regular role can be checked by this method. Role is not a regular role: "+roleName);
					return false;
				}
				return RoleLocalServiceUtil.hasUserRole(userId, role.getRoleId());
			} catch (NoSuchRoleException e) {
				_log.error("Role not exists: "+roleName, e);
				return false;
			}
		} catch (PortalException e) {
			_log.error("Fail to check isUserHasRole, roleName="+roleName, e);
		} catch (SystemException e) {
			_log.error("Fail to check isUserHasRole, roleName="+roleName, e);
		}
		return false;
	}
	
	public boolean isUserHasGroupRole(Object userObj, String roleName, Object groupIdObj) {
		User user = getUser(userObj);
		if ( user == null ) return false;
		long userId = user.getUserId();
		long companyId = user.getCompanyId();
		Long groupId = null;
		if ( groupIdObj instanceof Number) {
			groupId = ((Number)groupIdObj).longValue();
		}
		if ( groupId == null ) {
			_log.error("Invalid groupId type, should be number");
			return false;
		}
		try {
			try {
				Role role = RoleLocalServiceUtil.getRole(companyId, roleName);
				if ( role.getType() != RoleConstants.TYPE_SITE ) {
					_log.warn("Only site role can be checked by this method. Role is not a site role: "+roleName);
					return false;
				}
				return UserGroupRoleLocalServiceUtil.hasUserGroupRole(userId, groupId, role.getRoleId());
			} catch (NoSuchRoleException e) {
				_log.error("Role not exists: "+roleName, e);
				return false;
			}
		} catch (PortalException e) {
			_log.error("Unexpected exception when check isUserHasGroupRole, userId="+userId+", groupId="+groupId+", roleName="+roleName, e);
		} catch (SystemException e) {
			_log.error("Unexpected exception when check isUserHasGroupRole, userId="+userId+", groupId="+groupId+", roleName="+roleName, e);
		}
		return false;
		
	}
}
