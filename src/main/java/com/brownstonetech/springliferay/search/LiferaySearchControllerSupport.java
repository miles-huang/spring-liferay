package com.brownstonetech.springliferay.search;

import java.util.Iterator;
import java.util.Map;

import javax.portlet.MimeResponse;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletURL;

import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import com.brownstonetech.springliferay.LiferayControllerSupport;
import com.brownstonetech.springliferay.SpringLiferayWebKeys;
import com.liferay.portal.kernel.dao.search.DisplayTerms;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.PortletURLUtil;

/**
 * 
 * This is an abstract base class to support search page controller implementation.
 * 
 * @author Miles Huang
 *
 * @param <E> Type of searched Entity. This is consistent with the generated {@link SearchContainer}'s parameter type.
 * @param <S> User defined bean class to hold search form field values. It must extends from {@link DisplayTerms}.
 */
public abstract class LiferaySearchControllerSupport<E, S extends DisplayTerms> extends LiferayControllerSupport {

	private static Log _log = LogFactoryUtil.getLog(LiferaySearchControllerSupport.class);
	

	@RenderMapping
	public ModelAndView render(
			@ModelAttribute(SpringLiferayWebKeys.SEARCH_TERMS) DisplayTerms searchTerms,
			BindingResult bindingResult,
			@ModelAttribute(SpringLiferayWebKeys.THEME_DISPLAY) ThemeDisplay themeDisplay,
			PortletRequest portletRequest, MimeResponse mimeResponse,
			SessionStatus sessionStatus,
			ModelMap modelMap) {

		sessionStatus.setComplete();
		
		ModelAndView mav = new ModelAndView(getListView());

		try {
			@SuppressWarnings("unchecked")
			S s = (S)searchTerms;

			prepare(s, bindingResult, modelMap);
			
			int delta = getDefaultDelta(modelMap);
			boolean deltaConfigurable = isDeltaConfigurable(modelMap);
			
			SearchContainer<E> searchContainer
				= createSearchContainer(portletRequest, mimeResponse, s, delta, deltaConfigurable);
			
			mav.addObject(SpringLiferayWebKeys.SEARCH_CONTAINER, searchContainer);
			
			doSearch(
					themeDisplay.getCompanyId(),
					themeDisplay.getUserId(),
					searchContainer,
					s, bindingResult, modelMap);
		} catch (Exception e) {
			SessionErrors.add(portletRequest, "error.internal");
			_log.error("Fail to do entity search", e);
		}
		
		return mav;
	}
	
	@ModelAttribute(SpringLiferayWebKeys.SEARCH_TERMS)
	public DisplayTerms getSearchTerms(PortletRequest portletRequest) {
		return createSearchTerms(portletRequest);
	}
	
	@ModelAttribute(SpringLiferayWebKeys.CURRENT_URL)
	@Override
	public String currentURL(PortletRequest portletRequest, PortletResponse portletResponse) {
		LiferayPortletRequest liferayPortletRequest = (LiferayPortletRequest)portletRequest;
		LiferayPortletResponse liferayPortletResponse = (LiferayPortletResponse)portletResponse;
		PortletURL currentURLObj = PortletURLUtil.getCurrent(liferayPortletRequest, liferayPortletResponse);

		String currentURL = currentURLObj.toString();
		return currentURL;
	}
	
