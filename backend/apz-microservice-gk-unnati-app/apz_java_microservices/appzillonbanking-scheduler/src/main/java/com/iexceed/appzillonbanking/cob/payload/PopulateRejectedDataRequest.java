package com.iexceed.appzillonbanking.cob.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PopulateRejectedDataRequest {
	
	@JsonProperty("requestObj")
	private PopulateRejectedDataRequestFields requestObj;
	
	@JsonProperty("appId")
	private String appId;	
}