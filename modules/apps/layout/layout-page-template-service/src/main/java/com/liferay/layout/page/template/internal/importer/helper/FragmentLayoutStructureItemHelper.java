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

package com.liferay.layout.page.template.internal.importer.helper;

import com.liferay.fragment.contributor.FragmentCollectionContributorTracker;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalServiceUtil;
import com.liferay.fragment.service.FragmentEntryLocalServiceUtil;
import com.liferay.headless.delivery.dto.v1_0.PageElement;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jürgen Kappler
 */
public class FragmentLayoutStructureItemHelper
	implements LayoutStructureItemHelper {

	@Override
	public LayoutStructureItem addLayoutStructureItem(
		FragmentCollectionContributorTracker
			fragmentCollectionContributorTracker,
		LayoutPageTemplateEntry layoutPageTemplateEntry,
		LayoutStructure layoutStructure, PageElement pageElement,
		String parentItemId, int position) {

		FragmentEntryLink fragmentEntryLink = _addFragmentEntryLink(
			fragmentCollectionContributorTracker, layoutPageTemplateEntry,
			pageElement, position);

		if (fragmentEntryLink == null) {
			return null;
		}

		return layoutStructure.addFragmentLayoutStructureItem(
			fragmentEntryLink.getFragmentEntryLinkId(), parentItemId, position);
	}

	private FragmentEntryLink _addFragmentEntryLink(
		FragmentCollectionContributorTracker
			fragmentCollectionContributorTracker,
		LayoutPageTemplateEntry layoutPageTemplateEntry,
		PageElement pageElement, int position) {

		Map<String, Object> definitionMap =
			(Map<String, Object>)pageElement.getDefinition();

		if (definitionMap == null) {
			return null;
		}

		Map<String, Object> fragmentDefinitionMap =
			(Map<String, Object>)definitionMap.get("fragment");

		String fragmentKey = (String)fragmentDefinitionMap.get("fragmentKey");

		if (Validator.isNull(fragmentKey)) {
			return null;
		}

		FragmentEntry fragmentEntry = _getFragmentEntry(
			fragmentCollectionContributorTracker, fragmentKey,
			layoutPageTemplateEntry);

		long fragmentEntryId = 0;
		String html = StringPool.BLANK;
		String js = StringPool.BLANK;
		String css = StringPool.BLANK;
		String configuration = StringPool.BLANK;

		if (fragmentEntry != null) {
			fragmentEntryId = fragmentEntry.getFragmentEntryId();

			html = fragmentEntry.getHtml();
			js = fragmentEntry.getJs();
			css = fragmentEntry.getCss();
			configuration = fragmentEntry.getConfiguration();
		}

		JSONObject jsonObject = JSONUtil.put(
			"com.liferay.fragment.entry.processor.background.image." +
				"BackgroundImageFragmentEntryProcessor",
			JSONFactoryUtil.createJSONObject()
		).put(
			"com.liferay.fragment.entry.processor.editable." +
				"EditableFragmentEntryProcessor",
			_createEditablesValuesJSONObject(
				(List<Object>)definitionMap.get("fragmentFields"))
		).put(
			"com.liferay.fragment.entry.processor.freemarker." +
				"FreeMarkerFragmentEntryProcessor",
			_createConfigurationValuesJSONObject(
				(Map<String, Object>)definitionMap.get("fragmentConfig"))
		);

		try {
			return FragmentEntryLinkLocalServiceUtil.addFragmentEntryLink(
				layoutPageTemplateEntry.getUserId(),
				layoutPageTemplateEntry.getGroupId(), 0, fragmentEntryId,
				PortalUtil.getClassNameId(Layout.class.getName()),
				layoutPageTemplateEntry.getPlid(), css, html, js, configuration,
				jsonObject.toString(), StringUtil.randomId(), position,
				fragmentKey, ServiceContextThreadLocal.getServiceContext());
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(portalException, portalException);
			}
		}

		return null;
	}

	private JSONObject _createConfigurationValuesJSONObject(
		Map<String, Object> fragmentConfigMap) {

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		if (fragmentConfigMap == null) {
			return jsonObject;
		}

		for (Map.Entry<String, Object> entry : fragmentConfigMap.entrySet()) {
			if (entry.getValue() instanceof String) {
				jsonObject.put(entry.getKey(), entry.getValue());
			}
			else if (entry.getValue() instanceof HashMap) {
				Map<String, Object> childFragmentConfigMap =
					(Map<String, Object>)entry.getValue();

				jsonObject.put(
					entry.getKey(),
					_createConfigurationValuesJSONObject(
						childFragmentConfigMap));
			}
		}

		return jsonObject;
	}

	private JSONObject _createEditablesValuesJSONObject(
		List<Object> fragmentFields) {

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		if (fragmentFields == null) {
			return jsonObject;
		}

		for (Object fragmentField : fragmentFields) {
			JSONObject fragmentFieldJSONObject =
				JSONFactoryUtil.createJSONObject();

			Map<String, Object> fragmentFieldMap =
				(Map<String, Object>)fragmentField;

			String fragmentFieldId = (String)fragmentFieldMap.get("id");

			if (Validator.isNull(fragmentFieldId)) {
				continue;
			}

			Map<String, Object> valueMap =
				(Map<String, Object>)fragmentFieldMap.get("value");

			if (valueMap == null) {
				continue;
			}

			JSONObject fragmentLinkJSONObject = _createFragmentLinkJSONObject(
				(Map<String, Object>)valueMap.get("fragmentLink"));

			if (fragmentLinkJSONObject != null) {
				fragmentFieldJSONObject.put("config", fragmentLinkJSONObject);
			}

			JSONObject localizationJSONObject = _createLocalizationJSONObject(
				(Map<String, Object>)valueMap.get("text"));

			try {
				jsonObject.put(
					fragmentFieldId,
					JSONUtil.merge(
						fragmentFieldJSONObject, localizationJSONObject));
			}
			catch (JSONException jsonException) {
				if (_log.isWarnEnabled()) {
					_log.warn(jsonException, jsonException);
				}
			}
		}

		return jsonObject;
	}

	private JSONObject _createFragmentLinkJSONObject(
		Map<String, Object> fragmentLinkMap) {

		JSONObject fragmentLinkJSONObject = JSONFactoryUtil.createJSONObject();

		if (fragmentLinkMap == null) {
			return fragmentLinkJSONObject;
		}

		fragmentLinkJSONObject.put("target", fragmentLinkMap.get("target"));

		Map<String, Object> valueMap = (Map<String, Object>)fragmentLinkMap.get(
			"value");

		if (valueMap != null) {
			fragmentLinkJSONObject.put("href", valueMap.get("href"));
		}

		return fragmentLinkJSONObject;
	}

	private JSONObject _createLocalizationJSONObject(
		Map<String, Object> textMap) {

		JSONObject localizationJSONObject = JSONFactoryUtil.createJSONObject();

		if (textMap == null) {
			return localizationJSONObject;
		}

		Map<String, Object> valueI18nMap = (Map<String, Object>)textMap.get(
			"value_i18n");

		if (valueI18nMap != null) {
			for (Map.Entry<String, Object> entry : valueI18nMap.entrySet()) {
				localizationJSONObject.put(entry.getKey(), entry.getValue());
			}
		}

		return localizationJSONObject;
	}

	private FragmentEntry _getFragmentEntry(
		FragmentCollectionContributorTracker
			fragmentCollectionContributorTracker,
		String fragmentKey, LayoutPageTemplateEntry layoutPageTemplateEntry) {

		FragmentEntry fragmentEntry =
			FragmentEntryLocalServiceUtil.fetchFragmentEntry(
				layoutPageTemplateEntry.getGroupId(), fragmentKey);

		if (fragmentEntry == null) {
			fragmentEntry =
				fragmentCollectionContributorTracker.getFragmentEntry(
					fragmentKey);
		}

		return fragmentEntry;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FragmentLayoutStructureItemHelper.class);

}