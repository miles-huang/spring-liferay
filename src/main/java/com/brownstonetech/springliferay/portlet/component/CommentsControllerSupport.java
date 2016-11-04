package com.brownstonetech.springliferay.portlet.component;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.springframework.web.portlet.bind.annotation.ActionMapping;

import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;
import com.liferay.portal.service.SubscriptionLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.messageboards.model.MBMessage;
import com.liferay.portlet.messageboards.service.MBMessageServiceUtil;

/**
 * Controller supports entity comments and rating.
 * 
 * @author Miles Huang
 *
 */
public abstract class CommentsControllerSupport {

	@ActionMapping(value="updateDiscussion", params={Constants.CMD+"="+Constants.ADD})
	public void add(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {
		updateMessage(actionRequest, actionResponse);
	}
	
	@ActionMapping(value="updateDiscussion", params={Constants.CMD+"="+Constants.UPDATE})
	public void update(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {
		updateMessage(actionRequest, actionResponse);
	}
	
	protected MBMessage updateMessage(ActionRequest actionRequest, ActionResponse actionResponse)
			throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		String className = ParamUtil.getString(actionRequest, "className");
		long classPK = ParamUtil.getLong(actionRequest, "classPK");
		String permissionClassName = ParamUtil.getString(
				actionRequest, "permissionClassName");
		long permissionClassPK = ParamUtil.getLong(
				actionRequest, "permissionClassPK");
		long permissionOwnerId = ParamUtil.getLong(
				actionRequest, "permissionOwnerId");

		long messageId = ParamUtil.getLong(actionRequest, "messageId");

		long threadId = ParamUtil.getLong(actionRequest, "threadId");
		long parentMessageId = ParamUtil.getLong(
				actionRequest, "parentMessageId");
		String subject = ParamUtil.getString(actionRequest, "subject");
		String body = ParamUtil.getString(actionRequest, "body");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
				MBMessage.class.getName(), actionRequest);

		MBMessage message = null;

		if (messageId <= 0) {
			message = MBMessageServiceUtil.addDiscussionMessage(
					serviceContext.getScopeGroupId(), className, classPK,
					permissionClassName, permissionClassPK, permissionOwnerId,
					threadId, parentMessageId, subject, body, serviceContext);
		}
		else {
			message = MBMessageServiceUtil.updateDiscussionMessage(
					className, classPK, permissionClassName, permissionClassPK,
					permissionOwnerId, messageId, subject, body, serviceContext);
		}

		// Subscription

		boolean subscribe = ParamUtil.getBoolean(actionRequest, "subscribe");

		if (subscribe) {
			SubscriptionLocalServiceUtil.addSubscription(
					themeDisplay.getUserId(), themeDisplay.getScopeGroupId(),
					className, classPK);
		}

		return message;
	}

	@ActionMapping(value="updateDiscussion", params={Constants.CMD+"="+Constants.DELETE})
	public void delete(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {
		long groupId = PortalUtil.getScopeGroupId(actionRequest);

		String className = ParamUtil.getString(actionRequest, "className");
		long classPK = ParamUtil.getLong(actionRequest, "classPK");
		String permissionClassName = ParamUtil.getString(
				actionRequest, "permissionClassName");
		long permissionClassPK = ParamUtil.getLong(
				actionRequest, "permissionClassPK");
		long permissionOwnerId = ParamUtil.getLong(
				actionRequest, "permissionOwnerId");

		long messageId = ParamUtil.getLong(actionRequest, "messageId");

		MBMessageServiceUtil.deleteDiscussionMessage(
				groupId, className, classPK, permissionClassName, permissionClassPK,
				permissionOwnerId, messageId);
	}

	@ActionMapping(value="updateDiscussion", params={Constants.CMD+"="+Constants.SUBSCRIBE_TO_COMMENTS})
	public void subscribeToComments(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {
		subscribeToComments(actionRequest, actionResponse, true);
	}
	
	@ActionMapping(value="updateDiscussion", params={Constants.CMD+"="+Constants.UNSUBSCRIBE_FROM_COMMENTS})
	public void unsubscribeFromComments(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {
		subscribeToComments(actionRequest, actionResponse, false);
	}
	
	protected void subscribeToComments(
			ActionRequest actionRequest, ActionResponse actionResponse, boolean subscribe)
					throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		String className = ParamUtil.getString(actionRequest, "className");
		long classPK = ParamUtil.getLong(actionRequest, "classPK");

		if (subscribe) {
			SubscriptionLocalServiceUtil.addSubscription(
					themeDisplay.getUserId(), themeDisplay.getScopeGroupId(),
					className, classPK);
		}
		else {
			SubscriptionLocalServiceUtil.deleteSubscription(
					themeDisplay.getUserId(), className, classPK);
		}
	}


}
