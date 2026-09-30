package com.iexceed.appzillonbanking.cob.loans.domain.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tb_st_region_state")
@Getter
@Setter
public class RegionStateMappingDetails {

	@Id
	@JsonProperty("regionId")
	@Column(name = "region_id")
	private Integer regionId;

	@JsonProperty("stateId")
	@Column(name = "state_id")
	private Integer stateId;

	@Override
	public String toString() {
		return "RegionStateMappingDetails [regionId=" + regionId + ", stateId=" + stateId + "]";
	}

}