	/**
	 * Provide the default searchContainer model attribute implementation
	 * using {@link GenericSearchContainer}.
	 * <p>
	 * If you intend to use your extension class of the GenericSearchContainer or
	 * any other SearchContainer implementation, you need overwrite this method
	 * and provide your searchContainter instance.
	 * </p>
	 * 
	 * @param portletRequest
	 * @param mimeResponse
	 * @param searchTerms
	 * @return
	 */
	protected SearchContainer<E> createSearchContainer(PortletRequest portletRequest, MimeResponse mimeResponse,
			S searchTerms, int defaultDelta, boolean deltaConfigurable ) {
		PortletURL currentURLObj = PortletURLUtil.getCurrent(portletRequest, mimeResponse);
		Map<String, String[]> params = currentURLObj.getParameterMap();
		Iterator<Map.Entry<String,String[]>> iter = params.entrySet().iterator();
		while ( iter.hasNext()) {
			Map.Entry<String,String[]> entry = iter.next();
			// Exclude some parameters we don't want to inherit
			// for search form submit URL and also
			// Links in the search result table.
			String paramName = entry.getKey();
			if ( paramName.equals("formDate")
					|| paramName.endsWith("searchContainerPrimaryKeys")) {
				iter.remove();
			}
		}
		currentURLObj.setParameters(params);
		
		GenericSearchContainer<E, S> searchContainer
			= new GenericSearchContainer<E, S>(portletRequest, currentURLObj, searchTerms, defaultDelta);
		searchContainer.setDeltaConfigurable(deltaConfigurable);
		
		return searchContainer;
	}

	protected boolean isDeltaConfigurable(Map<String,Object> model) {
		return true;
	}
	
	protected int getDefaultDelta(Map<String,Object> model) {
		return SearchContainer.DEFAULT_DELTA;
	}
	
	/**
	 * Generate instance of user defined searchTerm bean.
	 * 
	 * @param portletRequest
	 * @return
	 */
	protected abstract S createSearchTerms(PortletRequest portletRequest);
	
	/**
	 * Subclasses need to implement this doSearch method by
	 * searching the entity according to the user defined searchTerms and searchContainer
	 * provided search conditions.</p>
	 * <p>
	 * As the output of this method, the number of matching records need to be set to searchContainer using
	 * {@link SearchContainer#setTotal(int)} method.
	 * The list of entities of current page to searchContainer using
	 * {@link SearchContainer#setResults(java.util.List)} method.
	 * </p>
	 * <p>
	 * Either indexer based or DB based search can be supported.
	 * </p>
	 * <p>
	 * To support pagination using values from searchContainer:
	 * <ul>
	 * <li>
	 * {@link SearchContainer#getStart()}</li>
	 * <li>{@link SearchContainer#getEnd()}
	 * </li>
	 * </ul>
	 * <p>
	 * To support sorting using these values from searchContainer:</p>
	 * <p>For indexer based search, the sorting representing {@link Sort} bean
	 * can be populated from searchContainer using {@link SortFactoryUtil#getSort(Class, String, String)} method.
	 * For example:</p>
	 * <code>Sort sort = SortFactoryUtil#getSort(User.class, searchContainer.getOrderByCol(), searchContainer.getOrderByType());</code>
	 * <ul>
	 * <li>{@link SearchContainer#getOrderByCol()}</li>
	 * <li>{@link SearchContainer#getOrderByType()}</li>
	 * </ul>
	 * <p>For Database backed search, the {@link OrderByComparator} bean can be
	 * get from searchContainer directly:</p>
	 * <ul>
	 * <li>{@link SearchContainer#getOrderByComparator()}</li>
	 * </ul>
	 * 
	 * @param companyId
	 * @param userId
	 * @param searchContainer
	 * This base controller generated ModelAttribute named "searchContainer".
	 * It is implemented by {@link GenericSearchContainer}.
	 * 
	 * @param searchTerms
	 * Application defined bean to hold search condition field values.
	 * @param modelMap
	 * Get model attributes and update model attribute via modelMap
	 * 
	 * @throws Exception
	 */
	protected abstract void doSearch(long companyId,
			long userId,
			SearchContainer<E> searchContainer, 
			S searchTerms, BindingResult bindingResult, ModelMap modelMap) throws Exception;
	
	/**
	 * Subclasses use this method to provide view location
	 * @return view location 
	 */
	protected abstract String getListView();

	/**
	 * Subclass override this method to do initializations before render
	 * @param portletInvocation
	 * @param command
	 * @param bindingResult
	 * @param modelMap
	 */
	protected void prepare(S command, BindingResult bindingResult,
			Map<String, Object> modelMap) {
	}
}
