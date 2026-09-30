package com.iexceed.appzillonbanking.cob.domain.ab;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "TB_ABOB_CITY_MASTER")
@Getter @Setter
public class Cities {

	@Id
	@Column(name = "CITY_CODE")
	private String cityCode;
	
	@JsonProperty("appId")
	@Column(name = "APP_ID")
	private String appId;

	@JsonProperty("stateCode")
	@Column(name = "STATE_CODE")
	private String stateCode;
	
	@Column(name = "CITY_NAME")
	private String cityName;
}