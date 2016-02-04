<%@ tag pageEncoding="UTF-8" %>
<%@ tag description="This tag renders multiple HTML 'input' tags with type 'radio'" %>
<%@ tag dynamic-attributes="dynamicAttrMap" %>

<%@ attribute name="autoFocus" required="false" rtexprvalue="true" type="Boolean" 
	description="Sets whether the input component gets focus by default." %>
<%@ attribute name="autoSize" required="false" rtexprvalue="true" type="Boolean" 
	description="Sets whether the input component autosizes." %>
<%@ attribute name="bean" required="false" rtexprvalue="true" type="java.lang.Object" 
	description="Sets a bean to associate with the input component." %>
<%@ attribute name="changesContext" required="false" rtexprvalue="true" type="Boolean" 
	description="Sets whether to reload the page when the value of the input changes. The default value is &lt;code&gt;false&lt;/code&gt;." %>
<%@ attribute name="checked" required="false" rtexprvalue="true" type="Boolean" 
	description="Sets whether the input is selected. Note that this attribute is only enabled when &lt;code&gt;type&lt;/code&gt; is &lt;code&gt;radio&lt;/code&gt; or &lt;code&gt;checkbox&lt;/code&gt;." %>
<%@ attribute name="classPK" required="false" rtexprvalue="true" type="Long" 
	description="Sets the primary key for the instance of the class. The default value is &lt;code&gt;0&lt;/code&gt;." %>
<%@ attribute name="cssClass" required="false" rtexprvalue="true" type="java.lang.String" 
	description="Sets a CSS class for styling this component." %>
<%@ attribute name="data" required="false" rtexprvalue="true" type="java.lang.Object" 
	description="Takes a set of data and passes it as HTML data attributes. For example, the &lt;code&gt;HashMap&&lt;&quote;class-name&quote;, foo&&gt;&lt;/code&gt; would render as the attribute &lt;code&gt;data-class-name = foo;&lt;/code&gt;." %>
<%@ attribute name="dateTogglerCheckboxLabel" required="false" rtexprvalue="true" type="java.lang.String" 
	description="..." %>
<%@ attribute name="defaultLanguageId" required="false" rtexprvalue="true" type="java.lang.String" 
	description="Sets the default language ID for the component." %>
<%@ attribute name="disabled" required="false" rtexprvalue="true" type="Boolean" 
	description="Sets whether the component is disabled. The default value is &lt;code&gt;false&lt;/code&gt;." %>
<%@ attribute name="field" required="false" rtexprvalue="true" type="java.lang.String" 
	description="..." %>
<%@ attribute name="fieldParam" required="false" rtexprvalue="true" type="java.lang.String" 
	description="Sets a variable name to refer to the &lt;code&gt;field&lt;/code&gt; of the component." %>
<%@ attribute name="first" required="false" rtexprvalue="true" type="Boolean" 
	description="Sets whether the input component should be the first element of the form." %>
<%@ attribute name="formName" required="false" rtexprvalue="true" type="java.lang.String" 
	description="Sets the name of the component's form." %>
<%@ attribute name="helpMessage" required="false" rtexprvalue="true" type="java.lang.String" 
	description="Sets text to display as a help tooltip on mouse over of the component's help icon." %>
<%@ attribute name="helpTextCssClass" required="false" rtexprvalue="true" type="java.lang.String" 
	description="Sets a CSS class for styling the help message text. The default value is &lt;code&gt;input-group-addon&lt;/code&gt;." %>
<%@ attribute name="id" required="false" rtexprvalue="true" type="java.lang.String" 
	description="Identifies the component instance." %>
<%@ attribute name="ignoreRequestValue" required="false" rtexprvalue="true" type="Boolean" 
	description="Sets whether to ignore the value saved from the request object. The default value is &lt;code&gt;false&lt;/code&gt;." %>
<%@ attribute name="inlineField" required="false" rtexprvalue="true" type="Boolean" 
	description="Sets whether to align the input's field with the next element in the form. The default value is &lt;code&gt;false&lt;/code&gt;." %>
<%@ attribute name="inlineLabel" required="false" rtexprvalue="true" type="java.lang.String" 
	description="Aligns the input label's text. Possible values are &lt;code&gt;right&lt;/code&gt; and &lt;code&gt;left&lt;/code&gt;." %>
<%@ attribute name="items" required="true" rtexprvalue="true" type="java.util.Map" 
	description="Provides options as a Map, each elemement will be rendered as a input radio box in the radio button group, map keys are interpreted as radio button values and the map values correspond to radio button labels." %>
<%@ attribute name="label" required="false" rtexprvalue="true" type="java.lang.String" 
	description="Sets the label for the input component. If not assigned a value, the label is automatically taken from the &lt;code&gt;name&lt;/code&gt; attribute's value." %>
<%@ attribute name="languageId" required="false" rtexprvalue="true" type="java.lang.String" 
	description="..." %>
