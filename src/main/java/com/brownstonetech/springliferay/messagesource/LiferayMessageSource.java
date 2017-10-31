package com.brownstonetech.springliferay.messagesource;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.util.ObjectUtils;

import com.liferay.portal.kernel.language.LanguageUtil;

public class LiferayMessageSource implements MessageSource {

	private boolean alwaysUseMessageFormat = false;
	private boolean useCodeAsDefaultMessage = false;
	
	/**
	 * Set whether to always apply the MessageFormat rules, parsing even
	 * messages without arguments.
	 * <p>Default is "false": Messages without arguments are by default
	 * returned as-is, without parsing them through MessageFormat.
	 * Set this to "true" to enforce MessageFormat for all messages,
	 * expecting all message texts to be written with MessageFormat escaping.
	 * <p>For example, MessageFormat expects a single quote to be escaped
	 * as "''". If your message texts are all written with such escaping,
	 * even when not defining argument placeholders, you need to set this
	 * flag to "true". Else, only message texts with actual arguments
	 * are supposed to be written with MessageFormat escaping.
	 * 
	 * @param alwaysUseMessageFormat
	 * 
	 * @see java.text.MessageFormat
	 */
	public void setAlwaysUseMessageFormat(boolean alwaysUseMessageFormat) {
		this.alwaysUseMessageFormat = alwaysUseMessageFormat;
	}

	/**
	 * Return whether to always apply the MessageFormat rules, parsing even
	 * messages without arguments.
	 * 
	 * @return
	 */
	protected boolean isAlwaysUseMessageFormat() {
		return this.alwaysUseMessageFormat;
	}

	
	/**
	 * Set whether to use the message code as default message instead of
	 * throwing a NoSuchMessageException. Useful for development and debugging.
	 * Default is "false".
	 * <p>Note: In case of a MessageSourceResolvable with multiple codes
	 * (like a FieldError) and a MessageSource that has a parent MessageSource,
	 * do <i>not</i> activate "useCodeAsDefaultMessage" in the <i>parent</i>:
	 * Else, you'll get the first code returned as message by the parent,
	 * without attempts to check further codes.
	 * <p>To be able to work with "useCodeAsDefaultMessage" turned on in the parent,
	 * AbstractMessageSource and AbstractApplicationContext contain special checks
	 * to delegate to the internal {@link #getMessageInternal} method if available.
	 * In general, it is recommended to just use "useCodeAsDefaultMessage" during
	 * development and not rely on it in production in the first place, though.
	 * 
	 * @param useCodeAsDefaultMessage
	 * 
	 * @see #getMessage(String, Object[], Locale)
	 * @see org.springframework.validation.FieldError
	 */
	public void setUseCodeAsDefaultMessage(boolean useCodeAsDefaultMessage) {
		this.useCodeAsDefaultMessage = useCodeAsDefaultMessage;
	}

	/**
	 * Return whether to use the message code as default message instead of
	 * throwing a NoSuchMessageException. Useful for development and debugging.
	 * Default is "false".
	 * <p>Alternatively, consider overriding the {@link #getDefaultMessage}
	 * method to return a custom fallback message for an unresolvable code.
	 * 
	 * @return
	 * 
	 * @see #getDefaultMessage(String)
	 */
	protected boolean isUseCodeAsDefaultMessage() {
		return this.useCodeAsDefaultMessage;
	}

	/**
	 * @param code the code of the message to resolve
	 * @param locale the Locale to resolve the code for
	 * (subclasses are encouraged to support internationalization)
	 * @return the message String, or {@code null} if not found
	 * 
	 * @see java.text.MessageFormat
	 */
	protected String resolveCodeWithoutArguments(String code, Locale locale) {
		String ret = LanguageUtil.get(locale, code, null);
		return ret;
	}
	
