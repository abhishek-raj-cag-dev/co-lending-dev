package com.iexceed.appzillonbanking.cob.loans.domain.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.Transient;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tb_st_branch_area")
@Getter
@Setter
public class BranchAreaMappingDetails {

	@Id
	@JsonProperty("branchId")
	@Column(name = "BRANCH_ID")
	private String branchId;

	@JsonProperty("areaId")
	@Column(name = "AREA_ID")
	private Integer areaId;

	@JsonProperty("areaName")
	@Column(name = "AREA_NAME")
	private String areaName;

	@Transient
	@JsonProperty("regionName")
	private String regionName;

	@Transient
	@JsonProperty("stateId")
	private Integer stateId;

	@Transient
	@JsonProperty("stateName")
	private String stateName;

	public BranchAreaMappingDetails() {

	}

	public BranchAreaMappingDetails(Integer stateId, String stateName, String regionName, String areaName,
			String branchId) {
		this.branchId = branchId;
		this.areaName = areaName;
		this.regionName = regionName;
		this.stateId = stateId;
		this.stateName = stateName;
	}
	
	public BranchAreaMappingDetails(String branchId) {
		this.branchId = branchId;
	}

	@Override
	public String toString() {
		return "BranchAreaMappingDetails [branchId=" + branchId + ", areaId=" + areaId + ", areaName=" + areaName
				+ ", regionName=" + regionName + ", stateId=" + stateId + "]";
	}

}