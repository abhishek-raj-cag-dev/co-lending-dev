package com.iexceed.appzillonbanking.cob.domain.ab;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "TB_ABOB_COUNTRY_MASTER")
@Getter @Setter
public class Countries {

	@JsonProperty("countryCode")
	@Id
	@Column(name = "COUNTRY_CODE")
	private String countryCode;
	
	@JsonProperty("appId")
	@Column(name = "APP_ID")
	private String appId;
	
	@JsonProperty("countryName")
	@Column(name = "COUNTRY_NAME")
	private String countryName;
}