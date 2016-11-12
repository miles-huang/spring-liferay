<%@ tag pageEncoding="UTF-8" %>
<%@ tag description="Select DDM templates or launch new DDM template
manage application to create/edit existing templates" %>

<%@ attribute name="classNameId" required="true" rtexprvalue="true" type="Long" 
	description="classNameId" %>
<%@ attribute name="classPK" required="false" rtexprvalue="true" type="Long" 
	description="classPK" %>
<%@ attribute name="displayStyle" required="true" rtexprvalue="true" type="String" 
	description="display style" %>
<%@ attribute name="displayStyleGroupId" required="false" rtexprvalue="true" type="Long" 
	description="display style group Id" %>
<%@ attribute name="displayStyles" required="false" rtexprvalue="true" type="java.util.List" 
	description="pre-defined display styles" %>
<%@ attribute name="icon" required="false" rtexprvalue="true" type="String" 
	description="icon for the manage template link" %>
<%@ attribute name="label" required="false" rtexprvalue="true" type="String" 
	description="label for this template selection component" %>
<%@ attribute name="refreshURL" required="false" rtexprvalue="true" type="String" 
	description="the url for navigation back from manage template" %>
<%@ attribute name="showEmptyOption" required="false" rtexprvalue="true" type="Boolean" 
	description="Is show Default option in the template list for custom template not defined case" %>
<%@ attribute name="emptyOptionLabel" required="false" rtexprvalue="true" type="String" 
	description="Resource key for empty option label. Default is 'default'" %>	
<%@ attribute name="preferenceNamePrefix" required="false" rtexprvalue="true" type="String" 
	description="A prefix added to portlet preference name displayStyle and displayStyleGroupId. To support multiple ADT definition case.
	 If not provided there is no prefix added.
	 If provided for example 'view', the preference names would be viewDisplayStyle and viewDisplayStyleGroupId." %>
<%@ attribute name="showBuiltinTemplates" required="false" rtexprvalue="true" type="Boolean"
	description="Is include builtin templates in the list. Default is false" %>
<%@ attribute name="showCustomTemplates" required="false" rtexprvalue="true" type="Boolean"
	description="Is include custom templates in the list. Default is true" %>
<%@ attribute name="showTemplateViewLink" required="false" rtexprvalue="true" type="Boolean"
	description="Is include a link to show current configured template in a separated window/tab. Default is true" %>
<%@ attribute name="showTemplateUpdateLink" required="false" rtexprvalue="true" type="Boolean"
	description="Is include a link to update current configured template directly. Default is true" %>
<%@ attribute name="showTemplateCreateLink" required="false" rtexprvalue="true" type="Boolean"
	description="Is include a link to create new custom template. Default is true" %>
<%@ attribute name="referenceTemplateURI" required="false" rtexprvalue="true" type="String"
	description="If provided, will also include a link to show the template using the template key as reference template in a separatec window/tab" %>
<%@ attribute name="formName" required="false" rtexprvalue="true" type="String"
	description="The form to submit after some configuration changed. Default is 'fm'" %>
<%@ attribute name="preferenceName" required="false" rtexprvalue="true" type="String"
	description="The perference name (suffix) used for storing configured preference. Default is 'DisplayStyle'" %>
<%@ attribute name="manageLinkLabel" required="false" rtexprvalue="true" type="String"
	description="Label resource key for display ddmTempalte manage link. Default is 'manage-display-templates-for-x'" %>
