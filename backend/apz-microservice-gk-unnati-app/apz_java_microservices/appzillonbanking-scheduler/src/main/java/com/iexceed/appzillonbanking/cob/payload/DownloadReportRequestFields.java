package com.iexceed.appzillonbanking.cob.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class DownloadReportRequestFields {

	@ApiModelProperty(required = true, position = 1, example = "APZCOB")
	@JsonProperty("appId")
	private String appId;	
	
	@ApiModelProperty(required = true, position = 2, example = "16185889475433589")
	@JsonProperty("applicationId")
	private String applicationId;
	
	@ApiModelProperty(required = true, position = 3, example = "creditAssessment")
	@JsonProperty("reportType")
	private String reportType;	
	
	@ApiModelProperty(required = true, position = 4, example = "Y")
	@JsonProperty("generateReport")
	private boolean generateReport;	
	
}