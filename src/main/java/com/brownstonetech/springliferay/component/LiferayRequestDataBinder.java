package com.brownstonetech.springliferay.component;

import javax.portlet.PortletRequest;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.portlet.bind.PortletRequestDataBinder;
import org.springframework.web.portlet.bind.PortletRequestParameterPropertyValues;
import org.springframework.web.portlet.util.PortletUtils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringBundler;

/**
 * Add special logic to handle request parameters generated by 
 * Liferay aui and liferay-ui tags.
 * 
 * @author Miles Huang
 *
 */
public class LiferayRequestDataBinder extends PortletRequestDataBinder {

	public LiferayRequestDataBinder(Object target) {
		super(target);
	}

	public LiferayRequestDataBinder(Object target, String objectName) {
		super(target, objectName);
	}

	@Override
	public void bind(PortletRequest request) {
		MutablePropertyValues mpvs = new PortletRequestParameterPropertyValues(request);
		MultipartRequest multipartRequest = PortletUtils.getNativeRequest(request, MultipartRequest.class);
		if (multipartRequest != null) {
			bindMultipart(multipartRequest.getMultiFileMap(), mpvs);
		}
		handleDateTimeParameters(mpvs);
		doBind(mpvs);
	}

	private void handleDateTimeParameters(MutablePropertyValues mpvs) {
		for ( PropertyValue pv: mpvs.getPropertyValueList()) {
			String name = pv.getName();
			Object value = pv.getValue();
			if ( mpvs.contains(name+"Year")
					&& mpvs.contains(name+"Month")
					&& mpvs.contains(name+"Day")
					&& mpvs.contains(name+"Time")) {
				String combinedValue = new StringBundler(3)
						.append(value).append(' ').append(mpvs.get(name+"Time")).toString();
				mpvs.add(name, combinedValue);
				if ( _log.isDebugEnabled() ) {
					_log.debug("Find date and time property, combined value: "+name+"="+value+" "+mpvs.get(name+"Time"));
				}
			}
		}
//		for ( PropertyValue pv: mpvs.getPropertyValueList()) {
//			String name = pv.getName();
//			Object value = pv.getValue();
//			_log.info("Request parameters after process: "+name+"="+value);
//		}
	}
	
	protected static Log _log = LogFactoryUtil.getLog(LiferayRequestDataBinder.class);

}