<%@ attribute name="viewDefaultTemplateLabel" required="false" rtexprvalue="true" type="String"
	description="Label resource key for view default template link. Default is 'default'" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
 
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/ddm" prefix="liferay-ddm" %>
<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>
<%@ taglib uri="http://liferay.com/tld/security" prefix="liferay-security" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@ tag import="com.liferay.counter.service.CounterLocalServiceUtil" %><%@
tag import="com.liferay.portal.LocaleException" %><%@
tag import="com.liferay.portal.NoSuchLayoutException" %><%@
tag import="com.liferay.portal.NoSuchRoleException" %><%@
tag import="com.liferay.portal.NoSuchUserException" %><%@
tag import="com.liferay.portal.kernel.bean.BeanParamUtil" %><%@
tag import="com.liferay.portal.kernel.bean.BeanPropertiesUtil" %><%@
tag import="com.liferay.portal.kernel.cal.Recurrence" %><%@
tag import="com.liferay.portal.kernel.captcha.CaptchaMaxChallengesException" %><%@
tag import="com.liferay.portal.kernel.captcha.CaptchaTextException" %><%@
tag import="com.liferay.portal.kernel.configuration.Filter" %><%@
tag import="com.liferay.portal.kernel.exception.LocalizedException" %><%@
tag import="com.liferay.portal.kernel.exception.PortalException" %><%@
tag import="com.liferay.portal.kernel.exception.SystemException" %><%@
tag import="com.liferay.portal.kernel.json.JSONArray" %><%@
tag import="com.liferay.portal.kernel.json.JSONFactoryUtil" %><%@
tag import="com.liferay.portal.kernel.json.JSONObject" %><%@
tag import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
tag import="com.liferay.portal.kernel.language.LanguageWrapper" %><%@
tag import="com.liferay.portal.kernel.language.UnicodeLanguageUtil" %><%@
tag import="com.liferay.portal.kernel.log.Log" %><%@
tag import="com.liferay.portal.kernel.log.LogFactoryUtil" %><%@
tag import="com.liferay.portal.kernel.log.LogUtil" %><%@
tag import="com.liferay.portal.kernel.messaging.DestinationNames" %><%@
tag import="com.liferay.portal.kernel.plugin.PluginPackage" %><%@
tag import="com.liferay.portal.kernel.portlet.DynamicRenderRequest" %><%@
tag import="com.liferay.portal.kernel.portlet.LiferayPortletMode" %><%@
tag import="com.liferay.portal.kernel.portlet.LiferayPortletRequest" %><%@
tag import="com.liferay.portal.kernel.portlet.LiferayPortletResponse" %><%@
tag import="com.liferay.portal.kernel.portlet.LiferayPortletURL" %><%@
tag import="com.liferay.portal.kernel.portlet.LiferayWindowState" %><%@
tag import="com.liferay.portal.kernel.repository.model.FileEntry" %><%@
tag import="com.liferay.portal.kernel.repository.model.FileVersion" %><%@
tag import="com.liferay.portal.kernel.template.StringTemplateResource" %><%@
tag import="com.liferay.portal.kernel.template.TemplateHandler" %><%@
tag import="com.liferay.portal.kernel.template.TemplateHandlerRegistryUtil" %><%@
tag import="com.liferay.portal.kernel.upload.LiferayFileItemException" %><%@
tag import="com.liferay.portal.kernel.upload.UploadException" %><%@
tag import="com.liferay.portal.kernel.util.ArrayUtil" %><%@
tag import="com.liferay.portal.kernel.util.CalendarFactoryUtil" %><%@
tag import="com.liferay.portal.kernel.util.CalendarUtil" %><%@
tag import="com.liferay.portal.kernel.util.CharPool" %><%@
tag import="com.liferay.portal.kernel.util.Constants" %><%@
tag import="com.liferay.portal.kernel.util.ContentTypes" %><%@
tag import="com.liferay.portal.kernel.util.CookieKeys" %><%@
tag import="com.liferay.portal.kernel.util.DateUtil" %><%@
tag import="com.liferay.portal.kernel.util.FastDateFormatFactoryUtil" %><%@
tag import="com.liferay.portal.kernel.util.GetterUtil" %><%@
tag import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
tag import="com.liferay.portal.kernel.util.Http" %><%@
tag import="com.liferay.portal.kernel.util.HttpUtil" %><%@
tag import="com.liferay.portal.kernel.util.IntegerWrapper" %><%@
tag import="com.liferay.portal.kernel.util.JavaConstants" %><%@
tag import="com.liferay.portal.kernel.util.KeyValuePair" %><%@
tag import="com.liferay.portal.kernel.util.KeyValuePairComparator" %><%@
tag import="com.liferay.portal.kernel.util.ListUtil" %><%@
tag import="com.liferay.portal.kernel.util.LocaleUtil" %><%@
tag import="com.liferay.portal.kernel.util.LocalizationUtil" %><%@
tag import="com.liferay.portal.kernel.util.MapUtil" %><%@
tag import="com.liferay.portal.kernel.util.MathUtil" %><%@
tag import="com.liferay.portal.kernel.util.ObjectValuePair" %><%@
tag import="com.liferay.portal.kernel.util.OrderByComparator" %><%@
tag import="com.liferay.portal.kernel.util.OrderedProperties" %><%@
tag import="com.liferay.portal.kernel.util.ParamUtil" %><%@
tag import="com.liferay.portal.kernel.util.PrefsParamUtil" %><%@
tag import="com.liferay.portal.kernel.util.PropertiesParamUtil" %><%@
tag import="com.liferay.portal.kernel.util.PropertiesUtil" %><%@
tag import="com.liferay.portal.kernel.util.PropsKeys" %><%@
tag import="com.liferay.portal.kernel.util.ReleaseInfo" %><%@
tag import="com.liferay.portal.kernel.util.ResourceBundleUtil" %><%@
tag import="com.liferay.portal.kernel.util.ServerDetector" %><%@
tag import="com.liferay.portal.kernel.util.SetUtil" %><%@
tag import="com.liferay.portal.kernel.util.SortedArrayList" %><%@
tag import="com.liferay.portal.kernel.util.StringBundler" %><%@
tag import="com.liferay.portal.kernel.util.StringComparator" %><%@
tag import="com.liferay.portal.kernel.util.StringPool" %><%@
tag import="com.liferay.portal.kernel.util.StringUtil" %><%@
tag import="com.liferay.portal.kernel.util.TextFormatter" %><%@
tag import="com.liferay.portal.kernel.util.Time" %><%@
tag import="com.liferay.portal.kernel.util.TimeZoneUtil" %><%@
tag import="com.liferay.portal.kernel.util.Tuple" %><%@
tag import="com.liferay.portal.kernel.util.UnicodeFormatter" %><%@
tag import="com.liferay.portal.kernel.util.UnicodeProperties" %><%@
tag import="com.liferay.portal.kernel.util.UniqueList" %><%@
tag import="com.liferay.portal.kernel.util.Validator" %><%@
tag import="com.liferay.portal.model.*" %><%@
tag import="com.liferay.portal.model.impl.*" %><%@
tag import="com.liferay.portal.portletfilerepository.PortletFileRepositoryUtil" %><%@
tag import="com.liferay.portal.security.auth.AuthTokenUtil" %><%@
tag import="com.liferay.portal.security.auth.PrincipalException" %><%@
tag import="com.liferay.portal.security.permission.ActionKeys" %><%@
tag import="com.liferay.portal.security.permission.PermissionChecker" %><%@
tag import="com.liferay.portal.security.permission.ResourceActionsUtil" %><%@
tag import="com.liferay.portal.service.*" %><%@
tag import="com.liferay.portal.service.permission.GroupPermissionUtil" %><%@
tag import="com.liferay.portal.service.permission.LayoutPermissionUtil" %><%@
tag import="com.liferay.portal.service.permission.LayoutPrototypePermissionUtil" %><%@
tag import="com.liferay.portal.service.permission.LayoutSetPrototypePermissionUtil" %><%@
tag import="com.liferay.portal.service.permission.PortalPermissionUtil" %><%@
tag import="com.liferay.portal.service.permission.PortletPermissionUtil" %><%@
tag import="com.liferay.portal.service.permission.RolePermissionUtil" %><%@
tag import="com.liferay.portal.theme.ThemeDisplay" %><%@
tag import="com.liferay.portal.util.Portal" %><%@
tag import="com.liferay.portal.util.PortalUtil" %><%@
tag import="com.liferay.portal.util.PortletCategoryKeys" %><%@
tag import="com.liferay.portal.util.PortletKeys" %><%@
tag import="com.liferay.portal.util.SessionClicks" %><%@
tag import="com.liferay.portal.util.comparator.PortletCategoryComparator" %><%@
tag import="com.liferay.portal.util.comparator.PortletTitleComparator" %><%@
tag import="com.liferay.portal.webserver.WebServerServletTokenUtil" %><%@
tag import="com.liferay.portlet.InvokerPortlet" %><%@
tag import="com.liferay.portlet.PortalPreferences" %><%@
tag import="com.liferay.portlet.PortletConfigFactoryUtil" %><%@
tag import="com.liferay.portlet.PortletInstanceFactoryUtil" %><%@
tag import="com.liferay.portlet.PortletPreferencesFactoryUtil" %><%@
tag import="com.liferay.portlet.PortletSetupUtil" %><%@
tag import="com.liferay.portlet.PortletURLFactoryUtil" %><%@
tag import="com.liferay.portlet.PortletURLUtil" %><%@
tag import="com.liferay.portlet.dynamicdatamapping.NoSuchStructureException" %><%@
tag import="com.liferay.portlet.dynamicdatamapping.model.DDMStructure" %><%@
tag import="com.liferay.portlet.dynamicdatamapping.model.DDMTemplate" %><%@
tag import="com.liferay.portlet.dynamicdatamapping.service.DDMStructureLocalServiceUtil" %><%@
tag import="com.liferay.portlet.dynamicdatamapping.service.DDMTemplateLocalServiceUtil" %><%@
tag import="com.liferay.portlet.expando.model.ExpandoBridge" %><%@
tag import="com.liferay.portlet.journal.model.JournalArticle" %><%@
tag import="com.liferay.portlet.journal.model.JournalArticleConstants" %><%@
tag import="com.liferay.portlet.journal.model.JournalArticleDisplay" %><%@
tag import="com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil" %><%@
tag import="com.liferay.portlet.journal.service.JournalArticleServiceUtil" %><%@
tag import="com.liferay.portlet.journalcontent.util.JournalContentUtil" %><%@
tag import="com.liferay.portlet.messageboards.model.MBMessage" %><%@
tag import="com.liferay.portlet.messageboards.service.MBMessageLocalServiceUtil" %><%@
tag import="com.liferay.portlet.portletconfiguration.util.PortletConfigurationUtil" %><%@
tag import="com.liferay.portlet.portletdisplaytemplate.util.PortletDisplayTemplateUtil" %><%@
tag import="com.liferay.portlet.rolesadmin.util.RolesAdminUtil" %><%@
tag import="com.liferay.portlet.sites.util.Sites" %><%@
tag import="com.liferay.portlet.sites.util.SitesUtil" %><%@
tag import="com.liferay.portlet.trash.model.TrashEntry" %><%@
tag import="com.liferay.portlet.trash.util.TrashUtil" %><%@
tag import="com.liferay.portlet.usersadmin.util.UsersAdminUtil" %><%@
tag import="com.liferay.taglib.util.OutputTag" %><%@
tag import="com.liferay.util.ContentUtil" %><%@
tag import="com.liferay.util.CreditCard" %><%@
tag import="com.liferay.util.Encryptor" %><%@
tag import="com.liferay.util.JS" %><%@
tag import="com.liferay.util.PKParser" %><%@
tag import="com.liferay.util.PwdGenerator" %><%@
tag import="com.liferay.util.State" %><%@
tag import="com.liferay.util.StateUtil" %><%@
tag import="com.liferay.util.log4j.Levels" %><%@
tag import="com.liferay.util.portlet.PortletRequestUtil" %><%@
tag import="com.liferay.util.xml.XMLFormatter" %>

