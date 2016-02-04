<%@ tag pageEncoding="UTF-8" %>
<%@ tag description="Display all errors contained in SessionErrors. Using error key as resource bundle key." %>

<%@ include file="../init.tagf" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%
java.util.Iterator<String> errors = com.liferay.portal.kernel.servlet.SessionErrors.iterator(portletRequest);
while (errors.hasNext()) {
	String key = errors.next();
%>
	<liferay-ui:error key="<%= key %>" message="<%= key %>" />
<%
}
%>