<%@ attribute name="last" required="false" rtexprvalue="true" type="Boolean" 
	description="Sets whether the component should be the last element of the form." %>
<%@ attribute name="localized" required="false" rtexprvalue="true" type="Boolean" 
	description="Sets whether to translate the component's text features into the user's language. The default value is &lt;code&gt;false&lt;/code&gt;." %>
<%@ attribute name="max" required="false" rtexprvalue="true" type="java.lang.Object" 
	description="Sets the maximum value for the input if its &lt;code&gt;type&lt;/code&gt; is &lt;code&gt;number&lt;/code&gt; or &lt;code&gt;range&lt;/code&gt;. Note that the max value is inclusive if the input's &lt;code&gt;type&lt;/code&gt; is &lt;code&gt;range&lt;/code&gt;." %>
<%@ attribute name="model" required="false" rtexprvalue="true" type="java.lang.Class" 
	description="Sets the class for the bean object." %>
<%@ attribute name="min" required="false" rtexprvalue="true" type="java.lang.Object" 
	description="Sets the minimum value for the input if its &lt;code&gt;type&lt;/code&gt; is &lt;code&gt;number&lt;/code&gt; or &lt;code&gt;range&lt;/code&gt;. Note that the min value is inclusive if the input's &lt;code&gt;type&lt;/code&gt; is &lt;code&gt;range&lt;/code&gt;." %>
<%@ attribute name="multiple" required="false" rtexprvalue="true" type="Boolean" 
	description="..." %>
<%@ attribute name="name" required="true" rtexprvalue="true" type="java.lang.String" 
	description="Sets the component's name." %>
<%@ attribute name="onChange" required="false" rtexprvalue="true" type="java.lang.String" 
	description="Sets a function to be called when the input's value changes." %>
<%@ attribute name="onClick" required="false" rtexprvalue="true" type="java.lang.String" 
	description="Sets a function to be called on a user clicking the input." %>
<%@ attribute name="placeholder" required="false" rtexprvalue="true" type="java.lang.String" 
	description="Sets placeholder text for the input's field." %>
<%@ attribute name="prefix" required="false" rtexprvalue="true" type="java.lang.String" 
	description="Sets text to display before the input." %>
<%@ attribute name="required" required="false" rtexprvalue="true" type="Boolean" 
	description="Sets whether to mark the input as required." %>
<%@ attribute name="resizable" required="false" rtexprvalue="true" type="Boolean" 
	description="Sets whether the input's field is resizeable, if the input is of type &lt;code&gt;textarea&lt;/code&gt;." %>
<%@ attribute name="showRequiredLabel" required="false" rtexprvalue="true" type="Boolean" 
	description="Sets whether to show the input's required label, if an input value is required." %>
<%@ attribute name="suffix" required="false" rtexprvalue="true" type="java.lang.String" 
	description="Sets text to display after the input." %>
<%@ attribute name="title" required="false" rtexprvalue="true" type="java.lang.String" 
	description="Sets the input's title." %>
<%@ attribute name="useNamespace" required="false" rtexprvalue="true" type="Boolean" 
	description="Sets whether to use the default portlet namespace, to avoid name conflicts. The default value is &lt;code&gt;true&lt;/code&gt;." %>
<%@ attribute name="value" required="false" rtexprvalue="true" type="java.lang.Object" 
	description="Sets the input's value." %>
<%@ attribute name="wrapperCssClass" required="false" rtexprvalue="true" type="java.lang.String" 
	description="Sets a CSS class for styling the &lt;code&gt;div&lt;/code&gt; that wraps the input component." %>
	
<%@ include file="../init.tagf" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>

<spring:bind path="${name}">
	<aui:field-wrapper 
		label="<%= label %>"
		inlineLabel="<%= inlineLabel %>"
		inlineField="<%= inlineField == null?false:inlineField %>" 
		cssClass="${status.error?'error':''} ${wrapperCssClass}"
		first="<%= first==null?false:first %>"
		last="<%= last==null?false:last %>"
		helpMessage="<%= helpMessage %>"
		required="<%= required == null? false: required %>"
		data="<%= data %>" >
			<c:forEach var="item" begin="0" items="${items}">
				<aui:input inlineLabel="right" inlineField="true" name="${status.expression}" type="radio"
					value="${item.key}" label="${item.value}" checked="${status.value == item.key}"
					disabled="<%= disabled == null?false:disabled %>"
					bean="<%= bean %>"
					changesContext="<%= changesContext == null?false:changesContext %>"
					classPK="<%= classPK == null?0:classPK %>"
					cssClass="${cssClass}"
					formName="<%= formName %>"
					model="<%= model %>"
					onChange="<%= onChange %>"
					onClick="<%= onClick %>"
					title="<%= title %>"
					useNamespace="<%= useNamespace==null?true:useNamespace %>"
					/>
			</c:forEach>
	</aui:field-wrapper>
</spring:bind>