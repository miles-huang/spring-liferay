<%@ tag pageEncoding="UTF-8" %>
<%@ tag description="Logging something into the log." %>

<%@ attribute name="message" required="false" rtexprvalue="true" type="String" 
	description="Log message." %>

<%@ attribute name="exception" required="false" rtexprvalue="true" type="java.lang.Throwable" 
	description="Optional exception." %>

<%@ tag import="com.liferay.portal.kernel.log.Log" %>
<%@ tag import="com.liferay.portal.kernel.log.LogFactoryUtil" %>
<%@ tag import="com.liferay.portal.kernel.util.Validator" %>

<%
	if (Validator.isNull(message)) {
		message="Auto generated view Log message";
	}
	if ( exception==null) {
		log(message);
	} else {
		log(message, exception);
	}
%>

<%!

static void log(String message, Throwable e) {
	_log.error(message, e);
}

static void log(String message) {
	_log.info(message);
}

static Log _log=LogFactoryUtil.getLog("com.brownstonetech.util.ViewLog");

%>