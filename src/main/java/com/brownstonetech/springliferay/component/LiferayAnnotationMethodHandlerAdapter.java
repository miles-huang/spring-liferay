package com.brownstonetech.springliferay.component;

import javax.portlet.PortletRequest;

import org.springframework.stereotype.Component;
import org.springframework.web.portlet.bind.PortletRequestDataBinder;
import org.springframework.web.portlet.mvc.annotation.AnnotationMethodHandlerAdapter;

@Component("liferayAnnotationMethodHandlerAdapter")
public class LiferayAnnotationMethodHandlerAdapter extends AnnotationMethodHandlerAdapter {

	@Override
	protected PortletRequestDataBinder createBinder(PortletRequest request, Object target, String objectName) throws Exception {
		
		return new LiferayRequestDataBinder(target, objectName);
	}

	@Override
	public int getOrder() {
		// Specify an order value to use this HandlerAdapter
		// to replace default implementation
		return 100;
	}
	
}
