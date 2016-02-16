/**
 * 
 * SURWING CONFIDENTIAL
 * 
 * Copyright (c) 2009-2013 Surwing Incorporated
 * All rights reserved.
 * 
 * NOTICE:  All information contained herein is, and remains the property
 * of Surwing Information Technology Inc. and its suppliers, if any. The
 * intellectual and technical concepts contained herein are proprietary to
 * Surwing Information Technology Inc. and its suppliers and may be covered
 * by China and Foreign Patents, patents in process, and are protected by
 * trade secret or copyright law.
 * Dissemination of this information or reproduction of this material is
 * strictly forbidden unless prior written permission is obtained from
 * Surwing Information Technology Inc.
 *
 */

package com.brownstonetech.springliferay.util;

import javax.portlet.PortletPreferences;

import com.liferay.portal.NoSuchGroupException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.Group;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.PortletPreferencesLocalServiceUtil;
import com.liferay.portal.util.PortletKeys;

/**
 * This utility help payment gateway plugins to
 * store configuration preferences that is shared across the
 * store scope group.
 * 
 * @author Miles Huang
 */
public class SpecialPortletPreferencesUtil {

	public static PortletPreferences getGroupSharedPreferences(
			long scopeGroupId, String portletId)
	throws SystemException, NoSuchGroupException {
		long ownerId = scopeGroupId;
		int ownerType = PortletKeys.PREFS_OWNER_TYPE_GROUP;
		long plid = PortletKeys.PREFS_PLID_SHARED;
		try {
			Group group = GroupLocalServiceUtil.getGroup(scopeGroupId);
			return PortletPreferencesLocalServiceUtil.getPreferences(
					group.getCompanyId(), ownerId, ownerType, plid, portletId);
		} catch (NoSuchGroupException e) {
			throw e;
		} catch (PortalException e) {
			throw new SystemException("Unexpected Portal Exception", e);
		}
	}

	public static PortletPreferences getCompanySharedPreferences(long companyId, String portletId)
	throws SystemException {
		long ownerId = companyId;
		int ownerType = PortletKeys.PREFS_OWNER_TYPE_COMPANY;
		long plid = PortletKeys.PREFS_PLID_SHARED;
		return PortletPreferencesLocalServiceUtil.getPreferences(
			companyId, ownerId, ownerType, plid, portletId);
	}
	
}
