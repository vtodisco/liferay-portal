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

package com.liferay.app.builder.rest.client.dto.v1_0;

import com.liferay.app.builder.rest.client.function.UnsafeSupplier;
import com.liferay.app.builder.rest.client.serdes.v1_0.AppSerDes;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Generated;

/**
 * @author Gabriel Albuquerque
 * @generated
 */
@Generated("")
public class App implements Cloneable {

	public AppDeployment[] getAppDeployments() {
		return appDeployments;
	}

	public void setAppDeployments(AppDeployment[] appDeployments) {
		this.appDeployments = appDeployments;
	}

	public void setAppDeployments(
		UnsafeSupplier<AppDeployment[], Exception>
			appDeploymentsUnsafeSupplier) {

		try {
			appDeployments = appDeploymentsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected AppDeployment[] appDeployments;

	public Long getDataDefinitionId() {
		return dataDefinitionId;
	}

	public void setDataDefinitionId(Long dataDefinitionId) {
		this.dataDefinitionId = dataDefinitionId;
	}

	public void setDataDefinitionId(
		UnsafeSupplier<Long, Exception> dataDefinitionIdUnsafeSupplier) {

		try {
			dataDefinitionId = dataDefinitionIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long dataDefinitionId;

	public Long getDataLayoutId() {
		return dataLayoutId;
	}

	public void setDataLayoutId(Long dataLayoutId) {
		this.dataLayoutId = dataLayoutId;
	}

	public void setDataLayoutId(
		UnsafeSupplier<Long, Exception> dataLayoutIdUnsafeSupplier) {

		try {
			dataLayoutId = dataLayoutIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long dataLayoutId;

	public Long getDataListViewId() {
		return dataListViewId;
	}

	public void setDataListViewId(Long dataListViewId) {
		this.dataListViewId = dataListViewId;
	}

	public void setDataListViewId(
		UnsafeSupplier<Long, Exception> dataListViewIdUnsafeSupplier) {

		try {
			dataListViewId = dataListViewIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long dataListViewId;

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public void setDateCreated(
		UnsafeSupplier<Date, Exception> dateCreatedUnsafeSupplier) {

		try {
			dateCreated = dateCreatedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date dateCreated;

	public Date getDateModified() {
		return dateModified;
	}

	public void setDateModified(Date dateModified) {
		this.dateModified = dateModified;
	}

	public void setDateModified(
		UnsafeSupplier<Date, Exception> dateModifiedUnsafeSupplier) {

		try {
			dateModified = dateModifiedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date dateModified;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setId(UnsafeSupplier<Long, Exception> idUnsafeSupplier) {
		try {
			id = idUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long id;

	public Map<String, Object> getName() {
		return name;
	}

	public void setName(Map<String, Object> name) {
		this.name = name;
	}

	public void setName(
		UnsafeSupplier<Map<String, Object>, Exception> nameUnsafeSupplier) {

		try {
			name = nameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, Object> name;

	public Long getSiteId() {
		return siteId;
	}

	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}

	public void setSiteId(
		UnsafeSupplier<Long, Exception> siteIdUnsafeSupplier) {

		try {
			siteId = siteIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long siteId;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setStatus(
		UnsafeSupplier<String, Exception> statusUnsafeSupplier) {

		try {
			status = statusUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String status;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public void setUserId(
		UnsafeSupplier<Long, Exception> userIdUnsafeSupplier) {

		try {
			userId = userIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long userId;

	@Override
	public App clone() throws CloneNotSupportedException {
		return (App)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof App)) {
			return false;
		}

		App app = (App)object;

		return Objects.equals(toString(), app.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return AppSerDes.toJSON(this);
	}

}