<%@ tag import="java.io.Serializable" %>

<%@ tag import="java.text.DateFormat" %><%@
tag import="java.text.DecimalFormat" %><%@
tag import="java.text.Format" %><%@
tag import="java.text.NumberFormat" %><%@
tag import="java.text.SimpleDateFormat" %>

<%@ tag import="java.util.ArrayList" %><%@
tag import="java.util.Arrays" %><%@
tag import="java.util.Calendar" %><%@
tag import="java.util.Collection" %><%@
tag import="java.util.Collections" %><%@
tag import="java.util.Currency" %><%@
tag import="java.util.Date" %><%@
tag import="java.util.Enumeration" %><%@
tag import="java.util.HashMap" %><%@
tag import="java.util.HashSet" %><%@
tag import="java.util.Iterator" %><%@
tag import="java.util.LinkedHashMap" %><%@
tag import="java.util.LinkedHashSet" %><%@
tag import="java.util.List" %><%@
tag import="java.util.Locale" %><%@
tag import="java.util.Map" %><%@
tag import="java.util.Properties" %><%@
tag import="java.util.ResourceBundle" %><%@
tag import="java.util.Set" %><%@
tag import="java.util.Stack" %><%@
tag import="java.util.TimeZone" %><%@
tag import="java.util.TreeMap" %><%@
tag import="java.util.TreeSet" %>


