<%@ tag pageEncoding="UTF-8" %>
<%@ tag description="Check Liferay permission and set result to a boolean variable
in the specified scope.
It can be combined with outer permissionchecker tag to get default resourceName,
scopeGroupId, ownerId and resourcePK value from it." %>

<%@ attribute name="actionId" required="true" rtexprvalue="true" type="String" 
	description="The action ID for permission checking." %>
	
<%@ attribute name="ownerId" required="false" rtexprvalue="true" type="Long" 
	description="The primary key of the resource's owner. 
		If not specified, the owner permission check will be bypass." %>
	
<%@ attribute name="resourceName" required="false" rtexprvalue="true" type="String" 
	description="The checking resource's name registed in Liferay permission system,
		which can be either an entity class name or a portlet ID. 
		The resourceName must be specified either by this tag or by outer permissioncontext tag." %>
	
<%@ attribute name="resourcePK" required="false" rtexprvalue="true" type="Long" 
	description="The primary key of checking resource. 
		The resourcePK must be specified either by this tag or by outer permissioncontext tag." %>
		
<%@ attribute name="scopeGroupId" required="false" rtexprvalue="true" type="Long" 
	description="The scopeGroupId of the checking resource. Default value is the 
		scope group of current rendering page." %>
	
<%@ attribute name="scope" required="false" rtexprvalue="true" type="String" 
	description="The scope in which to assign the variable. Can be &lt;b&gt;application&lt;/b&gt;
	, &lt;b&gt;session&lt;/b&gt;, &lt;b&gt;request&lt;/b&gt;, or &lt;b&gt;page&lt;/b&gt;." %>

<%@ attribute name="var" required="false" rtexprvalue="true" type="String" 
	description="The variable name of the check result." %>
	
<%@ tag import="com.liferay.portal.theme.ThemeDisplay" %>
<%@ tag import="com.liferay.portal.kernel.util.Validator" %>
<%@ tag import="com.liferay.portal.kernel.util.WebKeys" %>
<%@ tag import="com.brownstonetech.springliferay.component.PermissionCheckHelper" %>
<%@ tag import="com.brownstonetech.springliferay.component.PermissionContext" %>

<%
PermissionContext pc = (PermissionContext)jspContext.findAttribute("permissionContext");
ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(WebKeys.THEME_DISPLAY);
boolean result = permissionCheckHelper.hasPermission(themeDisplay,
		pc, actionId,
		scopeGroupId, resourceName, resourcePK, ownerId);
if ( Validator.isNotNull(var) ) {
	if ( "application".equalsIgnoreCase(scope) ) {
		application.setAttribute(var, result);
	} else if ( "session".equalsIgnoreCase(scope) ) {
		session.setAttribute(var, result);
	} else if ( "request".equalsIgnoreCase(scope) ) {
		request.setAttribute(var, result);
	} else {
		jspContext.setAttribute(var, result);
	}
}
if ( result ) {
%>
	<jsp:doBody />
<%
}
%>
<%!
	private static PermissionCheckHelper permissionCheckHelper = new PermissionCheckHelper();