package com.brownstonetech.springliferay.util.dynamicdatalist;

import com.brownstonetech.springliferay.util.dynamicdatalist.TemplateNode.TemplateNodeTypeHandler;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.auth.PrincipalException;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.documentlibrary.NoSuchFileEntryException;
import com.liferay.portlet.documentlibrary.service.DLAppServiceUtil;
import com.liferay.portlet.dynamicdatamapping.model.DDMStructure;

//	private void generateUrl() {
//		String url = StringPool.BLANK;
//		if ( getType().equals(DDMFieldTypes.TYPE_DDM_DOCUMENTLIBRARY) ) {
//			url = genDLDownloadURL();
//		} else if ( getType().equals(DDMFieldTypes.TYPE_DDM_LINK_TO_PAGE) ){
//			url = genLinkToPageURL();
//		} else {
//			url = super.getUrl();
//		}
//		setUrl(url);
//	}
//
	public class DDMDocumentLibraryHandler implements TemplateNodeTypeHandler {
		private static Log _log = LogFactoryUtil.getLog(DDMDocumentLibraryHandler.class);

		@Override
		public void handle(DDMStructure ddmStructure, TemplateNode templateNode, ThemeDisplay themeDisplay) {
			String value = (String)templateNode.getValue();
			String url = StringPool.BLANK;
			if ( Validator.isNotNull(value)) {
				try {
					JSONObject fileJSONObject = JSONFactoryUtil.createJSONObject(value);
					String fileEntryUUID=fileJSONObject.getString("uuid");
					long scopeGroupId=fileJSONObject.getLong("groupId");
					FileEntry fileEntry = DLAppServiceUtil.getFileEntryByUuidAndGroupId(fileEntryUUID, scopeGroupId);
					String fileName = HtmlUtil.unescape(fileEntry.getTitle());
					templateNode.put(TemplateNode.NODE_ATTR_FILE_NAME, fileName);
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
			templateNode.put(TemplateNode.NODE_ATTR_URL, url);
		}

		@Override
		public String getType() {
			return DDMFieldTypes.TYPE_DDM_DOCUMENTLIBRARY;
		}
		
	}