	@Override
	public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
		if ( ! isMessageAvaliable(code, locale) ) {
			return defaultMessage;
		}
		return LanguageUtil.format(locale, code, args);
	}

	/**
	 * Resolve the given code and arguments as message in the given Locale,
	 * returning {@code null} if not found. Does <i>not</i> fall back to
	 * the code as default message. Invoked by {@code getMessage} methods.
	 * @param code the code to lookup up, such as 'calculator.noRateSet'
	 * @param args array of arguments that will be filled in for params
	 * within the message
	 * @param locale the Locale in which to do the lookup
	 * @return the resolved message, or {@code null} if not found
	 * @see #getMessage(String, Object[], String, Locale)
	 * @see #getMessage(String, Object[], Locale)
	 * @see #getMessage(MessageSourceResolvable, Locale)
	 * @see #setUseCodeAsDefaultMessage
	 */
	protected String getMessageInternal(String code, Object[] args, Locale locale) {
		if (code == null) {
			return null;
		}
		if (locale == null) {
			locale = Locale.getDefault();
		}
		Object[] argsToUse = args;

		if (!isAlwaysUseMessageFormat() && ObjectUtils.isEmpty(args)) {
			// Optimized resolution: no arguments to apply,
			// therefore no MessageFormat needs to be involved.
			// Note that the default implementation still uses MessageFormat;
			// this can be overridden in specific subclasses.
			String message = resolveCodeWithoutArguments(code, locale);
			if (message != null) {
				return message;
			}
		}

		else {
			// Resolve arguments eagerly, for the case where the message
			// is defined in a parent MessageSource but resolvable arguments
			// are defined in the child MessageSource.
			argsToUse = resolveArguments(args, locale);
			
			String ret = getMessage(code, argsToUse, null, locale);
			return ret;
		}
		
		return null;
	}

	/**
	 * Searches through the given array of objects, finds any MessageSourceResolvable
	 * objects and resolves them.
	 * <p>Allows for messages to have MessageSourceResolvables as arguments.
	 * @param args array of arguments for a message
	 * @param locale the locale to resolve through
	 * @return an array of arguments with any MessageSourceResolvables resolved
	 */
	protected Object[] resolveArguments(Object[] args, Locale locale) {
		if (args == null) {
			return new Object[0];
		}
		List<Object> resolvedArgs = new ArrayList<Object>(args.length);
		for (Object arg : args) {
			if (arg instanceof MessageSourceResolvable) {
				resolvedArgs.add(getMessage((MessageSourceResolvable) arg, locale));
			}
			else {
				resolvedArgs.add(arg);
			}
		}
		return resolvedArgs.toArray(new Object[resolvedArgs.size()]);
	}
	
	@Override
	public String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException {
		if ( ! isMessageAvaliable(code, locale) ) {
			throw new NoSuchMessageException(code, locale);
		}
		return LanguageUtil.format(locale, code, args);
	}

	protected boolean isMessageAvaliable(String code, Locale locale) {
		String res = resolveCodeWithoutArguments(code, locale);
		return res != null;
	}
	
	/**
	 * Render the given default message String. The default message is
	 * passed in as specified by the caller and can be rendered into
	 * a fully formatted default message shown to the user.
	 * <p>The default implementation passes the String to {@code formatMessage},
	 * resolving any argument placeholders found in them. Subclasses may override
	 * this method to plug in custom processing of default messages.
	 * @param defaultMessage the passed-in default message String
	 * @param args array of arguments that will be filled in for params within
	 * the message, or {@code null} if none.
	 * @param locale the Locale used for formatting
	 * @return the rendered default message (with resolved arguments)
	 * @see #formatMessage(String, Object[], java.util.Locale)
	 */
	protected String renderDefaultMessage(String defaultMessage, Object[] args, Locale locale) {
		return formatMessage(defaultMessage, args, locale);
	}
	
	/**
	 * Format the given message String, using cached MessageFormats.
	 * By default invoked for passed-in default messages, to resolve
	 * any argument placeholders found in them.
	 * @param msg the message to format
	 * @param args array of arguments that will be filled in for params within
	 * the message, or {@code null} if none
	 * @param locale the Locale used for formatting
	 * @return the formatted message (with resolved arguments)
	 */
	protected String formatMessage(String msg, Object[] args, Locale locale) {
		if (msg == null || (!this.alwaysUseMessageFormat && ObjectUtils.isEmpty(args))) {
			return msg;
		}
		String ret = LanguageUtil.format(locale, msg, resolveArguments(args, locale));
		return ret;
	}
	
	@Override
	public final String getMessage(MessageSourceResolvable resolvable, Locale locale)
			throws NoSuchMessageException {

		String[] codes = resolvable.getCodes();
		if (codes == null) {
			codes = new String[0];
		}
		for (String code : codes) {
			String msg = getMessageInternal(code, resolvable.getArguments(), locale);
			if (msg != null) {
				return msg;
			}
		}
		String defaultMessage = resolvable.getDefaultMessage();
		if (defaultMessage != null) {
			return renderDefaultMessage(defaultMessage, resolvable.getArguments(), locale);
		}
		if (codes.length > 0) {
			String fallback = getDefaultMessage(codes[0]);
			if (fallback != null) {
				return fallback;
			}
		}
		throw new NoSuchMessageException(codes.length > 0 ? codes[codes.length - 1] : null, locale);
	}

	/**
	 * Return a fallback default message for the given code, if any.
	 * <p>Default is to return the code itself if "useCodeAsDefaultMessage" is activated,
	 * or return no fallback else. In case of no fallback, the caller will usually
	 * receive a NoSuchMessageException from {@code getMessage}.
	 * @param code the message code that we couldn't resolve
	 * and that we didn't receive an explicit default message for
	 * @return the default message to use, or {@code null} if none
	 * @see #setUseCodeAsDefaultMessage
	 */
	protected String getDefaultMessage(String code) {
		if (isUseCodeAsDefaultMessage()) {
			return code;
		}
		return null;
	}
	
}
