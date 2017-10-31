package com.brownstonetech.springliferay.util.dynamicdatalist;

import java.util.Locale;

import javax.portlet.PortletURL;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.IndexerPostProcessor;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portlet.dynamicdatalists.model.DDLRecord;
import com.liferay.portlet.dynamicdatalists.model.DDLRecordSet;
import com.liferay.portlet.dynamicdatalists.model.DDLRecordVersion;
import com.liferay.portlet.dynamicdatamapping.model.DDMStructure;
import com.liferay.portlet.dynamicdatamapping.storage.Fields;
import com.liferay.portlet.dynamicdatamapping.storage.StorageEngineUtil;

/**
 * This post processor complements the LP 6.2 built in
 * <code> com.liferay.portlet.dynamicdatamapping.util.DDMIndexerImpl </code>
 * <p>
 * In this post processor, we add all keyword indexed text fields
 * with corresponding lower case indexed field so that case insensitive
 * search against keyword indexed text field can be implemented.
 * 
 * @author Miles Huang
 *
 */
public class DDLIndexerPostProcessor implements IndexerPostProcessor {
	
	private static Log _log = LogFactoryUtil.getLog(DDLIndexerPostProcessor.class);

	public void postProcessContextQuery(BooleanQuery contextQuery,
			SearchContext searchContext) throws Exception {
		// Nothing to do
	}

	public void postProcessDocument(Document document, Object obj)
			throws Exception {

		DDLRecord record = (DDLRecord)obj;

		DDLRecordVersion recordVersion = record.getRecordVersion();

		DDLRecordSet recordSet = recordVersion.getRecordSet();

		DDMStructure ddmStructure = recordSet.getDDMStructure();

		Fields fields = StorageEngineUtil.getFields(
			recordVersion.getDDMStorageId());

		DDMKeywordIndexerUtil.addAttributes(document, ddmStructure, fields);
	}

	public void postProcessFullQuery(BooleanQuery fullQuery,
			SearchContext searchContext) throws Exception {
		// Nothing to do
		if ( _log.isDebugEnabled() ) {
			_log.debug("DDLIndexer Full Query: "+fullQuery.toString());
		}
	}

	public void postProcessSearchQuery(BooleanQuery searchQuery,
			SearchContext searchContext) throws Exception {
		// Nothing to do
	}

	public void postProcessSummary(Summary summary, Document document,
			Locale locale, String snippet, PortletURL portletURL) {
		// Nothing to do
	}

}