<%@ tag import="javax.portlet.MimeResponse" %><%@
tag import="javax.portlet.PortletConfig" %><%@
tag import="javax.portlet.PortletContext" %><%@
tag import="javax.portlet.PortletException" %><%@
tag import="javax.portlet.PortletMode" %><%@
tag import="javax.portlet.PortletPreferences" %><%@
tag import="javax.portlet.PortletRequest" %><%@
tag import="javax.portlet.PortletResponse" %><%@
tag import="javax.portlet.PortletURL" %><%@
tag import="javax.portlet.ResourceURL" %><%@
tag import="javax.portlet.UnavailableException" %><%@
tag import="javax.portlet.ValidatorException" %><%@
tag import="javax.portlet.WindowState" %>

<%@ tag import="com.liferay.portal.kernel.util.DateFormatFactoryUtil" %>
<%@ tag import="com.liferay.portal.kernel.json.JSONFactoryUtil" %>
<%@ tag import="com.liferay.portal.kernel.util.GetterUtil" %>
<%@ tag import="com.liferay.portal.kernel.util.StringPool" %>
<%@ tag import="com.liferay.portal.kernel.util.StringUtil" %>
<%@ tag import="com.liferay.portal.kernel.util.Validator" %>

<%@ tag import="com.liferay.taglib.aui.AUIUtil" %>
<%@ tag import="com.liferay.taglib.util.InlineUtil" %>

