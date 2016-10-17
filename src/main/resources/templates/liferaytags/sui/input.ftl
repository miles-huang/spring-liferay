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
<#macro input 
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
		<#if .namespace.status.error >
			<#local suffix = .namespace.status.getErrorMessagesAsString(r"<br/>") />
			<#local wrapperCssClass = wrapperCssClass + ' error' />
			<#local cssClass = cssClass + ' error-field' />
		</#if>
		<#local fieldValue=(.namespace.stringStatusValue)! />
	</#if>
	<#if optional.value?? >
		<#local fieldValue=optional.value />
	</#if>
	<#if readOnly >
			<@aui.input
				wrapperCssClass=wrapperCssClass
				cssClass=cssClass
				name=fieldParam
				value=fieldValue
				autoFocus=(optional.autoFocus)!false
				autoSize=(optional.autoSize)!false
				bean=(optional.bean)!null
				changesContext=(optional.changesContext)!false
				checked=(optional.checked)!false
				classPK=(optional.classPK)!0
				data=(optional.data)!null
				dateTogglerCheckboxLabel=(optional.dateTogglerCheckboxLabel)!null
				defaultLanguageId=(optional.defaultLanguageId)!null
				disabled=(optional.disabled)!false
				field=(optional.field)!null
				fieldParam=(optional.fieldParam)!null
				first=(optional.first)!false
				formName=(optional.formName)!null
				helpMessage=(optional.helpMessage)!null
				helpTextCssClass=helpTextCssClass
				id=(optional.id)!null
				ignoreRequestValue=ignoreRequestValue
				inlineField=(optional.inlineField)!false
				inlineLabel=(optional.inlineLabel)!null
				label=(optional.label)!null
				languageId=(optional.languageId)!null
				last=(optional.last)!false
				localized=(optional.localized)!false
				max=(optional.max)!null
				min=(optional.min)!null
				model=(optional.model)!null
				multiple=(optional.multiple)!false
				onChange=(optional.onChange)!null
				onClick=(optional.onClick)!null
				placeholder=(optional.placeholder)!null
				prefix=(optional.prefix)!null
				required=(optional.required)!false
				resizable=(optional.resizable)!false
				showRequiredLabel=(optional.showRequiredLabel)!true
				suffix=suffix
				title=(optional.title)!null
				type=(optional.type)!null
				useNamespace=(optional.useNamespace)!true
				readonly="readonly">
				<#nested fieldValue, fieldParam />
			</@aui.input>
	<#else>
			<@aui.input
				wrapperCssClass=wrapperCssClass
				cssClass=cssClass
				name=fieldParam
				value=fieldValue
				autoFocus=(optional.autoFocus)!false
				autoSize=(optional.autoSize)!false
				bean=(optional.bean)!null
				changesContext=(optional.changesContext)!false
				checked=(optional.checked)!false
				classPK=(optional.classPK)!0
				data=(optional.data)!null
				dateTogglerCheckboxLabel=(optional.dateTogglerCheckboxLabel)!null
				defaultLanguageId=(optional.defaultLanguageId)!""
				disabled=(optional.disabled)!false
				field=(optional.field)!null
				fieldParam=(optional.fieldParam)!null
				first=(optional.first)!false
				formName=(optional.formName)!null
				helpMessage=(optional.helpMessage)!null
				helpTextCssClass=helpTextCssClass
				id=(optional.id)!null
				ignoreRequestValue=ignoreRequestValue
				inlineField=(optional.inlineField)!false
				inlineLabel=(optional.inlineLabel)!null
				label=(optional.label)!null
				languageId=(optional.languageId)!null
				last=(optional.last)!false
				localized=(optional.localized)!false
				max=(optional.max)!null
				min=(optional.min)!null
				model=(optional.model)!null
				multiple=(optional.multiple)!false
				onChange=(optional.onChange)!null
				onClick=(optional.onClick)!null
				placeholder=(optional.placeholder)!null
				prefix=(optional.prefix)!null
				required=(optional.required)!false
				resizable=(optional.resizable)!false
				showRequiredLabel=(optional.showRequiredLabel)!true
				suffix=suffix
				title=(optional.title)!null
				type=(optional.type)!null
				useNamespace=(optional.useNamespace)!true
				>
				<#nested fieldValue, fieldParam />
			</@aui.input>
	</#if>
</@bind>
</#macro>

	