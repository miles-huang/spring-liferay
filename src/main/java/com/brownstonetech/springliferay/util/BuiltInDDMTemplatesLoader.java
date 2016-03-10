package com.brownstonetech.springliferay.util;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.template.TemplateHandler;
import com.liferay.portal.kernel.template.TemplateHandlerRegistryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.Group;
import com.liferay.portal.service.CompanyLocalServiceUtil;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.dynamicdatamapping.model.DDMTemplate;
import com.liferay.portlet.dynamicdatamapping.model.DDMTemplateConstants;
import com.liferay.portlet.dynamicdatamapping.service.DDMTemplateLocalServiceUtil;
import com.liferay.util.ContentUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Derived from portal-impl AddDefaultDDMTemplatesAction
 * Since it is not registering templateResource for Plugin provided
 * template handlers.
 * 
 * @author Miles Huang
 */
public class BuiltInDDMTemplatesLoader {

	private static Log _log = LogFactoryUtil.getLog(BuiltInDDMTemplatesLoader.class);
	
	protected static void addOrUpdateDDMTemplate(
			long userId, long groupId, long classNameId, String templateKey,
			String name, String description, String language,
			String scriptFileName, boolean cacheable,
			ServiceContext serviceContext)
		throws PortalException, SystemException {

		DDMTemplate ddmTemplate = DDMTemplateLocalServiceUtil.fetchTemplate(
			groupId, classNameId, templateKey);

		Map<Locale, String> nameMap = new HashMap<Locale, String>();

		Locale locale = PortalUtil.getSiteDefaultLocale(groupId);

		nameMap.put(locale, LanguageUtil.get(locale, name));

		Map<Locale, String> descriptionMap = new HashMap<Locale, String>();

		descriptionMap.put(locale, LanguageUtil.get(locale, description));

		String script = ContentUtil.get(scriptFileName);
		
		if (ddmTemplate != null) {
			int hashCode = ddmTemplate.getScript().hashCode();
			if ( script.hashCode() != hashCode) {
				_log.info("Updating template "+templateKey+" for company "+ddmTemplate.getCompanyId()+" Global group "+ddmTemplate.getGroupId());
				// Do template update
				DDMTemplateLocalServiceUtil.updateTemplate(
						ddmTemplate.getTemplateId(),
						0, nameMap, descriptionMap, DDMTemplateConstants.TEMPLATE_TYPE_DISPLAY, "builtin", 
						language, script, cacheable, false, null, null, serviceContext);
			}
			return;
		}

		_log.info("Adding template "+templateKey+" for company "+serviceContext.getCompanyId()+" Global group "+groupId);
		DDMTemplateLocalServiceUtil.addTemplate(
			userId, groupId, classNameId, 0, templateKey, nameMap,
			descriptionMap, DDMTemplateConstants.TEMPLATE_TYPE_DISPLAY, "builtin",
			language, script, cacheable, false, null, null, serviceContext);
	}

	protected static void updateDDMTemplates(String className,
			long userId, long groupId, ServiceContext serviceContext)
		throws Exception {

//		String[] templateHandlerClassNames = getPluginRegisteredTemplateHandlerEntityClassNames();
//		if ( templateHandlerClassNames == null ) return;
		
//		for (String className : templateHandlerClassNames) {
			TemplateHandler templateHandler = TemplateHandlerRegistryUtil.getTemplateHandler(
					className);
			if ( templateHandler == null ) {
				_log.error("Template handler for Entity "+className+" template loading ignored because of the template handler can not be resolved.");
				return;
			}
			
			try {
				long classNameId = PortalUtil.getClassNameId(
					templateHandler.getClassName());

				List<Element> templateElements =
					templateHandler.getDefaultTemplateElements();

				for (Element templateElement : templateElements) {
					String templateKey = templateElement.elementText(
						"template-key");

					String name = templateElement.elementText("name");
					String description = templateElement.elementText("description");
					String language = templateElement.elementText("language");
					String scriptFileName = templateElement.elementText(
						"script-file");
					boolean cacheable = GetterUtil.getBoolean(
						templateElement.elementText("cacheable"));

					addOrUpdateDDMTemplate(
						userId, groupId, classNameId, templateKey, name,
						description, language, scriptFileName, cacheable,
						serviceContext);
				}
			} catch (Exception e) {
				_log.error("Failed to update Template for "+className, e);
			}
//		}
	}

	public static void doRun(long companyId, String className) throws Exception {
		ServiceContext serviceContext = new ServiceContext();

		Group group = GroupLocalServiceUtil.getCompanyGroup(companyId);

		serviceContext.setCompanyId(companyId);
		serviceContext.setScopeGroupId(group.getGroupId());

		long defaultUserId = UserLocalServiceUtil.getDefaultUserId(companyId);

		serviceContext.setUserId(defaultUserId);

		updateDDMTemplates(className, defaultUserId, group.getGroupId(), serviceContext);
	}

	public static void doRun(String className) throws SystemException {
		List<Company> companies = CompanyLocalServiceUtil.getCompanies();
		for ( Company company: companies) {
			try {
				_log.info("Verifying built in templates for "+className+" into company "+company.getWebId());
				doRun(company.getCompanyId(), className);
			} catch (Exception e) {
				_log.error("Failed loading built in templates for "+className+" into company "+company.getWebId(), e);
			}
		}
	}
}