<%@ tag import="org.springframework.web.servlet.support.BindStatus" %>

<%@ tag import="com.liferay.portlet.dynamicdatamapping.model.DDMTemplateConstants" %>
<%@ tag import="com.liferay.portlet.portletdisplaytemplate.util.PortletDisplayTemplate" %>
<%@ tag import="com.liferay.portlet.portletdisplaytemplate.util.PortletDisplayTemplateUtil" %>
<%@ tag import="com.brownstonetech.springliferay.permission.DDMTemplatePermission" %>
<%@ tag import="com.brownstonetech.springliferay.permission.DDMPermission" %>
<%@ tag import="com.brownstonetech.springliferay.freemarker.ADTTemplateLoaderUtil" %>

<liferay-theme:defineObjects />
<%
if ( preferenceNamePrefix == null ) {
	preferenceNamePrefix = StringPool.BLANK;
}

if ( preferenceName == null ) {
	preferenceName = "displayStyle";
}
String preferenceNameUc = StringUtil.upperCaseFirstLetter(preferenceName);

String displayStyleGroupIdName = preferenceName+"GroupId";
if ( Validator.isNotNull(preferenceNamePrefix)) {
	displayStyleGroupIdName = preferenceNamePrefix+preferenceNameUc+"GroupId";
}

String displayStyleName = preferenceName;
if ( Validator.isNotNull(preferenceNamePrefix)) {
	displayStyleName = preferenceNamePrefix+preferenceNameUc;
}
if ( Validator.isNull(icon) ) icon = "configuration";
if ( Validator.isNull(label)) label ="display-template";
List<String> displayStylesList = (List<String>)displayStyles;
if ( null == classPK ) classPK=0L;
if ( null == displayStyleGroupId ) displayStyleGroupId = 0L;
if ( null == showEmptyOption ) showEmptyOption = false;
if ( null == showBuiltinTemplates ) showBuiltinTemplates = false;
if ( null == showCustomTemplates ) showCustomTemplates = true;
if ( null == showTemplateViewLink ) showTemplateViewLink = true;
if ( null == showTemplateCreateLink ) showTemplateCreateLink = true;
if ( null == showTemplateUpdateLink ) showTemplateUpdateLink = true;
if ( Validator.isNull(formName)) formName = "fm";
if ( null == manageLinkLabel) manageLinkLabel = "manage-display-templates-for-x";
if ( null == emptyOptionLabel ) emptyOptionLabel = "default";
if ( null == viewDefaultTemplateLabel ) viewDefaultTemplateLabel = "default";

long ddmTemplateGroupId = PortletDisplayTemplateUtil.getDDMTemplateGroupId(themeDisplay.getScopeGroupId());

Group ddmTemplateGroup = GroupLocalServiceUtil.getGroup(ddmTemplateGroupId);

DDMTemplate ddmTemplate = null;
%>

<aui:input id="<%= displayStyleGroupIdName %>" name='<%= "preferences--"+displayStyleGroupIdName+"--" %>'
	type="hidden" value="<%= String.valueOf(displayStyleGroupId) %>" />

