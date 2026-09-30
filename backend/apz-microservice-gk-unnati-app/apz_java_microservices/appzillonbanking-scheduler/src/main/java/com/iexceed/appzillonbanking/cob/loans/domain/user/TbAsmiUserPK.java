package com.iexceed.appzillonbanking.cob.loans.domain.user;

import java.io.Serializable;

import javax.persistence.Column;

public class TbAsmiUserPK implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name = "USER_ID")
	private String userId;

	@Column(name = "APP_ID")
	private String appId;

	public TbAsmiUserPK() {
	}

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getAppId() {
		return this.appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof TbAsmiUserPK)) {
			return false;
		}
		TbAsmiUserPK castOther = (TbAsmiUserPK) other;
		return this.userId.equals(castOther.userId) && this.appId.equals(castOther.appId);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.userId.hashCode();
		hash = hash * prime + this.appId.hashCode();

		return hash;
	}
}