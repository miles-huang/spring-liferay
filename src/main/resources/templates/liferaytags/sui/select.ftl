<#--
 * Render an aui:select component with spring form binding
-->
<#macro select
	name
	readOnly=false
	ignoreRequestValue=false
	noBinding=false
	optional...
	>
	
<#local bindingPath=name />
<#if noBinding >
	<#local bindingPath="*" />
</#if>
<@bind path=bindingPath >

	<#local suffix="" />
	<#local helpTextCssClass = "help-inline" />
	<#local wrapperCssClass = (optional.wrapperCssClass)!'' />
	<#local cssClass = (optional.cssClass)!'' />
	<#local fieldParam=name />
	<#local fieldValue="" />
	<#if !noBinding >
		<#local fieldParam = .namespace.status.expression />
		<#local fieldValue=(.namespace.stringStatusValue)! />
		<#if .namespace.status.error >
			<#local suffix = .namespace.status.getErrorMessagesAsString(r"<br/>") />
			<#local wrapperCssClass = wrapperCssClass + ' error' />
			<#local cssClass = cssClass + ' error-field' />
		</#if>
	</#if>
	<@aui.select
		bean=(optional.bean)!null
		changesContext=(optional.changesContext)!false
		cssClass=cssClass
		data=(optional.data)!null
		disabled=(optional.data)!false
		first=(optional.first)!false
		helpMessage=(optional.helpMessage)!null
		id=(optional.id)!null
		ignoreRequestValue=ignoreRequestValue
		inlineField=(optional.inlineField)!false
		inlineLabel=(optional.inlineLabel)!null
		label=(optional.label)!null
		last=(optional.last)!false
		listType=(optional.listType)!null
		listTypeFieldName=(optional.listTypeFieldName)!null
		multiple=(optional.multiple)!false
		name=fieldParam
 		onChange=(optional.onChange)!null
		onClick=(optional.onClick)!null
		prefix=(optional.prefix)!null
		required=(optional.required)!false
		showEmptyOption=(optional.showEmptyOption)!false
		showRequiredLabel=(optional.showRequiredLabel)!true
		suffix=suffix
		title=(optional.title)!null
		useNamespace=(optional.useNamespace)!true
	>
		<#local options = optional.options! />
		<#if options?is_hash >
			<#list options?keys as value >
				<#local label=options[value] />
				<@aui.option selected=(fieldValue == value) 
					value=value>${label?xhtml}</@aui.option>
			</#list>
		</#if>
		<#nested fieldValue, fieldParam />
	</@aui.select>
</@bind>
</#macro>

	