<aui:select id="<%= displayStyleName %>" inlineField="<%= true %>" label="<%= label %>" name='<%="preferences--"+displayStyleName+"--" %>' >
	<c:if test="<%= showEmptyOption %>">
		<aui:option label="<%= emptyOptionLabel %>" selected="<%= Validator.isNull(displayStyle) %>" />
	</c:if>

	<c:if test="<%= (displayStyles != null) && !displayStyles.isEmpty() %>">
		<optgroup label="<liferay-ui:message key="default" />">

			<%
			for (String curDisplayStyle : displayStylesList) {
			%>

				<aui:option label="<%= HtmlUtil.escape(curDisplayStyle) %>" selected="<%= displayStyle.equals(curDisplayStyle) %>" />

			<%
			}
			%>

		</optgroup>
	</c:if>

	<%
	Map<String,Object> data = new HashMap<String,Object>();

	if (displayStyle.startsWith(PortletDisplayTemplate.DISPLAY_STYLE_PREFIX)) {
		ddmTemplate = PortletDisplayTemplateUtil.fetchDDMTemplate(displayStyleGroupId, displayStyle);
	}

	List<DDMTemplate> companyPortletDDMTemplates = DDMTemplateLocalServiceUtil.getTemplates(themeDisplay.getCompanyGroupId(), classNameId, classPK);
	companyPortletDDMTemplates = filterTemplates(permissionChecker, companyPortletDDMTemplates,
			showBuiltinTemplates, showCustomTemplates);
	%>

	<c:if test="<%= (companyPortletDDMTemplates != null) && !companyPortletDDMTemplates.isEmpty() %>">
		<optgroup label="<liferay-ui:message key="global" />">

			<%
			data.put("displaystylegroupid", themeDisplay.getCompanyGroupId());

			for (DDMTemplate companyPortletDDMTemplate : companyPortletDDMTemplates) {
			%>

				<aui:option data="<%= data %>" label="<%= HtmlUtil.escape(companyPortletDDMTemplate.getName(locale)) %>" 
					selected="<%= (ddmTemplate != null) && (companyPortletDDMTemplate.getTemplateId() == ddmTemplate.getTemplateId()) %>"
					value="<%= PortletDisplayTemplate.DISPLAY_STYLE_PREFIX + companyPortletDDMTemplate.getUuid() %>" />

			<%
			}
			%>

		</optgroup>
	</c:if>

	<%
	List<DDMTemplate> groupPortletDDMTemplates = null;

	if (ddmTemplateGroupId != themeDisplay.getCompanyGroupId()) {
		groupPortletDDMTemplates = DDMTemplateLocalServiceUtil.getTemplates(ddmTemplateGroupId, classNameId, classPK);

		data.put("displaystylegroupid", ddmTemplateGroupId);
	}
	groupPortletDDMTemplates = filterTemplates(permissionChecker, groupPortletDDMTemplates, showBuiltinTemplates, showCustomTemplates);
	
	%>

	<c:if test="<%= (groupPortletDDMTemplates != null) && !groupPortletDDMTemplates.isEmpty() %>">
		<optgroup label="<%= HtmlUtil.escape(ddmTemplateGroup.getDescriptiveName(locale)) %>">

		<%
		for (DDMTemplate groupPortletDDMTemplate : groupPortletDDMTemplates) {
		%>

			<aui:option data="<%= data %>" 
				label="<%= HtmlUtil.escape(groupPortletDDMTemplate.getName(locale)) %>"
				selected="<%= (ddmTemplate != null) && (groupPortletDDMTemplate.getTemplateId() == ddmTemplate.getTemplateId()) %>"
				value="<%= PortletDisplayTemplate.DISPLAY_STYLE_PREFIX + groupPortletDDMTemplate.getUuid() %>" />

		<%
		}
		%>

		</optgroup>
	</c:if>
</aui:select>

<liferay-portlet:renderURL plid="<%= themeDisplay.getPlid() %>" portletName="<%= PortletKeys.DYNAMIC_DATA_MAPPING %>" var="basePortletURL">
	<portlet:param name="showHeader" value="<%= Boolean.FALSE.toString() %>" />
	<portlet:param name="type" value='<%= DDMTemplateConstants.TEMPLATE_TYPE_DISPLAY %>' />
</liferay-portlet:renderURL>

