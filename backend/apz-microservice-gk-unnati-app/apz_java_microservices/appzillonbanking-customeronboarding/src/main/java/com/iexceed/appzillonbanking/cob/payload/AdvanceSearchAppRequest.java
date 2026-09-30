package com.iexceed.appzillonbanking.cob.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class AdvanceSearchAppRequest {

	@JsonProperty("interfaceName")
	private String interfaceName;

	@JsonProperty("appId")
	private String appId;

	@JsonProperty("requestObj")
	private AdvanceSearchAppRequestFields requestObj;

}