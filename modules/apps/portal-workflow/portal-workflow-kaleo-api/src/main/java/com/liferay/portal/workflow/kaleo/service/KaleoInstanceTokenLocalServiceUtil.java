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

package com.liferay.portal.workflow.kaleo.service;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Provides the local service utility for KaleoInstanceToken. This utility wraps
 * <code>com.liferay.portal.workflow.kaleo.service.impl.KaleoInstanceTokenLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see KaleoInstanceTokenLocalService
 * @generated
 */
public class KaleoInstanceTokenLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.workflow.kaleo.service.impl.KaleoInstanceTokenLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the kaleo instance token to the database. Also notifies the appropriate model listeners.
	 *
	 * @param kaleoInstanceToken the kaleo instance token
	 * @return the kaleo instance token that was added
	 */
	public static com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken
		addKaleoInstanceToken(
			com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken
				kaleoInstanceToken) {

		return getService().addKaleoInstanceToken(kaleoInstanceToken);
	}

	public static com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken
			addKaleoInstanceToken(
				long currentKaleoNodeId, long kaleoDefinitionVersionId,
				long kaleoInstanceId, long parentKaleoInstanceTokenId,
				java.util.Map<String, java.io.Serializable> workflowContext,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().addKaleoInstanceToken(
			currentKaleoNodeId, kaleoDefinitionVersionId, kaleoInstanceId,
			parentKaleoInstanceTokenId, workflowContext, serviceContext);
	}

	public static com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken
			addKaleoInstanceToken(
				long parentKaleoInstanceTokenId,
				java.util.Map<String, java.io.Serializable> workflowContext,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().addKaleoInstanceToken(
			parentKaleoInstanceTokenId, workflowContext, serviceContext);
	}

	public static com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken
			completeKaleoInstanceToken(long kaleoInstanceTokenId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().completeKaleoInstanceToken(kaleoInstanceTokenId);
	}

	/**
	 * Creates a new kaleo instance token with the primary key. Does not add the kaleo instance token to the database.
	 *
	 * @param kaleoInstanceTokenId the primary key for the new kaleo instance token
	 * @return the new kaleo instance token
	 */
	public static com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken
		createKaleoInstanceToken(long kaleoInstanceTokenId) {

		return getService().createKaleoInstanceToken(kaleoInstanceTokenId);
	}

	/**
	 * @throws PortalException
	 */
	public static com.liferay.portal.kernel.model.PersistedModel
			createPersistedModel(java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	public static void deleteCompanyKaleoInstanceTokens(long companyId) {
		getService().deleteCompanyKaleoInstanceTokens(companyId);
	}

	public static void deleteKaleoDefinitionVersionKaleoInstanceTokens(
		long kaleoDefinitionVersionId) {

		getService().deleteKaleoDefinitionVersionKaleoInstanceTokens(
			kaleoDefinitionVersionId);
	}

	public static void deleteKaleoInstanceKaleoInstanceTokens(
		long kaleoInstanceId) {

		getService().deleteKaleoInstanceKaleoInstanceTokens(kaleoInstanceId);
	}

	/**
	 * Deletes the kaleo instance token from the database. Also notifies the appropriate model listeners.
	 *
	 * @param kaleoInstanceToken the kaleo instance token
	 * @return the kaleo instance token that was removed
	 */
	public static com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken
		deleteKaleoInstanceToken(
			com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken
				kaleoInstanceToken) {

		return getService().deleteKaleoInstanceToken(kaleoInstanceToken);
	}

	/**
	 * Deletes the kaleo instance token with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param kaleoInstanceTokenId the primary key of the kaleo instance token
	 * @return the kaleo instance token that was removed
	 * @throws PortalException if a kaleo instance token with the primary key could not be found
	 */
	public static com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken
			deleteKaleoInstanceToken(long kaleoInstanceTokenId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().deleteKaleoInstanceToken(kaleoInstanceTokenId);
	}

	/**
	 * @throws PortalException
	 */
	public static com.liferay.portal.kernel.model.PersistedModel
			deletePersistedModel(
				com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	public static com.liferay.portal.kernel.dao.orm.DynamicQuery
		dynamicQuery() {

		return getService().dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	public static <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return getService().dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoInstanceTokenModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @return the range of matching rows
	 */
	public static <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return getService().dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoInstanceTokenModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching rows
	 */
	public static <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return getService().dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return getService().dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return getService().dynamicQueryCount(dynamicQuery, projection);
	}

	public static com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken
		fetchKaleoInstanceToken(long kaleoInstanceTokenId) {

		return getService().fetchKaleoInstanceToken(kaleoInstanceTokenId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the kaleo instance token with the primary key.
	 *
	 * @param kaleoInstanceTokenId the primary key of the kaleo instance token
	 * @return the kaleo instance token
	 * @throws PortalException if a kaleo instance token with the primary key could not be found
	 */
	public static com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken
			getKaleoInstanceToken(long kaleoInstanceTokenId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getKaleoInstanceToken(kaleoInstanceTokenId);
	}

	/**
	 * Returns a range of all the kaleo instance tokens.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoInstanceTokenModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of kaleo instance tokens
	 * @param end the upper bound of the range of kaleo instance tokens (not inclusive)
	 * @return the range of kaleo instance tokens
	 */
	public static java.util.List
		<com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken>
			getKaleoInstanceTokens(int start, int end) {

		return getService().getKaleoInstanceTokens(start, end);
	}

	public static java.util.List
		<com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken>
			getKaleoInstanceTokens(
				long parentKaleoInstanceTokenId, java.util.Date completionDate,
				com.liferay.portal.kernel.service.ServiceContext
					serviceContext) {

		return getService().getKaleoInstanceTokens(
			parentKaleoInstanceTokenId, completionDate, serviceContext);
	}

	public static java.util.List
		<com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken>
			getKaleoInstanceTokens(
				long parentKaleoInstanceTokenId,
				com.liferay.portal.kernel.service.ServiceContext
					serviceContext) {

		return getService().getKaleoInstanceTokens(
			parentKaleoInstanceTokenId, serviceContext);
	}

	/**
	 * Returns the number of kaleo instance tokens.
	 *
	 * @return the number of kaleo instance tokens
	 */
	public static int getKaleoInstanceTokensCount() {
		return getService().getKaleoInstanceTokensCount();
	}

	public static int getKaleoInstanceTokensCount(
		long parentKaleoInstanceTokenId, java.util.Date completionDate,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return getService().getKaleoInstanceTokensCount(
			parentKaleoInstanceTokenId, completionDate, serviceContext);
	}

	public static int getKaleoInstanceTokensCount(
		long parentKaleoInstanceTokenId,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return getService().getKaleoInstanceTokensCount(
			parentKaleoInstanceTokenId, serviceContext);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	public static com.liferay.portal.kernel.model.PersistedModel
			getPersistedModel(java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getPersistedModel(primaryKeyObj);
	}

	public static com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken
			getRootKaleoInstanceToken(
				long kaleoInstanceId,
				java.util.Map<String, java.io.Serializable> workflowContext,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getRootKaleoInstanceToken(
			kaleoInstanceId, workflowContext, serviceContext);
	}

	public static com.liferay.portal.kernel.search.Hits search(
		Long userId, String assetClassName, String assetTitle,
		String assetDescription, String currentKaleoNodeName,
		String kaleoDefinitionName, Boolean completed, int start, int end,
		com.liferay.portal.kernel.search.Sort[] sorts,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return getService().search(
			userId, assetClassName, assetTitle, assetDescription,
			currentKaleoNodeName, kaleoDefinitionName, completed, start, end,
			sorts, serviceContext);
	}

	public static int searchCount(
		Long userId, String assetClassName, String assetTitle,
		String assetDescription, String currentKaleoNodeName,
		String kaleoDefinitionName, Boolean completed,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return getService().searchCount(
			userId, assetClassName, assetTitle, assetDescription,
			currentKaleoNodeName, kaleoDefinitionName, completed,
			serviceContext);
	}

	/**
	 * Updates the kaleo instance token in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * @param kaleoInstanceToken the kaleo instance token
	 * @return the kaleo instance token that was updated
	 */
	public static com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken
		updateKaleoInstanceToken(
			com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken
				kaleoInstanceToken) {

		return getService().updateKaleoInstanceToken(kaleoInstanceToken);
	}

	public static com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken
			updateKaleoInstanceToken(
				long kaleoInstanceTokenId, long currentKaleoNodeId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().updateKaleoInstanceToken(
			kaleoInstanceTokenId, currentKaleoNodeId);
	}

	public static KaleoInstanceTokenLocalService getService() {
		return _serviceTracker.getService();
	}

	private static ServiceTracker
		<KaleoInstanceTokenLocalService, KaleoInstanceTokenLocalService>
			_serviceTracker;

	static {
		Bundle bundle = FrameworkUtil.getBundle(
			KaleoInstanceTokenLocalService.class);

		ServiceTracker
			<KaleoInstanceTokenLocalService, KaleoInstanceTokenLocalService>
				serviceTracker =
					new ServiceTracker
						<KaleoInstanceTokenLocalService,
						 KaleoInstanceTokenLocalService>(
							 bundle.getBundleContext(),
							 KaleoInstanceTokenLocalService.class, null);

		serviceTracker.open();

		_serviceTracker = serviceTracker;
	}

}