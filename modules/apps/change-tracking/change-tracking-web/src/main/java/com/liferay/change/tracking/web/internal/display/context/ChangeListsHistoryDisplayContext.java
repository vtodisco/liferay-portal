/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.change.tracking.web.internal.display.context;

import com.liferay.change.tracking.constants.CTPortletKeys;
import com.liferay.change.tracking.web.internal.constants.CTWebConstants;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.template.soy.util.SoyContext;
import com.liferay.portal.template.soy.util.SoyContextFactoryUtil;

import java.util.List;
import java.util.Objects;

import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceURL;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Máté Thurzó
 */
public class ChangeListsHistoryDisplayContext {

	public ChangeListsHistoryDisplayContext(
		HttpServletRequest httpServletRequest, RenderResponse renderResponse) {

		_httpServletRequest = httpServletRequest;
		_renderResponse = renderResponse;

		_themeDisplay = (ThemeDisplay)_httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public SoyContext getChangeListsHistoryContext() throws PortalException {
		SoyContext soyContext = SoyContextFactoryUtil.createSoyContext();

		soyContext.put(
			"baseURL", String.valueOf(_getPortletURL())
		).put(
			"filterStatus", _getFilterByStatus()
		).put(
			"filterUser", _getFilterByUser()
		).put(
			"keywords", _getKeywords()
		).put(
			"orderByCol", _getOrderByCol()
		).put(
			"orderByType", getOrderByType()
		).put(
			"spritemap",
			_themeDisplay.getPathThemeImages() + "/lexicon/icons.svg"
		);

		ResourceURL processesURL = PortletURLFactoryUtil.create(
			_httpServletRequest, CTPortletKeys.CHANGE_LISTS_HISTORY,
			PortletRequest.RESOURCE_PHASE);

		processesURL.setResourceID("/change_lists_history/get_ct_processes");

		soyContext.put("urlProcesses", processesURL.toString());

		processesURL.setResourceID(
			"/change_lists_history/get_ct_process_users");

		soyContext.put("urlProcessUsers", processesURL.toString());

		return soyContext;
	}

	public String getClearResultsActionURL() {
		PortletURL portletURL = _getPortletURL();

		return portletURL.toString();
	}

	public List<DropdownItem> getFilterDropdownItems() {
		return DropdownItemListBuilder.addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					_getFilterStatusDropdownItems());
				dropdownGroupItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "filter-by-status"));
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					_getFilterUserDropdownItems());
				dropdownGroupItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "filter-by-user"));
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(_getOrderByDropdownItems());
				dropdownGroupItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "order-by"));
			}
		).build();
	}

	public String getOrderByType() {
		if (_orderByType != null) {
			return _orderByType;
		}

		_orderByType = ParamUtil.getString(
			_httpServletRequest, "orderByType", "desc");

		return _orderByType;
	}

	public String getSortingURL() {
		PortletURL sortingURL = _getKeywordsURL();

		sortingURL.setParameter(
			"orderByType",
			Objects.equals(getOrderByType(), "asc") ? "desc" : "asc");

		return sortingURL.toString();
	}

	public String getViewSearchActionURL() {
		PortletURL portletURL = _getKeywordsURL();

		return portletURL.toString();
	}

	private String _getDisplayStyle() {
		if (_displayStyle != null) {
			return _displayStyle;
		}

		_displayStyle = ParamUtil.getString(
			_httpServletRequest, "displayStyle", "list");

		return _displayStyle;
	}

	private String _getFilterByStatus() {
		if (_filterByStatus != null) {
			return _filterByStatus;
		}

		_filterByStatus = ParamUtil.getString(
			_httpServletRequest, "status", "all");

		return _filterByStatus;
	}

	private long _getFilterByUser() {
		if (_filterByUser != null) {
			return _filterByUser;
		}

		_filterByUser = ParamUtil.getLong(
			_httpServletRequest, "user", CTWebConstants.USER_FILTER_ALL);

		return _filterByUser;
	}

	private List<DropdownItem> _getFilterStatusDropdownItems() {
		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.setActive(
					Objects.equals(_getFilterByStatus(), "all"));
				dropdownItem.setHref(_getKeywordsURL(), "status", "all");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "all"));
			}
		).add(
			dropdownItem -> {
				dropdownItem.setActive(
					Objects.equals(_getFilterByStatus(), "published"));
				dropdownItem.setHref(_getKeywordsURL(), "status", "published");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "published"));
			}
		).add(
			dropdownItem -> {
				dropdownItem.setActive(
					Objects.equals(_getFilterByStatus(), "failed"));
				dropdownItem.setHref(_getKeywordsURL(), "status", "failed");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "failed"));
			}
		).add(
			dropdownItem -> {
				dropdownItem.setActive(
					Objects.equals(_getFilterByStatus(), "in-progress"));
				dropdownItem.setHref(
					_getKeywordsURL(), "status", "in-progress");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "in-progress"));
			}
		).add(
			dropdownItem -> {
				dropdownItem.setActive(
					Objects.equals(_getFilterByStatus(), "scheduled"));
				dropdownItem.setHref(_getKeywordsURL(), "status", "scheduled");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "scheduled"));
			}
		).build();
	}

	private List<DropdownItem> _getFilterUserDropdownItems() {
		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.setActive(
					_getFilterByUser() == CTWebConstants.USER_FILTER_ALL);
				dropdownItem.setHref(
					_getKeywordsURL(), "user", CTWebConstants.USER_FILTER_ALL);
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "all"));
			}
		).build();
	}

	private String _getKeywords() {
		if (_keywords != null) {
			return _keywords;
		}

		_keywords = ParamUtil.getString(_httpServletRequest, "keywords", null);

		return _keywords;
	}

	private PortletURL _getKeywordsURL() {
		PortletURL portletURL = _getPortletURL();

		String keywords = _getKeywords();

		if (Validator.isNotNull(keywords)) {
			portletURL.setParameter("keywords", keywords);
		}

		return portletURL;
	}

	private String _getOrderByCol() {
		if (_orderByCol != null) {
			return _orderByCol;
		}

		_orderByCol = ParamUtil.getString(
			_httpServletRequest, "orderByCol", "publishDate");

		return _orderByCol;
	}

	private List<DropdownItem> _getOrderByDropdownItems() {
		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.setActive(
					Objects.equals(_getOrderByCol(), "publishDate"));
				dropdownItem.setHref(
					_getKeywordsURL(), "orderByCol", "publishDate");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "publish-date"));
			}
		).add(
			dropdownItem -> {
				dropdownItem.setActive(
					Objects.equals(_getOrderByCol(), "name"));
				dropdownItem.setHref(_getKeywordsURL(), "orderByCol", "name");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "name"));
			}
		).build();
	}

	private PortletURL _getPortletURL() {
		PortletURL portletURL = _renderResponse.createRenderURL();

		String displayStyle = ParamUtil.getString(
			_httpServletRequest, "displayStyle");

		if (Validator.isNotNull(displayStyle)) {
			portletURL.setParameter("displayStyle", _getDisplayStyle());
		}

		String orderByCol = _getOrderByCol();

		if (Validator.isNotNull(orderByCol)) {
			portletURL.setParameter("orderByCol", orderByCol);
		}

		String orderByType = getOrderByType();

		if (Validator.isNotNull(orderByType)) {
			portletURL.setParameter("orderByType", orderByType);
		}

		return portletURL;
	}

	private String _displayStyle;
	private String _filterByStatus;
	private Long _filterByUser;
	private final HttpServletRequest _httpServletRequest;
	private String _keywords;
	private String _orderByCol;
	private String _orderByType;
	private final RenderResponse _renderResponse;
	private final ThemeDisplay _themeDisplay;

}