<%
if ( showTemplateViewLink && ddmTemplate != null) {
	String url = getTemplateViewURL(themeDisplay, ddmTemplate.getTemplateId());
	if ( url != null ) {
%>
<liferay-ui:icon
	id='<%= "viewCustomTemplate"+preferenceNamePrefix %>'
	iconCssClass="icon-eye-open"
	label="<%= true %>"
	message='view-source'
	url="<%= url %>"
	target="_blank"
/>
<%
	}
}
%>

<%
if ( showTemplateUpdateLink && ddmTemplate != null 
	&& hasUpdateTemplatePermission(permissionChecker, ddmTemplate.getTemplateId())) {
%>

<liferay-ui:icon
	id='<%= "updateDDMTemplate"+preferenceNamePrefix %>'
	iconCssClass="icon-edit"
	label="<%= true %>"
	message="edit"
	url="javascript:;"
/>

<aui:script use="aui-base">
	var updateDDMTemplate = A.one('#<portlet:namespace />updateDDMTemplate<%= preferenceNamePrefix %>');

	if (updateDDMTemplate) {
		var windowId = A.guid();

		updateDDMTemplate.on(
			'click',
			function(event) {
				Liferay.Util.openDDMPortlet(
					{
						basePortletURL: '<%= basePortletURL %>',
						classNameId: '<%= classNameId %>',
						classPK: '<%= classPK %>',
						dialog: {
							width: 1024
						},
						groupId: <%= ddmTemplateGroupId %>,
						templateId: <%= ddmTemplate.getTemplateId() %>,
						refererPortletName: '<%= PortletKeys.PORTLET_DISPLAY_TEMPLATES %>',
						struts_action: '/dynamic_data_mapping/edit_template',
						title: '<%= UnicodeLanguageUtil.get(locale, label) %>'
					},
					function(event) {
						if (!event.newVal) {
							submitForm(document.<portlet:namespace /><%= formName %>, '<%= refreshURL %>');
						}
					}
				);
			}
		);
	}

</aui:script>
<%
}
%>

<%
if ( showTemplateCreateLink 
	&& hasAddTemplatePermission(permissionChecker, ddmTemplateGroupId, classNameId)) {
%>
<liferay-ui:icon
	id='<%= "createDDMTemplate"+preferenceNamePrefix %>'
	iconCssClass="icon-plus"
	label="<%= true %>"
	message="add"
	url="javascript:;"
/>
<aui:script use="aui-base">
	var createDDMTemplate = A.one('#<portlet:namespace />createDDMTemplate<%= preferenceNamePrefix %>');

	if (createDDMTemplate) {
		var windowId = A.guid();

		createDDMTemplate.on(
			'click',
			function(event) {
				Liferay.Util.openDDMPortlet(
					{
						basePortletURL: '<%= basePortletURL %>',
						classNameId: '<%= classNameId %>',
						classPK: '<%= classPK %>',
						dialog: {
							width: 1024
						},
						showBackURL: 'false',
						groupId: <%= ddmTemplateGroupId %>,
						refererPortletName: '<%= PortletKeys.PORTLET_DISPLAY_TEMPLATES %>',
						struts_action: '/dynamic_data_mapping/edit_template',
						title: '<%= UnicodeLanguageUtil.get(locale, label) %>'
					},
					function(event) {
						if (!event.newVal) {
							submitForm(document.<portlet:namespace /><%= formName %>, '<%= refreshURL %>');
						}
					}
				);
			}
		);
	}

</aui:script>
<%
}
%>

<liferay-ui:icon
	id='<%= "selectDDMTemplate"+preferenceNamePrefix %>'
	iconCssClass="icon-cog"
	label="<%= true %>"
	message='<%= LanguageUtil.format(locale, manageLinkLabel, HtmlUtil.escape(ddmTemplateGroup.getDescriptiveName(locale)), false) %>'
	url="javascript:;"
/>

<%
if (Validator.isNotNull(referenceTemplateURI)) {
	long templateId = ADTTemplateLoaderUtil.getTemplateId(referenceTemplateURI);
	String url = getTemplateViewURL(themeDisplay, templateId);
	if ( url != null ) {
%>
<liferay-ui:icon
	id='<%= "viewBaseTemplate"+preferenceNamePrefix %>'
	iconCssClass="icon-book"
	label="<%= true %>"
	message='<%= viewDefaultTemplateLabel %>'
	url="<%= url %>"
	target="_blank"
/>
<%
	}
}
%>

