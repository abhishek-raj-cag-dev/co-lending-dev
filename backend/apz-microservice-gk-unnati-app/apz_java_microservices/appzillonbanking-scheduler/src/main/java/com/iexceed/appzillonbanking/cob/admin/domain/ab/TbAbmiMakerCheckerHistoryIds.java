package com.iexceed.appzillonbanking.cob.admin.domain.ab;

import javax.persistence.Column;
import java.io.Serializable;

public class TbAbmiMakerCheckerHistoryIds implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name = "ID", nullable = false)
	private String id;

	@Column(name = "VERSION_NO", nullable = false)
	private int versionNo;

	public TbAbmiMakerCheckerHistoryIds() {
	}

	public TbAbmiMakerCheckerHistoryIds(String id, int versionNo) {
		this.id = id;
		this.versionNo = versionNo;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getVersionNo() {
		return versionNo;
	}

	public void setVersionNo(int versionNo) {
		this.versionNo = versionNo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + versionNo;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TbAbmiMakerCheckerHistoryIds other = (TbAbmiMakerCheckerHistoryIds) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (versionNo != other.versionNo)
			return false;
		return true;
	}
	
	
}
