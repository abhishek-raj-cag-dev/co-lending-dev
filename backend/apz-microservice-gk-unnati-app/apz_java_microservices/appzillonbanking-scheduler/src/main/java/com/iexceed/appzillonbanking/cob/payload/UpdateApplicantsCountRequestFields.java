package com.iexceed.appzillonbanking.cob.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateApplicantsCountRequestFields {

	@JsonProperty("applicationId")
	private String applicationId;
	
	@JsonProperty("appId")
	private String appId;
	
	@JsonProperty("versionNum")
	private Integer versionNum;

	@JsonProperty("applicantsCount")
	private Integer applicantsCount;
}