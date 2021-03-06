package com.brownstonetech.springliferay.util.scripting;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

import javax.servlet.ServletContext;

import com.brownstonetech.springliferay.util.SimpleCache;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.AggregateClassLoader;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;

public class ScriptBeanFactory {

	private static final String GROOVY_CLASSLOADER_KEY="bstech.groovy.classloader";
	private static final String GROOVY_CLASS_CACHE_KEY="betech.scripting.classcache";
	private static Class<?> groovyObjectClass;

	private static ClassLoader createGroovyClassLoader() throws SystemException {
		try {
			ClassLoader portalClassLoader = PortalClassLoaderUtil.getClassLoader();
			ClassLoader portletClassLoader = Thread.currentThread().getContextClassLoader();
			Class<?> groovyClassLoaderClass = portalClassLoader.loadClass("groovy.lang.GroovyClassLoader");
			groovyObjectClass = portalClassLoader.loadClass("groovy.lang.GroovyObject");
			ClassLoader groovyClassLoader = (ClassLoader)groovyClassLoaderClass.getConstructor(ClassLoader.class)
					.newInstance(AggregateClassLoader.getAggregateClassLoader(new ClassLoader[]{portletClassLoader, portalClassLoader}));
			return groovyClassLoader;
		} catch (Exception e) {
			throw new SystemException("Failed to create GroovyClassLoader for the portletApp");
		}
	}
	
	public static CachedCompiledClass getCompiledClass(ServletContext servletContext, String classNameFQDN) {
		Map<String, CachedCompiledClass> scriptClassCache = getScriptClassCache(servletContext);
		CachedCompiledClass compiledClass = scriptClassCache.get(classNameFQDN);
		return compiledClass;
	}

	public static void expire(ServletContext servletContext, String classNameFQDN) {
		Map<String, CachedCompiledClass> scriptClassCache = getScriptClassCache(servletContext);
		scriptClassCache.remove(classNameFQDN);
	}
	
	private static synchronized Map<String, CachedCompiledClass> getScriptClassCache(
			ServletContext servletContext) {
		@SuppressWarnings("unchecked")
		Map<String, CachedCompiledClass> scriptClassCache = 
			(Map<String,CachedCompiledClass>)servletContext.getAttribute(GROOVY_CLASS_CACHE_KEY);
		if ( scriptClassCache == null ) {
			scriptClassCache = new SimpleCache<String, CachedCompiledClass>(100);
			scriptClassCache = Collections.synchronizedMap(scriptClassCache);
			servletContext.setAttribute(GROOVY_CLASS_CACHE_KEY, scriptClassCache);
		}
		return scriptClassCache;
	}
	
	private static synchronized ClassLoader getGroovyClassLoader(ServletContext servletContext) throws SystemException {
		ClassLoader gl = (ClassLoader)servletContext.getAttribute(GROOVY_CLASSLOADER_KEY);
		if ( gl == null ) {
			gl = createGroovyClassLoader();
			servletContext.setAttribute(GROOVY_CLASSLOADER_KEY, gl);
		}
		return gl;
	}
	
	public static Class<?> compileClass(ServletContext servletContext, String sourceHash, long lastUpdateTime,
			String script, String classFQDN) throws SystemException {
		Map<String,CachedCompiledClass> scriptClassCache = getScriptClassCache(servletContext);
		ClassLoader gcl = getGroovyClassLoader(servletContext);
		try {
			// gcl must be GroovyClassLoader
			// Call gcl.parseClass<String> to parse the groovy script into
			// class
			Method method = gcl.getClass().getMethod("parseClass", String.class);
			Class<?> scriptClass = (Class<?>)method.invoke(gcl, script);
			scriptClassCache.put(classFQDN, new CachedCompiledClass(sourceHash, lastUpdateTime, scriptClass));
			return scriptClass;
		} catch (InvocationTargetException e) {
			throw new SystemException(e.getCause());
		} catch (RuntimeException e) {
			throw e; 
		} catch (Exception e) {
			throw new SystemException(e);
		}
	}
	
	public static boolean isGroovyObject(Object object) {
		return groovyObjectClass.isAssignableFrom(object.getClass());
	}

}
