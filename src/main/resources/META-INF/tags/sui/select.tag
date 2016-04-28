<%@ tag pageEncoding="UTF-8" %>
<%@ tag description="Creates a form that offers additional styling and custom namespacing." %>
<%@ tag dynamic-attributes="dynamicAttrMap" %>

<%@ attribute name="bean" required="false" rtexprvalue="true" type="java.lang.Object" description="Sets the bean associated with the select component." %>
<%@ attribute name="noBinding" required="false" rtexprvalue="true" type="Boolean" description="Don't binding to an spring form property. The default value is &lt;code&gt;false&lt;/code&gt;." %>
<%@ attribute name="value" required="false" rtexprvalue="true" type="String" description="value" %>
<%@ attribute name="changesContext" required="false" rtexprvalue="true" type="Boolean" description="Sets whether to reload the page when the value of the field changes. The default value is &lt;code&gt;false&lt;/code&gt;." %>
<%@ attribute name="cssClass" required="false" rtexprvalue="true" type="java.lang.String" description="Sets a CSS class for styling this component." %>
<%@ attribute name="data" required="false" rtexprvalue="true" type="java.util.Map" 
	description="Takes a set of data and passes it as HTML data attributes. For example, the &lt;code&gt;HashMap&&lt;&quote;class-name&quote;, &quote;foo&quote;&&gt;&lt;/code&gt; would render as the attribute &lt;code&gt;data-class-name = &quote;foo&quote;;&lt;/code&gt;." %>
<%@ attribute name="disabled" required="false" rtexprvalue="true" type="Boolean" 
	description="Sets whether the component is disabled. The default value is &lt;code&gt;false&lt;/code&gt;." %>
<%@ attribute name="first" required="false" rtexprvalue="true" type="Boolean" 
	description="Sets whether the component should be the first element of the form. The default value is &lt;code&gt;false&lt;/code&gt;." %>
<%@ attribute name="helpMessage" required="false" rtexprvalue="true" type="java.lang.String" 
	description="Sets text to display as a help tooltip on mouse over of the component's help icon." %>
<%@ attribute name="id" required="false" rtexprvalue="true" type="java.lang.String" 
	description="Identifies the component instance." %>
<%@ attribute name="ignoreRequestValue" required="false" rtexprvalue="true" type="Boolean" 
	description="..." %>
<%@ attribute name="inlineField" required="false" rtexprvalue="true" type="Boolean" 
	description="Sets whether to align the select field with the next element in the form. The default value is &lt;code&gt;false&lt;/code&gt;." %>
<%@ attribute name="inlineLabel" required="false" rtexprvalue="true" type="java.lang.String" 
	description="Aligns the label's text. Possible values are &lt;code&gt;right&lt;/code&gt; and &lt;code&gt;left&lt;/code&gt;." %>
<%@ attribute name="label" required="false" rtexprvalue="true" type="java.lang.String" 
	description="Sets the select field's label and displays it above the field." %>
<%@ attribute name="last" required="false" rtexprvalue="true" type="Boolean" 
	description="Sets whether the component should be the last element in the form. The default value is &lt;code&gt;false&lt;/code&gt;." %>
<%@ attribute name="listType" required="false" rtexprvalue="true" type="java.lang.String" 
	description="Sets the list type and automatically generates the options. Possible values can be found in &lt;code&gt;ListTypeImpl.java&lt;/code&gt;." %>
<%@ attribute name="listTypeFieldName" required="false" rtexprvalue="true" type="java.lang.String" 
	description="..." %>
<%@ attribute name="multiple" required="false" rtexprvalue="true" type="Boolean" 
	description="Sets whether multiple options can be selected. If set to &lt;code&gt;true&lt;/code&gt;, the select field expands to allow for multiple selections. The default value is &lt;code&gt;false&lt;/code&gt;." %>
<%@ attribute name="name" required="true" rtexprvalue="true" type="java.lang.String" 
	description="Sets the select field's name attribute. Note that this value overrides the &lt;code&gt;label&lt;/code&gt; attribute and takes the place of the label." %>
