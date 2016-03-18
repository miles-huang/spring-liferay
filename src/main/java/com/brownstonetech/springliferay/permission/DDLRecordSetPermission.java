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

package com.brownstonetech.springliferay.permission;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.security.auth.PrincipalException;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portlet.dynamicdatalists.model.DDLRecordSet;
import com.liferay.portlet.dynamicdatalists.service.DDLRecordSetLocalServiceUtil;

/**
 * @author Marcellus Tavares
 */
public class DDLRecordSetPermission {

	public static void check(
			PermissionChecker permissionChecker, DDLRecordSet recordSet,
			String actionId)
		throws PortalException {

		if (!contains(permissionChecker, recordSet, actionId)) {
			throw new PrincipalException();
		}
	}

	public static void check(
			PermissionChecker permissionChecker, long recordSetId,
			String actionId)
		throws PortalException, SystemException {

		if (!contains(permissionChecker, recordSetId, actionId)) {
			throw new PrincipalException();
		}
	}

	public static void check(
			PermissionChecker permissionChecker, long groupId,
			String recordSetKey, String actionId)
		throws PortalException, SystemException {

		if (!contains(permissionChecker, groupId, recordSetKey, actionId)) {
			throw new PrincipalException();
		}
	}

	public static boolean contains(
		PermissionChecker permissionChecker, DDLRecordSet recordSet,
		String actionId) {

		if (permissionChecker.hasOwnerPermission(
				recordSet.getCompanyId(), DDLRecordSet.class.getName(),
				recordSet.getRecordSetId(), recordSet.getUserId(), actionId)) {

			return true;
		}

		return permissionChecker.hasPermission(
			recordSet.getGroupId(), DDLRecordSet.class.getName(),
			recordSet.getRecordSetId(), actionId);
	}

	public static boolean contains(
			PermissionChecker permissionChecker, long recordSetId,
			String actionId)
		throws PortalException, SystemException {

		DDLRecordSet recordSet = DDLRecordSetLocalServiceUtil.getRecordSet(
			recordSetId);

		return contains(permissionChecker, recordSet, actionId);
	}

	public static boolean contains(
			PermissionChecker permissionChecker, long groupId,
			String recordSetKey, String actionId)
		throws PortalException, SystemException {

		DDLRecordSet recordSet = DDLRecordSetLocalServiceUtil.getRecordSet(
			groupId, recordSetKey);

		return contains(permissionChecker, recordSet, actionId);
	}

}