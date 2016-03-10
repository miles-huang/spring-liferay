package com.brownstonetech.springliferay.freemarker;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.brownstonetech.springliferay.util.scripting.CachedScript;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.dynamicdatamapping.NoSuchTemplateException;
import com.liferay.portlet.dynamicdatamapping.model.DDMTemplate;
import com.liferay.portlet.dynamicdatamapping.service.DDMTemplateLocalServiceUtil;
import com.liferay.portlet.portletdisplaytemplate.util.PortletDisplayTemplateUtil;

public class ADTTemplateLoaderUtil {

	private static Log _log = LogFactoryUtil.getLog(ADTTemplateLoaderUtil.class);

	private static final Pattern URI_PATTERN_DISPLAY_STYLE = Pattern.compile(
			"^adt/displayStyle/([0-9]+)/(.+)\\.ftl$");
	private static final Pattern URI_PATTERN_TEMPLATE_KEY = Pattern.compile(
			"^adt/templateKey/([^/]+)/([0-9]+)/(.+)\\.ftl$");
	
	public static class TemplateSource {
		private long templateId;
		private long modifiedTime;
		
		@Override
		public String toString() {
			return "TemplateSource [templateId=" + templateId + ", modifiedTime=" + modifiedTime + "]";
		}
		public TemplateSource(long templateId, long modifiedTime) {
			this.templateId = templateId;
			this.modifiedTime = modifiedTime;
		}
		
		public long getTemplateId() {
			return templateId;
		}
		
		public long getModifiedTime() {
			return modifiedTime;
		}
		
		public void setModifiedTime(long modifiedTime) {
			this.modifiedTime = modifiedTime;
		}
	}
	
	public static TemplateSource findTemplateSource(String name) throws IOException {
		Matcher matcher = URI_PATTERN_DISPLAY_STYLE.matcher(name);
		if ( matcher.matches()  ) {
			return findTemplateSourceDisplayStyle(name, matcher);
		}
		matcher = URI_PATTERN_TEMPLATE_KEY.matcher(name);
		if ( matcher.matches() ) {
			return findTemplateSourceTemplateKey(name, matcher);
		}
		if ( _log.isDebugEnabled() ) {
			_log.debug("Template name "+name+" doesn't match with ADT template loader URI pattern, ignored by ADTTemplateLoader.");
		}
		return null;
	}

	private static TemplateSource findTemplateSourceDisplayStyle(String name, Matcher matcher) throws IOException {
		String displayStyle = matcher.group(2);
		long displayStyleGroupId = Long.parseLong(matcher.group(1));
		DDMTemplate ddmTemplate = PortletDisplayTemplateUtil.fetchDDMTemplate(
				displayStyleGroupId, displayStyle);
		if ( ddmTemplate == null ) {
			if (_log.isDebugEnabled()) {
				_log.debug("DDMTemplate "+name+" parsed as groupId="+displayStyleGroupId+", displayStyle="+displayStyle+" not found.");
			}
			return null;
		}
		TemplateSource templateSource = new TemplateSource(ddmTemplate.getTemplateId(),
				ddmTemplate.getModifiedDate().getTime());
		
		return templateSource;
	}

	private static TemplateSource findTemplateSourceTemplateKey(String name, Matcher matcher) throws IOException {
		String className = matcher.group(1);
		long scopeGroupId = Long.parseLong(matcher.group(2));
		long displayStyleGroupId = PortletDisplayTemplateUtil.getDDMTemplateGroupId(scopeGroupId);
		String templateKey = matcher.group(3);
		long classNameId = PortalUtil.getClassNameId(className);
		if ( classNameId == 0 ) {
			if (_log.isWarnEnabled()) {
				_log.warn("DDMTemplate "+name+" resolve failure because className is invalid. Parsed as TemplateKey URI: groupId="+displayStyleGroupId
						+ ", className=" + className + "(" + classNameId + "), templateKey="
						+ templateKey);
			}
			return null;
		}
		try {
			DDMTemplate ddmTemplate = DDMTemplateLocalServiceUtil.fetchTemplate(displayStyleGroupId, classNameId, templateKey, true);
			if ( ddmTemplate == null ) {
				if (_log.isDebugEnabled()) {
					_log.debug("DDMTemplate "+name+" not exists. Parsed as TemplateKey URI: groupId="+displayStyleGroupId
							+ ", className=" + className + "(" + classNameId + "), templateKey="
							+ templateKey);
				}
				return null;
			}
			TemplateSource templateSource = new TemplateSource(ddmTemplate.getTemplateId(),
					ddmTemplate.getModifiedDate().getTime() );
			
			return templateSource;
		} catch (Exception e) {
			_log.error("Unexpected exception when loading DDMTemplate "+name+". Parsed as TemplateKeyURI: groupId="+displayStyleGroupId
							+ ", className=" + className + "(" + classNameId + "), templateKey="
							+ templateKey);
		}
		return null;
	}
	
	public static long getLastModified(TemplateSource src) {
		try {
			DDMTemplate ddmTemplate = DDMTemplateLocalServiceUtil.getTemplate(
					src.getTemplateId());
			src.setModifiedTime(ddmTemplate.getModifiedDate().getTime());
			return src.getModifiedTime();
		} catch (NoSuchTemplateException e) {
			_log.error("DDMTemplate no longer exists. TemplateId= "+src.getTemplateId());
			return System.currentTimeMillis();
		} catch (Exception e) {
			_log.error("Fail to load DDMTemplate. TemplateId= "+src.getTemplateId(), e);
		}
		return src.getModifiedTime();
	}

	public static CachedScript getCachedScript(TemplateSource src) throws IOException {
		try {
			DDMTemplate ddmTemplate = DDMTemplateLocalServiceUtil.getTemplate(
					src.getTemplateId());
			CachedScript cachedScript = new CachedScript(
					ddmTemplate.getModifiedDate(), ddmTemplate.getScript());
			return cachedScript;
		} catch (NoSuchTemplateException e) {
			throw new FileNotFoundException("DDMTemplate not exists. TemplateId= "+src.getTemplateId());
		} catch (Exception e) {
			throw new IOException("Fail to load DDMTemplate. TemplateId= "+src.getTemplateId(), e);
		}
		
	}
	
	public static CachedScript fetchCachedScript(String templateURI) throws IOException {
		TemplateSource templateSource = findTemplateSource(templateURI);
		if ( templateSource != null ) {
			CachedScript cachedScript = getCachedScript(templateSource);
			return cachedScript;
		}
		return null;
	}
	
	public static long getTemplateId(String templateURI) throws IOException {
		TemplateSource templateSource = (TemplateSource)findTemplateSource(templateURI);
		if ( templateSource != null ) {
			return templateSource.getTemplateId();
		}
		return 0L;
	}
	
	public static Reader getReader(TemplateSource templateSource)
			throws IOException {
		CachedScript cachedScript = getCachedScript(templateSource);
		TemplateSource src = (TemplateSource)templateSource;
		src.setModifiedTime(cachedScript.getLastUpdateTime());
		Reader reader = new StringReader(cachedScript.getScript());
		return reader;
	}
	
}
