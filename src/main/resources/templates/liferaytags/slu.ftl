<#ftl strip_whitespace=true>

<#--
 * Render the nested template if passed permission check.
 *
-->
<#macro ifPermitted actionId optional... >
	<#local ret = slputils.permissionCheckHelper.hasPermission(themeDisplay,
			(.namespace.permissionContextValue[0])!null,
			actionId,
			optional.scopeGroupId,
			optional.resourceName,
			optional.resourcePK,
			optional.ownerId) />
	<#if ret >
		<#nested />
	</#if>
</#macro>

<#--
 * Check permission and return check result as boolean.
 * Parameters(Required):
 * actionId
 * Parameters(Optional):
 * scopeGroupId
 * resourceName
 * resourcePK
 * ownerId
-->
<#function hasPermission actionId, optional... >
	<#local ret = slputils.permissionCheckHelper.hasPermission(themeDisplay,
			(.namespace.permissionContextValue[0])!null,
			actionId,
			(optional[0])!null,
			(optional[1])!null,
			(optional[2])!null,
			(optional[3])!null
		) />
	<#return ret />
</#function>

<#--
 * Setup an permission check context for the nesting template
 * In the nested templats, call to hasPermission function or
 * ifPermitted directive can use the permission check context to
 * provide default values.
 *
-->
<#macro permissionContext scopeGroupId=0,
			resourceName="",
			resourcePK=0,
			ownerId=0 >
	<#local prevPermissionContext = (.namespace.permissionContextValue)![] />
	<#assign permissionContextValue=[slputils.permissionCheckHelper.getPermissionCheckerContext(
			(prevPermissionContext[0])!null,
			scopeGroupId,
			resourceName,
			resourcePK,
			ownerId)] />
	<#nested />
	<#assign permissionContextValue=prevPermissionContext />
</#macro>

<#-- 
 * Convert any object to its string representation
 * using the object's toString method.
 * obj : the object to convert.
-->
<#function toString obj >
	<#return slputils.renderHelper.toString(obj) />
</#function>

<#-- 
 * Convert any object to JSON string representation
 * obj : the object to convert.
-->
<#function toJSONString obj >
	<#return slputils.renderHelper.toJSONString(obj) />
</#function>

<#-- 
 * Import constants defined in a Class/Interface
 * obj : the FQDN of the class or interface which defines the constants.
 * return
 *   A hash for all constants definition in the Class/Interface
 *   The hash key and value reflects constants name and value definition.
-->
<#function importConstants className >
	<#return slputils.staticModels[className] />
</#function>

<#-- 
<#function null>
</#function>
-->

<#--
Write log to viewLogger.
level: Log level for the log message.
	Optional. Can be debug, info, warn, error. Default value is info
message: Message text that will be written to log.
	Optional. Default would be the exception's message if exception is provided. Otherwise
	a message of "Auto generated view template log" will be used.
exception: Exception for produce stack trace into the log.
	Optional. No default. If exception is not provided, the log will not contain
	any stacktrace.
-->
<#macro log level="info" optional... >
	<#local hasException = optional?is_hash_ex && optional.exception?? />
	<#local hasData = optional?is_hash_ex && optional.data?? />
	<#local hasMessage = optional?is_hash_ex && optional.message?? />
	<#if hasMessage >
		<#local message = optional.message />
	<#else>
		<#local message = "Auto generated view template log" />
	</#if>
	<#if hasException >
		<#local data = optional.exception />
	<#elseif hasData >
		<#local data = optional.data />
	</#if>
	<#if hasException || hasData >
  		<#local x=slputils.viewLogger.log(level, message, data) >
	<#else>
		<#local x=slputils.viewLogger.log(level, message) >
	</#if>
</#macro>

<#macro initSettings >
	<#global WebKeys=importConstants("com.liferay.portal.kernel.util.WebKeys") />
	<#global themeDisplay=Request[WebKeys.THEME_DISPLAY] />
	<#global timeZone=themeDisplay.timeZone />
	<#global locale=themeDisplay.locale />
	<#setting time_zone=timeZone.getID() />
	<#setting locale=locale.toString() />
</#macro>