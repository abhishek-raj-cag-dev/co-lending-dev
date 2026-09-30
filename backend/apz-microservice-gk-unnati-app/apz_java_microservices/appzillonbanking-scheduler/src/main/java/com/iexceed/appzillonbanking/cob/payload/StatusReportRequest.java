package com.iexceed.appzillonbanking.cob.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class StatusReportRequest {

	@JsonProperty("requestObj")
	private StatusReportRequestFields requestObj;
	
	@JsonProperty("appId")
	private String appId;
}