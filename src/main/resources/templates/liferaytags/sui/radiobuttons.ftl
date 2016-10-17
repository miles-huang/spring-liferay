<#--
 * Render an aui:select component with spring form binding
-->
<#macro radioButtons
	name
	options
	readOnly=false
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
	
	<@aui.field_wrapper 
		label=(optional.label)!null
		inlineLabel=(optional.inlineLabel)!null
		inlineField=(optional.inlineField)!false
		cssClass=wrapperCssClass
		first=(optional.first)!false
		last=(optional.last)!false
		helpMessage=(optional.helpMessage)!null
		required=(optional.required)!false
		data=(optional.data)!null >
		
		<#if options!?is_hash >
			<#list options?keys as value >
				<#local label=options[value] />
				<@aui.input inlineLabel="right" inlineField=true
					type="radio"
					name=fieldParam
					value=value
					label=label
					checked=(fieldValue == value) 
					disabled=(optional.disabled)!false
					bean=(optional.bean)!null
					changesContext=(optional.changesContext)!false
					classPK=(optional.classPK)!0
					cssClass=cssClass
					formName=(optional.formName)!null
					model=(optional.model)!null
					onChange=(optional.onChange)!null
					onClick=(optional.onClick)!null
					title=(optional.title)!null
					useNamespace=(optional.useNamespace)!true
					/>
			</#list>
		</#if>
		<#nested fieldValue, fieldParam />
	</@aui.field_wrapper>
</@bind>
</#macro>

	