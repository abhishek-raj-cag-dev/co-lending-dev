package com.iexceed.appzillonbanking.cob.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LoanCreationRequestFields {

	@JsonProperty("appId")
	private String appId;

	@JsonProperty("applicationId")
	private String applicationId;

	@JsonProperty("versionNum")
	private int versionNum;

}
