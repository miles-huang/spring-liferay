package com.brownstonetech.springliferay.portlet.component;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import com.liferay.portal.kernel.captcha.CaptchaUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * This is a built in controller to implement Liferay captcha support.
 * <p>
 * This controller serves resource request with Captcha image on a form page,
 * then the response can be later verified using 
 * {@link com.liferay.portal.kernel.captcha.CaptchaUtil#check(javax.portlet.PortletRequest)}
 * when the form is submit back to verify the captcha.
 * </p>
 * <p>
 * In the page, use following code as example to show the captcha image served by
 * this controller:
 * </p>
 * <code>
 * &lt;img src="&lt;portlet:resourceURL id="captcha" /&gt;" alt="captcha" /&gt;
 * </code>
 * <p>
 * To enable this controller, you need configure the spring portlet MVC context
 * configuration to scan and include components defined in this package.
 * </p>
 * 
 * @author Miles Huang
 *
 */
@Controller
@RequestMapping("VIEW")
public class CaptchaController {

	/**
	 * 
	 */
	private static Log _log = LogFactoryUtil.getLog(CaptchaController.class);

	@ResourceMapping("captcha")
	public void execute(ResourceRequest resourceRequest, ResourceResponse resourceResponse) {
		try {
			resourceResponse.setProperty("Cache-Control", "no-cache");
			resourceResponse.setProperty("Pragma", "no-cache");
			resourceResponse.setProperty("Expires", "0");
//			resourceResponse.setContentType("image/png");
			CaptchaUtil.serveImage(
					resourceRequest, resourceResponse);
		}
		catch (Exception e) {
			_log.error("Failed to generate captcha image", e);
		}
	}
	
}
