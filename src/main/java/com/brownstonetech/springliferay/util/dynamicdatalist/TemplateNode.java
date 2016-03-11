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

import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.dynamicdatamapping.model.DDMStructure;
import com.liferay.portlet.dynamicdatamapping.storage.FieldConstants;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Miles Huang
 */
public class TemplateNode extends com.liferay.portal.kernel.templateparser.TemplateNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Map<String, TemplateNodeTypeHandler> handlers;
	
	public static final String NODE_ATTR_URL="url";
	public static final String NODE_ATTR_FILE_NAME = "fileName";

	public interface TemplateNodeTypeHandler {
		public void handle(DDMStructure ddmStructure, TemplateNode templateNode, ThemeDisplay themeDisplay);

		public String getType();
	}

	public static void registerHandler(TemplateNodeTypeHandler handler) {
		handlers.put(handler.getType(), handler);
	}
	
	static {
		handlers = new HashMap<String, TemplateNodeTypeHandler>();
		registerHandler(new DDMDocumentLibraryHandler());
		registerHandler(new DDMLinkToPageHadler());
	}
		
	public TemplateNode(
		ThemeDisplay themeDisplay, DDMStructure ddmStructure, String name, String data, String type,
		String dataType, Serializable value, String label) {

		super(themeDisplay, name, data, type);
		this.setDataType(dataType);
		this.setLabel(label);
		this.setValue(value);
		postProcess(ddmStructure, this, themeDisplay);
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
		return (String)get(NODE_ATTR_FILE_NAME);
	}
	
	@Override
	public String getUrl() {
		return (String)get(NODE_ATTR_URL);
	}

	private static void postProcess(DDMStructure ddmStructure, TemplateNode templateNode, ThemeDisplay themeDisplay) {
		String type = templateNode.getType();
		TemplateNodeTypeHandler handler = handlers.get(type);
		if ( handler != null) {
			handler.handle(ddmStructure, templateNode, themeDisplay);
		}
	}
	
}