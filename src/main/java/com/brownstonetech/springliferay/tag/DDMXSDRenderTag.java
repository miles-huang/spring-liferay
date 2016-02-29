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

package com.brownstonetech.springliferay.tag;

import java.util.Locale;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.dynamicdatalists.model.DDLRecordSet;
import com.liferay.portlet.dynamicdatalists.model.DDLRecordVersion;
import com.liferay.portlet.dynamicdatamapping.model.DDMStructure;
import com.liferay.portlet.dynamicdatamapping.storage.Fields;
import com.liferay.portlet.dynamicdatamapping.storage.StorageEngineUtil;
import com.liferay.portlet.dynamicdatamapping.util.DDMXSDUtil;
 
public class DDMXSDRenderTag extends TagSupport {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Log _log = LogFactoryUtil.getLog(DDMXSDRenderTag.class);
	
    private DDLRecordSet recordSet;
	private long detailDDMTemplateId;
    private DDLRecordVersion recordVersion;
    private boolean readOnly;
	private String namespace = StringPool.BLANK;
    private String mode = null;
   
    @Override
    public int doStartTag() throws JspException {
    	DDMStructure ddmStructure = null;
        try {
        	ThemeDisplay themeDisplay = (ThemeDisplay)pageContext.getRequest().getAttribute(WebKeys.THEME_DISPLAY);
        	Locale locale = themeDisplay.getLocale();
        	String portletNamespace = themeDisplay.getPortletDisplay().getNamespace();
        	
            //Get the writer object for output.
            JspWriter out = pageContext.getOut();
 
            ddmStructure = recordSet.getDDMStructure(detailDDMTemplateId);
			Fields fields = null;

			if (recordVersion != null) {
				long ddmStorageId = recordVersion.getDDMStorageId();
				fields = StorageEngineUtil.getFields(ddmStorageId);
			}

			String html = DDMXSDUtil.getHTML(pageContext, 
					ddmStructure.getXsd(), fields, portletNamespace,
					namespace, mode, readOnly, locale);
            out.append(html);
 
        } catch (Exception e) {
			if ( _log.isErrorEnabled() ) {
				_log.error(new StringBuilder("Fail to generate DDL Record form, companyId=")
					.append(recordSet.getCompanyId())
					.append(", recordSetId=").append(recordSet.getRecordSetId())
					.append(", ddmStructureId=").append(ddmStructure==null?"null":ddmStructure.getStructureId())
					.append(", detailDDMTemplateId=").append(detailDDMTemplateId)
					.append(", recordId=").append(recordVersion==null?"null":recordVersion.getRecordId())
					.append(", recordVersionId=").append(recordVersion==null?"null":recordVersion.getRecordVersionId()).toString(),
					e);
			}
        }

        return SKIP_BODY;
    }

    public void setRecordSet(DDLRecordSet recordSet) {
		this.recordSet = recordSet;
	}

	public void setDetailDDMTemplateId(long detailDDMTemplateId) {
		this.detailDDMTemplateId = detailDDMTemplateId;
	}

	public void setRecordVersion(DDLRecordVersion recordVersion) {
		this.recordVersion = recordVersion;
	}

    public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

}