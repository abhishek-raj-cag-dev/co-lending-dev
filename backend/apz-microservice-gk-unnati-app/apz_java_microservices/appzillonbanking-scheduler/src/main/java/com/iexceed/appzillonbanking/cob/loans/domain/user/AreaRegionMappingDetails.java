package com.iexceed.appzillonbanking.cob.loans.domain.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tb_st_area_region")
@Getter
@Setter
public class AreaRegionMappingDetails {

	@Id
	@JsonProperty("areaId")
	@Column(name = "AREA_ID")
	private Integer areaId;

	@JsonProperty("regionId")
	@Column(name = "REGION_ID")
	private Integer regionId;

	@JsonProperty("regionName")
	@Column(name = "REGION_NAME")
	private String regionName;

	@Override
	public String toString() {
		return "AreaRegionMappingDetails [areaId=" + areaId + ", regionId=" + regionId + ", regionName=" + regionName
				+ "]";
	}

}