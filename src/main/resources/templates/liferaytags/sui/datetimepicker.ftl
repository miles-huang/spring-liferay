<#--
Render an aui:input component with spring form binding
Parameters:
autoFocus: Sets whether the input component gets focus by default.
	Type: Boolean
	Optional: Yes
	Default: false
autoSize: Sets whether the input component autosizes.
	Type: Boolean
	Optional: Yes
	Default: false
bean: Sets a bean to associate with the input component.
	Type: Object
	Optional: Yes
	Default: none
changesContext: Sets whether to reload the page when the value of the input changes. 
	Type: Boolean
	Optional: Yes
	Default: false
checked: Sets whether the input is selected. 
	Note that this attribute is only enabled when type is radio or checkbox.
	Type: Boolean
	Optional: Yes
	Default: false
classPK: Sets the primary key for the instance of the class. The default value is 0.
	Type: Long
	Optional: Yes
	Default: 0
cssClass: " required="false" rtexprvalue="true" type="java.lang.String" 
	description="Sets a CSS class for styling this component." %>

-->
<#macro datetimepicker
	name
	showTime=false
	readOnly=false
	ignoreRequestValue=false
	noBinding=false
	optional...
	>
<#local Calendar=slu.importConstants("java.util.Calendar") />	
<#local WebKeys=slu.importConstants("com.liferay.portal.kernel.util.WebKeys") />
<#local TextFormatter=slu.importConstants("com.liferay.portal.kernel.util.TextFormatter") />

<#local themeDisplay=Request[WebKeys.THEME_DISPLAY] />
<#local timeZone=themeDisplay.timeZone />
<#local locale=themeDisplay.locale />
<#local bindingPath=name />
<#if noBinding >
	<#local bindingPath="*" />
</#if>
<@bind path=bindingPath >

<#local suffix="" />
<#local helpTextCssClass = "help-inline" />
<#local wrapperCssClass = (optional.wrapperCssClass)!'' />
<#local cssClass = (optional.cssClass)!'' />
<#local fieldParam = name />
<#local fieldValue = "" />
<#if !noBinding >
	<#local fieldParam = .namespace.status.expression />

	<#if .namespace.status.error >
		<#local suffix = .namespace.status.getErrorMessagesAsString(r"<br/>") />
		<#local wrapperCssClass = wrapperCssClass + ' error' />
		<#local cssClass = cssClass + ' error-field' />
	</#if>
	<#local fieldValue=(.namespace.status.value)! />
</#if>
<#if optional.value?? >
	<#local fieldValue=optional.value />
</#if>

<#if readOnly >
	<#local valueText="" />
	<#if fieldValue?is_date_like >
		<#if showTime >
			<#local valueText=fieldValue?datetime_if_unknown?string.medium_short />
		<#else>
			<#local valueText=fieldValue?date_if_unknown?string.medium />
		</#if>
	</#if>
	<@aui.input type="text" disabled=true readonly="readonly"
		first=(optional.first)!false
		helpMessage=(optional.helpMessage)!null
		inlineField=(optional.inlineField)!false
		inlineLabel=(optional.inlineLabel)!null
		label=(optional.label)!null
		last=(optional.last)!false
		required=(optional.required)!false
		cssClass=cssClass
		wrapperCssClass=wrapperCssClass
		suffix=suffix
		name=fieldParam
		value=valueText
	/>
