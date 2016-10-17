<#ftl strip_whitespace=true>
<#assign portlet=JspTaglibs["/WEB-INF/tld/liferay-portlet.tld"] />

<#--
 * 
 * sessionErrors
 *
 -->
<#macro sessionErrors >
	<@portlet.defineObjects />
	<#local errorKeys=slputils.renderHelper.retrieveSessionErrors(renderRequest)!false />
	<#if errorKeys?is_enumerable >
		<#list errorKeys as errorKey>
			<@lui.error key=errorKey message=errorKey />
		</#list>
	</#if>
</#macro>

<#--
 * 
 * sessionMessages
 *
 -->
<#macro sessionMessages >
	<@portlet.defineObjects />
	<#local messageKeys=slputils.renderHelper.retrieveSessionMessages(renderRequest)!false />
	<#if messageKeys?is_enumerable >
		<#list messageKeys as messageKey>
			<@lui.success key=messageKey message=messageKey />
		</#list>
	</#if>
</#macro>

<#--
 * bind
 *
 * Exposes a BindStatus object for the given bind path, which can be
 * a bean (e.g. "person") to get global errors, or a bean property
 * (e.g. "person.name") to get field errors. Can be called multiple times
 * within a form to bind to multiple command objects and/or field names.
 *
 * This macro will participate in the default HTML escape setting for the given
 * RequestContext. This can be customized by calling "setDefaultHtmlEscape"
 * on the "springMacroRequestContext" context variable, or via the
 * "defaultHtmlEscape" context-param in web.xml (same as for the JSP bind tag).
 * Also regards a "htmlEscape" variable in the namespace of this library.
 *
 * Producing no output, the following context variable will be available
 * in the nested template of this macro (assuming you import this library in
 * your templates with the namespace 'spring'):
 *
 *   spring.status : a BindStatus instance holding the command object name,
 *   expression, value, and error messages and codes for the path supplied
 *
 * @param path : the path (string value) of the value required to bind to.
 *   Spring defaults to a command name of "command" but this can be overridden
 *   by user config.
 * @param htmlEscape : an optional HTML escape flag. If not specified with
 *   a boolean value, the macro will do HTML escape according to the default
 *   HTML escape setting.
 -->
<#macro bind path, htmlEscape="*", ignoreNestedPath=false >
	<#local escape=springMacroRequestContext.defaultHtmlEscape />
	<#if htmlEscape?is_boolean >
		<#local escape=htmlEscape />
	<#else>
	    <#if .namespace.htmlEscape?exists>
	    	<#local escape = .namespace.htmlEscape />
	    </#if>
    </#if>
    <#local resolvedPath = ignoreNestedPath?string(path,resolvePath(path)) />
    <#-- save original status -->
    <#local previousStatus = .namespace.status![] />
    <#assign status = springMacroRequestContext.getBindStatus(resolvedPath, escape)>
    <#-- assign a temporary value, forcing a string representation for any
    kind of variable. This temp value is only used in this macro lib -->
    <#assign stringStatusValue = asString((.namespace.status.value)!) />
    <#nested/>
    <#-- restore original status -->
	<#assign status = previousStatus />
</#macro>

<#function asString value >
	<#if !(value??) >
		<#local stringStatusValue="" />
	<#elseif value?is_string >
		<#local stringStatusValue=value />
	<#elseif value?is_boolean >
		<#local stringStatusValue=value?string />
	<#elseif value?is_number >
		<#local stringStatusValue=value?string />
	<#else>
		<#local stringStatusValue="" />
	</#if>
	<#return stringStatusValue />
</#function>
<#--
 * nestedPath
 *
 * Sets a nested path to be used by the nested bind macro’s path.
 *
 * @param path : the path (string value) of the value required to bind to.
 *
 -->
<#macro nestedPath path>
	<#-- save original nestedPathValue -->
	<#local previousNestedPath = .namespace.nestedPathValue!"" />
	<#if (path?has_content) >
		<#-- ensure path ends with "." -->
		<#local newPath = (path?ends_with("."))?string(path,path+".") />
		<#local newPath = previousNestedPath + newPath />
	<#else>
		<#-- cleanup any existing nestedPath for nested template -->
		<#local newPath = "" />
	</#if>
	<#assign nestedPathValue = newPath />
	<#nested/>
	<#-- recover original netestPath before exit -->
	<#assign nestedPathValue = previousNestedPath />
</#macro>

<#function resolvePath path >
	<#local resolvedPath = path!"" />
	<#local nestedPath = .namespace.nestedPathValue!"" />
	<#-- only prepend if not already an absolute path
		The logic is consistent with corresponding spring
		JSP tag -->
	<#if nestedPath?has_content
		&& !(resolvedPath?starts_with(nestedPath))
		&& !(nestedPath?starts_with(resolvedPath)) >
		<#local resolvedPath = nestedPath + resolvedPath />
	</#if>
	<#return resolvedPath/>
</#function>

<#macro showErrors path="", ignoreNestedPath=false >
	<@bind path=path ignoreNestedPath=ignoreNestedPath >
		<#if .namespace.status.error >
			<#list .namespace.status.errorMessages as messageKey >
				<div class="alert alert-error">
					<@lui.message key=messageKey />
				</div>
			</#list>
		</#if>
	</@bind>
</#macro>

<#include "sui/input.ftl" />
<#include "sui/select.ftl" />
<#include "sui/radiobuttons.ftl" />
<#include "sui/checkboxes.ftl" />
<#include "sui/datetimepicker.ftl" />
<#include "sui/datepicker.ftl" />
