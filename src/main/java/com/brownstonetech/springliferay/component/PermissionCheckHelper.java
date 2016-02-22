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

package com.brownstonetech.springliferay.component;

import java.io.Serializable;

import org.springframework.stereotype.Component;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.theme.ThemeDisplay;

@Component("permissionCheckHelper")
public class PermissionCheckHelper implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Log _log = LogFactoryUtil.getLog(PermissionCheckHelper.class);
    
//	private PermissionContext getPermissionCheckerContext(
//			Long scopeGroupId, String resourceName, 
//			Long resourcePK, Long ownerId) {
//		return getPermissionCheckerContext(null,
//				scopeGroupId, resourceName, resourcePK, ownerId);
//	}
	
	public PermissionContext getPermissionCheckerContext(
			PermissionContext defaultContext,
			Long scopeGroupId,
			String resourceName,
			Long resourcePK,
			Long ownerId) {
    	String resourceNameValue = Validator.isNotNull(resourceName)?resourceName:
    			(defaultContext == null? null:defaultContext.getResourceName());

    	Long resourcePKValue = (resourcePK != null && resourcePK > 0)? resourcePK:
    		(defaultContext == null? 0: defaultContext.getResourcePK());

    	Long ownerIdValue = (ownerId != null && ownerId > 0)? ownerId:
    		(defaultContext == null? 0: defaultContext.getOwnerId());
    	
    	long scopeGroupIdValue = (scopeGroupId != null && scopeGroupId > 0)? scopeGroupId:
    		(defaultContext == null? 0: defaultContext.getScopeGroupId());
    	
    	PermissionContext permissionCheckerContext = new PermissionContext(
        		0, scopeGroupIdValue,
        		resourceNameValue, resourcePKValue, ownerIdValue);
    	return permissionCheckerContext;
	}

	public boolean hasPermission(ThemeDisplay themeDisplay,
			PermissionContext defaultContext, 
			String actionId,
			Long scopeGroupId,
			String resourceName,
			Long resourcePK,
			Long ownerId) {
		
		PermissionContext context = getPermissionCheckerContext(defaultContext,
				scopeGroupId, resourceName, resourcePK, ownerId);

		if ( themeDisplay == null ) {
			if ( _log.isWarnEnabled() ) {
				_log.warn("Permission check failed: Can't get themeDisplay from request. "+context.toString());
			}
			return false;
		}

		if ( Validator.isNull(context.getResourceName()) || context.getResourcePK()==null ) {
			if ( _log.isWarnEnabled() ) {
				_log.warn("Permission check failed: resourceName or resourcePK has no value. "+context.toString());
			}
			return false;
		}
		
		if ( _log.isDebugEnabled()) {
			_log.debug("Run permission check: "+context.toString()+", actionId="+actionId);
		}
		
		if ( Validator.isNull(actionId)) {
			if ( _log.isWarnEnabled()) {
				_log.warn("Permission check failed: actionId has no value");
			}
			return false;
		}
		
		PermissionChecker permissionChecker = themeDisplay.getPermissionChecker();

		boolean answer = false;

		if ( context.getOwnerId() != null && context.getOwnerId() > 0 ) {
			answer = permissionChecker.hasOwnerPermission(
					themeDisplay.getCompanyId(), context.getResourceName(),
					context.getResourcePK(),
					context.getOwnerId(), actionId);
			if ( _log.isDebugEnabled()) {
				_log.debug("Run owner permission check return: "+answer+": "+context.toString()+" check actionId="+actionId);
			}
		}
		if ( !answer ) {
			scopeGroupId = context.getScopeGroupId();
			if ( scopeGroupId == null || scopeGroupId <= 0 ) {
				scopeGroupId = themeDisplay.getScopeGroupId();
			}
			answer = permissionChecker.hasPermission(
					scopeGroupId, context.getResourceName(),
					context.getResourcePK(), actionId);
			if ( _log.isDebugEnabled()) {
				_log.debug("Run normal permission check return: "+answer+": "+context.toString()+" check actionId="+actionId);
			}
		}
		return answer;
	}
	
}