<aui:script use="aui-base">
	var selectDDMTemplate = A.one('#<portlet:namespace />selectDDMTemplate<%= preferenceNamePrefix %>');

	if (selectDDMTemplate) {
		var windowId = A.guid();

		selectDDMTemplate.on(
			'click',
			function(event) {
				Liferay.Util.openDDMPortlet(
					{
						basePortletURL: '<%= basePortletURL %>',
						classNameId: '<%= classNameId %>',
						classPK: '<%= classPK %>',
						dialog: {
							width: 1024
						},
						groupId: <%= ddmTemplateGroupId %>,
						refererPortletName: '<%= PortletKeys.PORTLET_DISPLAY_TEMPLATES %>',
						struts_action: '/dynamic_data_mapping/view_template',
						title: '<%= UnicodeLanguageUtil.get(locale, label) %>'
					},
					function(event) {
						if (!event.newVal) {
							submitForm(document.<portlet:namespace />fm, '<%= refreshURL %>');
						}
					}
				);
			}
		);
	}

	var displayStyleGroupIdInput = A.one('#<portlet:namespace /><%= displayStyleGroupIdName %>');

	var displayStyleSelect = A.one('#<portlet:namespace /><%= displayStyleName %>');

	displayStyleSelect.on(
		'change',
		function(event) {
			var selectedIndex = event.currentTarget.get('selectedIndex');

			if (selectedIndex >= 0) {
				var selectedOption = event.currentTarget.get('options').item(selectedIndex);

				var displayStyleGroupId = selectedOption.attr('data-displaystylegroupid');

				if (displayStyleGroupId) {
					displayStyleGroupIdInput.set('value', displayStyleGroupId);
				}
			}
		}
	);
</aui:script>

<%!
private static Log _log = LogFactoryUtil.getLog("com.brownstonetech.jsp.LiferaySpring");

private static String getTemplateViewURL(ThemeDisplay themeDisplay, long templateId) {
	PermissionChecker permissionChecker = themeDisplay.getPermissionChecker();
	if ( templateId == 0 || !hasViewTemplatePermission(permissionChecker, templateId) ) {
		return null;
	}
	return themeDisplay.getPortalURL() + themeDisplay.getPathMain() + "/dynamic_data_mapping/get_template?templateId=" + templateId;
}

private static boolean hasViewTemplatePermission(PermissionChecker permissionChecker, long templateId) {
	try {
		return DDMTemplatePermission.contains(permissionChecker, templateId, 
				PortletKeys.PORTLET_DISPLAY_TEMPLATES, ActionKeys.VIEW);
	} catch (Exception e) {
		_log.error("Check DDM template permission failed",e);
	}
	return false;
}

private static boolean hasUpdateTemplatePermission(PermissionChecker permissionChecker, long templateId) {
	try {
		return DDMTemplatePermission.contains(permissionChecker, templateId, 
				PortletKeys.PORTLET_DISPLAY_TEMPLATES, ActionKeys.UPDATE);
	} catch (Exception e) {
		_log.error("Check DDM template permission failed",e);
	}
	return false;
}

private static boolean hasAddTemplatePermission(PermissionChecker permissionChecker, long scopeGroupId,
		long classNameId) {
	TemplateHandler templateHandler = TemplateHandlerRegistryUtil.getTemplateHandler(classNameId);
	if ( templateHandler == null ) return false;
	return DDMPermission.contains(permissionChecker, scopeGroupId, templateHandler.getResourceName(), ActionKeys.ADD_PORTLET_DISPLAY_TEMPLATE);
}

private static List<DDMTemplate> filterTemplates(PermissionChecker permissionChecker,
		List<DDMTemplate> src, 
		boolean showBuiltinTemplates, boolean showCustomTemplates) {
	if ( src == null ) return src;
	List<DDMTemplate> ret = new ArrayList<DDMTemplate>(src.size());
	for ( DDMTemplate template: src ) {
		if (!DDMTemplatePermission.contains(permissionChecker, template, 
				PortletKeys.PORTLET_DISPLAY_TEMPLATES, ActionKeys.VIEW)) {
			continue;
		}
		boolean builtinTemplate = "builtin".equals(template.getMode());
		if ( builtinTemplate && !showBuiltinTemplates) {
			continue;
		}
		if ( !builtinTemplate && !showCustomTemplates ) {
			continue;
		}
		ret.add(template);
	}
	return ret;
}
%>