<%@ attribute name="onChange" required="false" rtexprvalue="true" type="java.lang.String" 
	description="Sets a function to be called when the select field's value changes." %>
<%@ attribute name="onClick" required="false" rtexprvalue="true" type="java.lang.String" 
	description="Sets a function to be called on users clicking the select field." %>
<%@ attribute name="prefix" required="false" rtexprvalue="true" type="java.lang.String" 
	description="Sets the text to display before the select field." %>
<%@ attribute name="required" required="false" rtexprvalue="true" type="Boolean" 
	description="Sets whether to mark the select field as required. The default value is &lt;code&gt;false&lt;/code&gt;." %>
<%@ attribute name="showEmptyOption" required="false" rtexprvalue="true" type="Boolean" 
	description="Sets whether to display an empty option as a placeholder. The default value is &lt;code&gt;false&lt;/code&gt;." %>
<%@ attribute name="showRequiredLabel" required="false" rtexprvalue="true" type="Boolean" 
	description="Sets whether to display the required label above the select field. Note that this attribute is only active if the &lt;code&gt;required&lt;/code&gt; attribute is set to &lt;code&gt;true&lt;/code&gt;. The default value is &lt;code&gt;false&lt;/code&gt;." %>
<%@ attribute name="suffix" required="false" rtexprvalue="true" type="java.lang.String" 
	description="Sets the text to display after the select field." %>
<%@ attribute name="title" required="false" rtexprvalue="true" type="java.lang.String" 
	description="Sets the select field's title." %>
<%@ attribute name="useNamespace" required="false" rtexprvalue="true" type="Boolean" 
	description="Sets whether to use the default portlet namespace, to avoid name conflicts. The default value is &lt;code&gt;true&lt;/code&gt;." %>
<%@ attribute name="options" required="false" rtexprvalue="true" type="java.util.Map" 
	description="Provides options as a Map, each elemement will be rendered as a option in the select html control, map keys are interpreted as option values and the map values correspond to option labels." %>

<%@ include file="/META-INF/tags/init.tagf" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<%
String paramName = name;
noBinding = noBinding==null?false:noBinding;
if ( !noBinding) {
%>
<spring:bind path="<%= name %>">
<%
cssClass = (status.isError()?"error-field ":"") + cssClass;
paramName = status.getExpression();
if (value == null) {
	value = status.getDisplayValue();
}
%>
<c:set var="value" value="<%= value %>" />
</spring:bind>
<%
}
%>
	<aui:select
		bean="<%= bean %>"
		changesContext="<%= changesContext==null?false:changesContext %>"
		cssClass="<%= cssClass %>"
		data="<%= data %>"
		disabled="<%= disabled == null?false:disabled %>"
		first="<%= first==null?false:first %>"
		helpMessage="<%= helpMessage%>"
		id="<%= id %>"
		ignoreRequestValue="<%= ignoreRequestValue==null?false:ignoreRequestValue %>"
		inlineField="<%= inlineField==null?false:inlineField %>"
		inlineLabel="<%= inlineLabel %>"
		label="<%= label %>"
		last="<%= last == null? false: last %>"
		listType="<%= listType %>"
		listTypeFieldName="<%= listTypeFieldName %>"
		multiple="<%= multiple == null? false: multiple %>"
		name="<%= paramName %>"
 		onChange="<%= onChange %>"
		onClick="<%= onClick %>"
		prefix="<%= prefix %>"
		required="<%= required == null? false: required %>"
		showEmptyOption="<%= showEmptyOption == null? false: showEmptyOption %>"
		showRequiredLabel="<%= showRequiredLabel == null? true:showRequiredLabel %>"
		suffix="<%= suffix %>"
		title="<%= title %>"
		useNamespace="<%= useNamespace ==null?true:useNamespace %>"
	>
		<c:if test="<%= options != null %>">
			<c:forEach var="item" begin="0" items="${options}">
				<aui:option selected="${value == item.key}" value="${item.key}">${item.value}</aui:option>
			</c:forEach>
		</c:if>
		<jsp:doBody />
</aui:select>
