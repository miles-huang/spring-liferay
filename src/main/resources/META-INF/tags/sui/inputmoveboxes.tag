<%@ tag pageEncoding="UTF-8" %>
<%@ tag description="Creates a double selection box which allows user to pick and sort items." %>
<%@ tag dynamic-attributes="dynamicAttrMap" %>

<%@ attribute name="bean" required="false" rtexprvalue="true" type="java.lang.Object" description="Sets the bean associated with the select component." %>
<%@ attribute name="cssClass" required="false" rtexprvalue="true" type="java.lang.String" description="Sets a CSS class for styling this component." %>
<%@ attribute name="data" required="false" rtexprvalue="true" type="java.lang.Object" 
	description="Takes a set of data and passes it as HTML data attributes. For example, the &lt;code&gt;HashMap&&lt;&quote;class-name&quote;, foo&&gt;&lt;/code&gt; would render as the attribute &lt;code&gt;data-class-name = foo;&lt;/code&gt;." %>
<%@ attribute name="first" required="false" rtexprvalue="true" type="Boolean" 
	description="Sets whether the input component should be the first element of the form." %>
<%@ attribute name="helpMessage" required="false" rtexprvalue="true" type="java.lang.String" 
	description="Sets text to display as a help tooltip on mouse over of the component's help icon." %>
<%@ attribute name="ignoreRequestValue" required="false" rtexprvalue="true" type="Boolean" 
	description="..." %>
<%@ attribute name="inlineField" required="false" rtexprvalue="true" type="Boolean" 
	description="Sets whether to align the select field with the next element in the form. The default value is &lt;code&gt;false&lt;/code&gt;." %>
<%@ attribute name="inlineLabel" required="false" rtexprvalue="true" type="java.lang.String" 
	description="Aligns the label's text. Possible values are &lt;code&gt;right&lt;/code&gt; and &lt;code&gt;left&lt;/code&gt;." %>
<%@ attribute name="label" required="false" rtexprvalue="true" type="java.lang.String" 
	description="Sets the select field's label and displays it above the field." %>
<%@ attribute name="last" required="false" rtexprvalue="true" type="Boolean" 
	description="Sets whether the component should be the last element of the form." %>
<%@ attribute name="leftTitle" required="false" rtexprvalue="true" type="String" 
	description="Sets the title text key above left select box. Default is 'Selected'." %>
<%@ attribute name="name" required="true" rtexprvalue="true" type="java.lang.String" 
	description="Sets the select field's name attribute. Note that this value overrides the &lt;code&gt;label&lt;/code&gt; attribute and takes the place of the label." %>
<%@ attribute name="noBinding" required="false" rtexprvalue="true" type="Boolean" description="Don't binding to an spring form property. The default value is &lt;code&gt;false&lt;/code&gt;." %>
<%@ attribute name="options" required="true" rtexprvalue="true" type="java.util.Map" 
	description="Provides options as a Map, each elemement will be rendered as a option in the select html control, map keys are interpreted as option values and the map values correspond to option labels." %>
<%@ attribute name="required" required="false" rtexprvalue="true" type="Boolean" 
	description="Sets whether to mark the input as required." %>
<%@ attribute name="rightTitle" required="false" rtexprvalue="true" type="String" 
	description="Sets the title text key above right select box. Default is 'Available'." %>
<%@ attribute name="value" required="false" rtexprvalue="true" type="String" description="value" %>
<%@ attribute name="wrapperCssClass" required="false" rtexprvalue="true" type="java.lang.String" 
	description="Sets a CSS class for styling the &lt;code&gt;div&lt;/code&gt; that wraps the input component." %>


<%@ tag import="java.util.List" %>
<%@ tag import="com.liferay.portal.kernel.util.KeyValuePair" %>
<%@ tag import="com.liferay.portal.kernel.util.StringUtil" %>
<%@ tag import="com.brownstonetech.springliferay.util.KeyValuePairUtil" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%
	String paramName = name;
	boolean hasError = false;
	cssClass = (cssClass==null?"":cssClass);
	wrapperCssClass = (wrapperCssClass==null?"":wrapperCssClass);
	leftTitle = (leftTitle == null?"selected":leftTitle);
	rightTitle = (rightTitle == null?"available":rightTitle);
	if (noBinding ==null || !noBinding) {
%>
	<spring:bind path="<%= name %>">
<%
		cssClass = (status.isError()?"error-field ":"") + cssClass;
		wrapperCssClass = (status.isError()?"error ":"") + wrapperCssClass;
		paramName = status.getExpression();
		if (value == null) {
			value = status.getDisplayValue();
		}
%>
	</spring:bind>
<%
	}
	if ( ignoreRequestValue == null || !ignoreRequestValue ) {
		// TODO check request parameter
	}
	List<KeyValuePair> leftList = KeyValuePairUtil.getSelectedList(options, value);
	List<KeyValuePair> rightList = KeyValuePairUtil.getUnselectedList(options, value);
	String randomId = StringUtil.randomId();
%>

<aui:field-wrapper
		label="<%= label %>"
		inlineLabel="<%= inlineLabel %>"
		inlineField="<%= inlineField == null?false:inlineField %>" 
		cssClass="<%= wrapperCssClass %>"
		first="<%= first==null?false:first %>"
		last="<%= last==null?false:last %>"
		helpMessage="<%= helpMessage %>"
		required="<%= required == null? false: required %>"
		data="<%= data %>"
	>
	<aui:input name="<%= paramName %>" id="<%= randomId %>" type="hidden"
		value="<%= value %>">
		<aui:validator name="custom" errorMessage="">
				function (val, fieldNode, ruleValue) {
					var field = A.one('#<portlet:namespace />selected_<%= randomId %>');
					var hiddenField = A.one('#<portlet:namespace /><%= randomId %>');
					window.<portlet:namespace />inputMoveBoxesSaveSelection(field, hiddenField);
					return true;
				}
			</aui:validator>
	</aui:input>

	<liferay-ui:input-move-boxes 
		cssClass="<%= cssClass %>"
		leftBoxName='<%= "selected_"+randomId %>'
		leftList="<%= leftList %>"
		leftTitle="<%= leftTitle %>"
		rightBoxName='<%= "available_"+randomId %>'
		rightList="<%= rightList %>"
		rightTitle="<%= rightTitle %>"
		leftReorder="true" />

	<aui:script use="aui-base, liferay-util-list-fields">
			if ( !window.<portlet:namespace />inputMoveBoxesSaveSelection ) {
				Liferay.provide(
					window,
					'<portlet:namespace />inputMoveBoxesSaveSelection',
					function(field, hiddenField) {
						var newValue=Liferay.Util.listSelect(field);
						hiddenField.val(newValue);
					},
					['liferay-util-list-fields']
				);
			}
	</aui:script>
</aui:field-wrapper>
