package com.iexceed.appzillonbanking.cob.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class DownloadReportRequestWrapper {
	
	@JsonProperty("apiRequest")
	private DownloadReportRequest downloadReportRequest;
}
