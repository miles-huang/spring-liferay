package com.brownstonetech.springliferay.optional.freemarker;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

import com.brownstonetech.springliferay.component.PermissionCheckHelper;
import com.brownstonetech.springliferay.component.RenderHelper;
import com.brownstonetech.springliferay.component.ViewLogger;
import com.brownstonetech.springliferay.freemarker.ApplicationDisplayTemplateLoader;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil_IW;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.ParamUtil_IW;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.WebappTemplateLoader;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.jsp.TaglibFactory;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModel;
import freemarker.template.Version;

@Configuration
public class LiferaySpringFreemarkerConfigSupport {

//    @Bean
//    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
//        PropertySourcesPlaceholderConfigurer placeHolderConfigurer = new PropertySourcesPlaceholderConfigurer(); 
//        return placeHolderConfigurer;
//    }

    @Bean
	@Autowired
    public FreeMarkerConfigurer freemarkerConfig(ServletContext context,
    		PermissionCheckHelper permissionCheckHelper,
    		RenderHelper renderHelper,
    		ViewLogger viewLogger) {
    	FreeMarkerConfigurer configurer = new FreeMarkerConfigurer();
    	configurer.setPreTemplateLoaders(
    			new WebappTemplateLoader(context, "/WEB-INF/ftl"),
    			new ClassTemplateLoader(context.getClassLoader(), "/templates"),
    			new ApplicationDisplayTemplateLoader()
    	);
    	// The imcompatible_improvements setting affects JSP taglib on
    	// how the freemarker provided pageContext working with JSP tags.
    	// It must be set to a version higher than VERSION_2_3_22 since
    	// the freemarker default setting is always 2.3.0 even the actual
    	// implementation is higher version which can unwrapping the page
    	// scoped variables correctly.
    	// Compilation check for freemarker implementation version ensure
    	// 2.3.22 or higher.
    	@SuppressWarnings("unused")
		Version version = freemarker.template.Configuration.VERSION_2_3_22;
    	Properties settings = new Properties();
    	// Use the latest version as implementation.
    	settings.setProperty(
    			freemarker.template.Configuration.INCOMPATIBLE_IMPROVEMENTS_KEY,
    			freemarker.template.Configuration.getVersion().toString()
    			);
    	settings.setProperty(
    			freemarker.template.Configuration.SQL_DATE_AND_TIME_TIME_ZONE_KEY,
    			TimeZone.getDefault().getID());
    	configurer.setFreemarkerSettings(settings);

    	Map<String,Object> freemarkerObjects = new HashMap<String,Object>();
    	Map<String,Object> slputils = new HashMap<String,Object>();
    	freemarkerObjects.put("slputils", slputils);
    	slputils.put("staticModels", getStaticModels());
    	slputils.put("permissionCheckHelper", permissionCheckHelper);
    	slputils.put("renderHelper", renderHelper);
    	slputils.put("viewLogger", viewLogger);
    	slputils.put("defaultTimeZone", TimeZone.getDefault());
    	slputils.put("defaultLocale", Locale.getDefault());
    	freemarkerObjects.put("httpUtil", HttpUtil.getHttp());
    	freemarkerObjects.put("paramUtil", ParamUtil_IW.getInstance());
    	freemarkerObjects.put("getterUtil", GetterUtil_IW.getInstance());
    	freemarkerObjects.put("calendarFactory", CalendarFactoryUtil.getCalendarFactory());
    	configurer.setFreemarkerVariables(freemarkerObjects);
    	configurer.setDefaultEncoding("UTF-8");
    	configurer.setPreferFileSystemAccess(false); 
    	return configurer;
    }
    
	@Bean
    public FreeMarkerViewResolver viewResolver() {
         FreeMarkerViewResolver viewResolver = new FreeMarkerViewResolver(); 
         viewResolver.setExposeSpringMacroHelpers(true);  
         viewResolver.setExposeRequestAttributes(true);
         viewResolver.setCache(true);
         viewResolver.setPrefix("");
         viewResolver.setSuffix(".ftl");
//         viewResolver.setContentType("text/html;charset=UTF-8");
         // If you want to use security tags in freemarker the escense are this two lines:
//         viewResolver.setExposeSpringMacroHelpers(true);  
//         viewResolver.setExposeRequestAttributes(true);
         return viewResolver;
    }
    
	private TemplateHashModel getStaticModels() {
		TemplateHashModel staticModels;
		BeansWrapper wrapper = new BeansWrapper(freemarker.template.Configuration.getVersion());
		wrapper.setExposureLevel(BeansWrapper.EXPOSE_PROPERTIES_ONLY);
		staticModels = wrapper.getStaticModels();
		return staticModels;
	}
    
    @Bean
    @Autowired
    public TaglibFactory taglibFactory(FreeMarkerConfigurer freemarkerConfig) throws IOException, TemplateException {
    	// This is to avoid freemarker warning message of no ObjectWrapper
        TaglibFactory taglibFactory = freemarkerConfig.getTaglibFactory();
        taglibFactory.setObjectWrapper(
        		freemarker.template.Configuration.getDefaultObjectWrapper(
        				freemarker.template.Configuration.getVersion()));

        return taglibFactory;
    }	
}
