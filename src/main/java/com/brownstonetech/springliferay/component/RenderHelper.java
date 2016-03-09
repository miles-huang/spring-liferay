package com.brownstonetech.springliferay.component;

import java.io.Serializable;
import java.util.Iterator;

import javax.portlet.PortletRequest;

import org.springframework.stereotype.Component;

import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;

@Component("renderHelper")
public class RenderHelper implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String toString(Object o) {
		return String.valueOf(o);
	}

	public String toJSONString(Object o) {
		return JSONFactoryUtil.looseSerializeDeep(o);
	}
	
	public Iterator<String> retrieveSessionErrors(PortletRequest portletRequest) {
		Iterator<String> errors =
				SessionErrors.iterator(portletRequest);
		return errors;
	}
	
	public Iterator<String> retrieveSessionMessages(PortletRequest portletRequest) {
		Iterator<String> errors =
				SessionMessages.iterator(portletRequest);
		return errors;
	}

}
