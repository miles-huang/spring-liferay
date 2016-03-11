package com.brownstonetech.springliferay.util.dynamicdatalist;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brownstonetech.springliferay.util.dynamicdatalist.DDLExtUtil.StructureRenderModelTypeHandler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portlet.dynamicdatamapping.model.DDMStructure;
import com.liferay.portlet.dynamicdatamapping.storage.FieldConstants;

public class SelectHandler implements StructureRenderModelTypeHandler {
	private static Log _log = LogFactoryUtil.getLog(SelectHandler.class);

	@Override
	public void handle(Map<String, Serializable> structureFieldModel, 
			DDMStructure ddmStructure, String languageId) {
		// TODO move this to structureRenderModel
		if ( ddmStructure == null ) return;
		String fieldName = (String)structureFieldModel.get(FieldConstants.NAME);
		try {
			HashMap<String, String> optionsMap = new HashMap<String, String>();
			List<String> childFields = ddmStructure.getChildrenFieldNames(fieldName);
			for ( String childFieldName: childFields ) {
				Map<String, String> fieldsMap = ddmStructure.getFields(
						fieldName, FieldConstants.NAME, childFieldName,
						languageId);
				optionsMap.put(fieldsMap.get(FieldConstants.VALUE), fieldsMap.get(FieldConstants.LABEL));
			}
			structureFieldModel.put(DDLExtUtil.STRUCTURE_FIELD_OPTIONS, optionsMap);
		} catch (Exception e) {
			_log.error("Unexpected exception when parsing select/checkbox options, ddmStructure="
					+ddmStructure.getName(LocaleUtil.getSiteDefault())
					+", fieldName="+fieldName, e);
		}
	}
	
}