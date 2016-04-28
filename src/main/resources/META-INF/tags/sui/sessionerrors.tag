<%@ tag pageEncoding="UTF-8" %>
<%@ tag description="Display all errors contained in SessionErrors. Using error key as resource bundle key." %>

<%@ include file="/META-INF/tags/init.tagf" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>

<portlet:defineObjects/>
<%
java.util.Iterator<String> errors = com.liferay.portal.kernel.servlet.SessionErrors.iterator(renderRequest);
while (errors.hasNext()) {
	String key = errors.next();
%>
	<liferay-ui:error key="<%= key %>" message="<%= key %>" />
<%
}
%>