package com.brownstonetech.springliferay.component;

import java.io.Serializable;

import org.springframework.stereotype.Component;

import com.liferay.portal.kernel.json.JSONFactoryUtil;

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
	
}
