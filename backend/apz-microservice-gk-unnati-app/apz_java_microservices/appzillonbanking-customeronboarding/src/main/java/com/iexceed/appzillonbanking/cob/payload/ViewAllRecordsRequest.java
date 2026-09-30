package com.iexceed.appzillonbanking.cob.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class ViewAllRecordsRequest {

	@JsonProperty("appId")
	private String appId;
	
	@JsonProperty("requestObj")
	private ViewAllRecordsRequestFields requestObj;
}