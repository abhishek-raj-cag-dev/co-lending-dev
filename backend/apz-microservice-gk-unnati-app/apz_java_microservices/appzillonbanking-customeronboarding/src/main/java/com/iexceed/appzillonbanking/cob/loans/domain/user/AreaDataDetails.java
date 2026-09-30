package com.iexceed.appzillonbanking.cob.loans.domain.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tb_area_data")
@Getter
@Setter
public class AreaDataDetails {

	@Id
	@JsonProperty("areaId")
	@Column(name = "AREA_ID")
	private Integer areaId;

	@JsonProperty("areaName")
	@Column(name = "AREA_NAME")
	private String areaName;

	@Override
	public String toString() {
		return "AreaDataDetails [areaId=" + areaId + ", areaName=" + areaName + "]";
	}

}