<#else>
	<#local cssClass = cssClass+ ' input-small' />
	<#local formName=optional.formName!"" />
	<#local amPm=Calendar.AM />
	<#local cal=calendarFactory.getCalendar(timeZone, locale) />
	<#if fieldValue?is_date >
		<#local x=cal.setTime(fieldValue) />
	</#if>
	<#local year=cal.get(Calendar.YEAR) />
	<#local month=cal.get(Calendar.MONTH) />
	<#local day=cal.get(Calendar.DATE) />
	<#local hour=cal.get(Calendar.HOUR_OF_DAY) />
	<#local minute=cal.get(Calendar.MINUTE) />
	<#local amPm=cal.get(Calendar.AM_PM) />
	<#local firstDayOfWeek=cal.firstDayOfWeek - 1 />
	
	<#if !ignoreRequestValue >
		<#local year=paramUtil.getInteger(renderRequest, fieldParam + "Year", year) />
		<#local month=paramUtil.getInteger(renderRequest, fieldParam + "Month", month) />
		<#local day=paramUtil.getInteger(renderRequest, fieldParam + "Day", day) />
		<#local amPm=paramUtil.getInteger(renderRequest, fieldParam + "AmPm", amPm) />
		<#local hour=paramUtil.getInteger(renderRequest, fieldParam + "Hour", hour) />
		<#local minute=paramUtil.getInteger(renderRequest, fieldParam + "Minute", minute) />
	</#if>

	<@aui.field_wrapper cssClass=wrapperCssClass
		data=(optional.data)!null
		first=(optional.first)!false
		helpMessage=(optional.helpMessage)!null
		inlineField=(optional.inlineField)!false
		inlineLabel=(optional.inlineLabel)!null
		label=(optional.label)!null
		last=(optional.last)!false
		required=(optional.required)!false >

				<@lui.input_date
					autoFocus=(optional.autoFocus)!false
					cssClass=cssClass
					dayParam="${fieldParam}Day"
					dayValue=day
					disabled=(optional.disabled)!false
					firstDayOfWeek=firstDayOfWeek
					formName=(optional.formName)!null
					monthParam="${fieldParam}Month"
					monthValue=month
					name=fieldParam
					yearParam="${fieldParam}Year"
					yearValue=year
				/>

				<#if showTime >
					<@lui.input_time
						amPmParam="${fieldParam}AmPm"
						amPmValue=amPm
						cssClass=cssClass
						disabled=(optional.disabled)!false
						hourParam="${fieldParam}Hour"
						hourValue=hour
						minuteParam="${fieldParam}Minute"
						minuteValue=minute
						name="${fieldParam}Time"
					/>
				</#if>
<#-- 
			<#if (optional.dateTogglerCheckboxLabel)!?has_content >
				<#local dateTogglerCheckboxLabel = optional.dateTogglerCheckboxLabel />
				<#local dateTogglerCheckboxName = slputils.renderHelper.TextFormatterFormat(dateTogglerCheckboxLabel, TextFormatter.M) />

				<div class="clearfix">
					<@aui.input id="${formName}${fieldParam}" label=dateTogglerCheckboxLabel name=dateTogglerCheckboxName
						 type="checkbox" value=(optional.disabled)!false />
				</div>

				<@aui.script use="aui-base">
					var checkbox = A.one('#${portletNS}${formName}${fieldParam}Checkbox');

					checkbox.once(
						['click', 'mouseover'],
						function() {
							Liferay.component('${portletNS}${fieldParam}DatePicker');
						}
					);

					checkbox.on(
						['click', 'mouseover'],
						function(event) {
							var checked = document.getElementById('${portletNS}${formName}${fieldParam}Checkbox').checked;

							document.${portletNS}${formName!}["${portletNS}${fieldParam}"].disabled = checked;
							document.${portletNS}${formName!}["${portletNS}${fieldParam}Month"].disabled = checked;
							document.${portletNS}${formName!}["${portletNS}${fieldParam}Day"].disabled = checked;
							document.${portletNS}${formName!}["${portletNS}${fieldParam}Year"].disabled = checked;

							<#if showTime >
								document.${portletNS}${formName!}["${portletNS}${fieldParam}Time"].disabled = checked;
								document.${portletNS}${formName!}["${portletNS}${fieldParam}Hour"].disabled = checked;
								document.${portletNS}${formName!}["${portletNS}${fieldParam}Minute"].disabled = checked;
								document.${portletNS}${formName!}["${portletNS}${fieldParam}AmPm"].disabled = checked;
							</#if>
						}
					);
				</@aui.script>
			</#if>
-->
	</@aui.field_wrapper>
</#if>
</@bind>
</#macro>

	