<%@ tag pageEncoding="UTF-8" %>
<%@ tag description="Display all errors contained in SessionErrors. Using error key as resource bundle key." %>

<%@ include file="../init.tagf" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<portlet:defineObjects/>
<%
java.util.Iterator<String> messages = com.liferay.portal.kernel.servlet.SessionMessages.iterator(renderRequest);
while (messages.hasNext()) {
	String key = messages.next();
%>
	<liferay-ui:success key="<%= key %>" message="<%= key %>" />
<%
}
%>