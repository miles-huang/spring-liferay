package com.brownstonetech.springliferay.component;

import java.io.Serializable;

import org.springframework.stereotype.Component;

import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringBundler;

/**
 * TODO optimize performance by check log level before string handling
 * 
 * @author Miles Huang
 *
 */
@Component
public class ViewLogger implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String FATAL = "fatal";
	private static final String ERROR = "error";
	private static final String WARN = "warn";
	private static final String INFO = "info";
	private static final String DEBUG = "debug";
	
	private static Log _log = LogFactoryUtil.getLog(ViewLogger.class);

	public void log(String level, Object message, Object obj ) {
		if ( ! (message instanceof String) ) {
			message = JSONFactoryUtil.looseSerializeDeep(message);
		}
		if ( obj instanceof Throwable ) {
			Throwable exception = (Throwable)obj;
			if ( FATAL.equalsIgnoreCase(level) ) {
				_log.fatal(message, exception);
			} else if ( ERROR.equalsIgnoreCase(level) ) {
				_log.error(message, exception);
			} else if ( WARN.equalsIgnoreCase(level) ) {
				_log.warn(message, exception);
			} else if ( INFO.equalsIgnoreCase(level) ) {
				_log.info(message, exception);
			} else if ( DEBUG.equalsIgnoreCase(level) ) {
				_log.debug(message, exception);
			} else {
				_log.error(message, exception);
			}
		} else {
			String data = JSONFactoryUtil.looseSerializeDeep(obj);
			StringBundler sb = new StringBundler(4);
			sb.append(message).append(System.getProperty("line.separator"))
			.append(obj.getClass().getName()).append(data);
			log(level, sb.toString());
		}
	}
	
	public void log(String level, Object message) {
		if ( ! (message instanceof String) ) {
			message = JSONFactoryUtil.looseSerializeDeep(message);
		}
		if ( FATAL.equalsIgnoreCase(level) ) {
			_log.fatal(message);
		} else if ( ERROR.equalsIgnoreCase(level) ) {
			_log.error(message);
		} else if ( WARN.equalsIgnoreCase(level) ) {
			_log.warn(message);
		} else if ( INFO.equalsIgnoreCase(level) ) {
			_log.info(message);
		} else if ( DEBUG.equalsIgnoreCase(level) ) {
			_log.debug(message);
		} else {
			_log.info(message);
		}
	}
}
