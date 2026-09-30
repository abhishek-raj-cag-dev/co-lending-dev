package com.iexceed.appzillonbanking.cob.domain.ab;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "TB_ABOB_LOV_MASTER")
@Getter @Setter
public class LovMaster {

	@JsonProperty("lovId")
	@Id
	@Column(name = "LOV_ID")
	private int lovId;
	
	@JsonProperty("appId")
	@Column(name = "APP_ID")
	private String appId;
	
	@JsonProperty("lovName")
	@Column(name = "LOV_NAME")
	private String lovName;
	
	@JsonProperty("lovDtls")
	@Column(name = "LOV_DTLS")
	private String lovDtls;
}