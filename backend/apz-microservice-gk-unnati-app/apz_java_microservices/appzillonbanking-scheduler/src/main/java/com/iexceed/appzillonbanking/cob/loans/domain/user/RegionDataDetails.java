package com.iexceed.appzillonbanking.cob.loans.domain.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tb_st_region_data")
@Getter
@Setter
public class RegionDataDetails {

	@Id
	@JsonProperty("regionId")
	@Column(name = "REGION_ID")
	private Integer regionId;

	@JsonProperty("regionName")
	@Column(name = "REGION_NAME")
	private String regionName;

	@Override
	public String toString() {
		return "RegionDataDetails [regionId=" + regionId + ", regionName=" + regionName + "]";
	}

}