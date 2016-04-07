package com.brownstonetech.springliferay.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.KeyValuePair;
import com.liferay.portal.kernel.util.KeyValuePairComparator;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;

public class KeyValuePairUtil {

	/**
	 * Generate selected KeyValuePair list from given options Map and selectedItems parameter.
	 * <p>
	 * Item order in the selectedItems parameter is preserved in the result list.
	 * </p>
	 * @param options A Map to provide all available options for the select component.
	 * Key of the map is select option value, and Value of the map is select option label.
	 * @param selectedItems A comma separated list of multiple selected option values.
	 * @return
	 */
	public static List<KeyValuePair> getSelectedList(
			Map<String, String> options, String selectedItems) {
		String[] items = StringUtil.split(selectedItems);
		return getSelectedList(options, items);
	}
	
	/**
	 * Generate KeyValuePair list from given options Map and selectedItems parameter.
	 * <p>
	 * Item order in the selectedItems parameter is preserved in the result list.
	 * </p>
	 * 
	 * @param options A Map to provide all available options for the select component.
	 * Key of the map is select option value, and Value of the map is select option label.
	 * @param selectedItems An array of selected option values.
	 * @return
	 */
	public static List<KeyValuePair> getSelectedList(
			Map<String, String> options, String[] selectedItems) {
		
		List<KeyValuePair> selectedList = new ArrayList<KeyValuePair>(selectedItems.length);

		for (String selectedItem: selectedItems) {
			if ( options.containsKey(selectedItem)) {
				selectedList.add(new KeyValuePair(selectedItem, options.get(selectedItem)));
			}
		}
		return selectedList;
	}
	
	/**
	 * Generate remain unselected KeyValuePair list from given options Map and 
	 * selectedItems parameter.
	 * <p>
	 * Result list is sorted by option label (value).
	 * </p>
	 * 
	 * @param options A Map to provide all available options for the select component.
	 * Key of the map is select option value, and Value of the map is select option label.
	 * @param selectedItems A comma separated list of multiple selected option values.
	 * @return
	 */
	public static List<KeyValuePair> getUnselectedList(Map<String,String> options, String selectedItems) {
		String[] items = StringUtil.split(selectedItems);
		return getUnselectedList(options, items);
	}
	
	/**
	 * Generate remain unselected KeyValuePair list from given options Map and 
	 * selectedItems parameter.
	 * <p>
	 * Result list is sorted by option label (value).
	 * </p>
	 * 
	 * @param options A Map to provide all available options for the select component.
	 * Key of the map is select option value, and Value of the map is select option label.
	 * @param selectedItems An array of selected option values.
	 * @return
	 */
	public static List<KeyValuePair> getUnselectedList(
			Map<String, String> options, String[] selectedItems) {
		List<KeyValuePair> availablesList = new ArrayList<KeyValuePair>(options.size());

		for (Map.Entry<String,String> entry : options.entrySet()) {
			if (!ArrayUtil.contains(selectedItems, entry.getKey())) {
				availablesList.add(new KeyValuePair(entry.getKey(), entry.getValue()));
			}
		}

		availablesList = ListUtil.sort(availablesList, new KeyValuePairComparator(false, true));
		return availablesList;
	}
}
