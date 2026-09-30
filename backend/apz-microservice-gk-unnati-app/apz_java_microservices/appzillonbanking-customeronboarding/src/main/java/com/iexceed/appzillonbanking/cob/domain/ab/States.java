package com.iexceed.appzillonbanking.cob.domain.ab;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "TB_ABOB_STATE_MASTER")
@Getter @Setter
public class States {

	@JsonProperty("stateCode")
	@Id
	@Column(name = "STATE_CODE")
	private String stateCode;
	
	@JsonProperty("appId")
	@Column(name = "APP_ID")
	private String appId;
	
	@JsonProperty("stateName")
	@Column(name = "STATE_NAME")
	private String stateName;
}