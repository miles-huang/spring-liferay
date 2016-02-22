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

public class PermissionContext {
	protected long companyId;
	protected long scopeGroupId;
	protected String resourceName;
	protected Long resourcePK;
	protected Long ownerId;
	
	public PermissionContext( long companyId, long scopeGroupId, String resourceName, Long resourcePK,
			Long ownerId ) {
		this.companyId = companyId;
		this.resourceName = resourceName;
		this.scopeGroupId = scopeGroupId;
		this.resourcePK = resourcePK;
		this.ownerId = ownerId;
	}

	public long getCompanyId() {
		return companyId;
	}

	public String getResourceName() {
		return resourceName;
	}

	public Long getResourcePK() {
		return resourcePK;
	}

	public Long getScopeGroupId() {
		return scopeGroupId;
	}

	public Long getOwnerId() {
		return ownerId;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{ companyId=").append(companyId)
			.append(", scopeGroupId=").append(scopeGroupId)
			.append(", ResourceName=").append(resourceName)
			.append(", resourcePK=").append(resourcePK)
			.append(", ownerId=").append(ownerId)
			.append("}");
		return sb.toString();
	}
}
