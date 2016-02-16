package com.brownstonetech.springliferay.util.scripting;

import java.util.Map;
import java.util.Set;

public interface ScriptHandler {

	public Set<String> getOutputVariables();
	public void populateAdditionalScriptContext(Map<String,Object> scriptContext);
	public void handleScriptResult(Map<String,Object> result);

}

