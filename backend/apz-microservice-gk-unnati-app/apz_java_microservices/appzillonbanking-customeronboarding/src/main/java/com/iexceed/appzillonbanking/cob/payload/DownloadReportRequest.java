package com.iexceed.appzillonbanking.cob.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class DownloadReportRequest {

	@JsonProperty("requestObj")
	private DownloadReportRequestFields requestObj;
	
	@JsonProperty("interfaceName")
	private String interfaceName;
	
	
}