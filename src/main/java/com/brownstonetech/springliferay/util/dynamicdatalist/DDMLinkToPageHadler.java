package com.brownstonetech.springliferay.util.dynamicdatalist;

import com.brownstonetech.springliferay.util.dynamicdatalist.TemplateNode.TemplateNodeTypeHandler;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.Layout;
import com.liferay.portal.service.LayoutLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.dynamicdatamapping.model.DDMStructure;

public class DDMLinkToPageHadler implements TemplateNodeTypeHandler {
	private static Log _log = LogFactoryUtil.getLog(DDMLinkToPageHadler.class);

	@Override
	public void handle(DDMStructure ddmStructure, TemplateNode templateNode, ThemeDisplay themeDisplay) {
		String value = (String)templateNode.getValue();
		String url = StringPool.BLANK;
		if ( Validator.isNotNull(value)) {
			try {
				JSONObject fieldLayoutJSONObject = JSONFactoryUtil.createJSONObject(value);
				long fieldLayoutGroupId=fieldLayoutJSONObject.getLong("groupId");
				if ( fieldLayoutGroupId <=0 ) {
					fieldLayoutGroupId = themeDisplay.getScopeGroupId();
				}
				boolean privateLayout = fieldLayoutJSONObject.getBoolean("privateLayout");
				long layoutId = fieldLayoutJSONObject.getLong("layoutId");
				Layout fieldLayout = LayoutLocalServiceUtil.fetchLayout(fieldLayoutGroupId, privateLayout, layoutId);
				if ( fieldLayout != null ) {
					// overwrite with locale specific page name
					templateNode.put("data", fieldLayout.getName(themeDisplay.getLocale()));
					// TODO we don't have http request object here, so we can't call supposed method
					// fieldLayout.getRegularURL(request);
					// This may cause layout under different domain name lost session
					url = PortalUtil.getLayoutURL(fieldLayout, themeDisplay);
				}
			} catch (JSONException e) {
				_log.error("Failed to generate Link to page URL because of invalid JSON string: " +value);
			} catch (Exception e) {
				_log.error("Failed to generate Link to page URL because of unexpected Exception: "+value, e);
			}
		}
		templateNode.put(TemplateNode.NODE_ATTR_URL, url);
	}

	@Override
	public String getType() {
		return DDMFieldTypes.TYPE_DDM_LINK_TO_PAGE;
	}
	
}