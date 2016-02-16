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

package com.brownstonetech.springliferay.util.dynamicdatalist;

import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.auth.PrincipalException;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.documentlibrary.NoSuchFileEntryException;
import com.liferay.portlet.documentlibrary.service.DLAppServiceUtil;
import com.liferay.portlet.dynamicdatamapping.storage.FieldConstants;

import java.io.Serializable;

/**
 * @author Miles Huang
 */
public class TemplateNode extends com.liferay.portal.kernel.templateparser.TemplateNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Log _log = LogFactoryUtil.getLog(TemplateNode.class);
	
	private String url;
	private ThemeDisplay themeDisplay;
	private String fileName=StringPool.BLANK;
	
	
	public TemplateNode(
		ThemeDisplay themeDisplay, String name, String data, String type,
		String dataType, Serializable value, String label) {

		super(themeDisplay, name, data, type);
		this.themeDisplay = themeDisplay;
		this.setDataType(dataType);
		this.setLabel(label);
		this.setValue(value);
		generateUrl();
	}

	private void setValue(Serializable value) {
		this.put(FieldConstants.VALUE, value);
	}
	
	public Serializable getValue() {
		return (Serializable)get(FieldConstants.VALUE);
	}
	
	private void setLabel(String label) {
		this.put(FieldConstants.LABEL, label);
	}
	
	public String getLabel() {
		return GetterUtil.getString(get(FieldConstants.LABEL), StringPool.BLANK);
	}

	private void setDataType(String dataType) {
		this.put(FieldConstants.DATA_TYPE, dataType);
	}
	
	public String getDataType() {
		return GetterUtil.getString(get(FieldConstants.DATA_TYPE), StringPool.BLANK);
	}

	public String getFileName() {
		return fileName;
	}
	
	@Override
	public String getUrl() {
		return url;
	}
	
	private void generateUrl() {
		String url = StringPool.BLANK;
		if ( getType().equals(DDMFieldTypes.TYPE_DDM_DOCUMENTLIBRARY) ) {
			url = genDLDownloadURL();
		} else if ( getType().equals(DDMFieldTypes.TYPE_DDM_FILEUPLOAD) ){
			url = genUploadFieldDownloadURL();
		} else {
			url = super.getUrl();
		}
		this.url = url;
	}

	private String genDLDownloadURL() {
		String value = (String)getValue();
		String url = StringPool.BLANK;
		if ( Validator.isNotNull(value)) {
			try {
				JSONObject fileJSONObject = JSONFactoryUtil.createJSONObject(value);
				String fileEntryUUID=fileJSONObject.getString("uuid");
				long scopeGroupId=fileJSONObject.getLong("groupId");
				FileEntry fileEntry = DLAppServiceUtil.getFileEntryByUuidAndGroupId(fileEntryUUID, scopeGroupId);
				fileName = HtmlUtil.unescape(fileEntry.getTitle());
				StringBuilder sb = new StringBuilder(themeDisplay.getPathContext())
					.append("/documents/").append(fileEntry.getRepositoryId()).append('/')
					.append(fileEntry.getFolderId()).append('/').append(HttpUtil.encodeURL(fileName, true))
					.append('/').append(fileEntry.getUuid());
				url = sb.toString();
			} catch (JSONException e) {
				_log.error("Failed to generate DL download url because of invalid JSON string: " +value);
			} catch (NoSuchFileEntryException e) {
				if ( _log.isDebugEnabled() ) {
					_log.debug("Can't generate DL download url because file entry not exist: "+value);
				}
			} catch (PrincipalException e) {
				if ( _log.isDebugEnabled() ) {
					_log.debug("Can't generate DL download url because user have no permission: "+value);
				}
			} catch (Exception e) {
				_log.error("Failed to generate DL download url because of unexpected Exception: "+value, e);
			}
		}
		return url;
	}
	
	private String genUploadFieldDownloadURL() {
		String value = (String)getValue();
		String url = StringPool.BLANK;
		if ( Validator.isNotNull(value)) {
			try {
				JSONObject fileJSONObject = JSONFactoryUtil.createJSONObject(value);
				fileName=fileJSONObject.getString("name");
				String className=fileJSONObject.getString("className");
				String classPK=fileJSONObject.getString("classPK");
				StringBuilder sb = new StringBuilder();
				sb.append("/documents/ddm/").append(className).append('/').append(classPK)
					.append('/').append(fileName);
				url = sb.toString();
			} catch (JSONException e) {
				_log.error("Failed to generate Upload field download url because of invalid JSON string: " +value);
			}
		}
		return url;
	}
	
}