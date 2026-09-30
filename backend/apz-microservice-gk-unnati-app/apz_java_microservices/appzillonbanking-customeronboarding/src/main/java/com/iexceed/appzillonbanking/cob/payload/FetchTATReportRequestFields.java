package com.iexceed.appzillonbanking.cob.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class FetchTATReportRequestFields {
	
	@ApiModelProperty(required = true, position = 1, example = "6")
	@JsonProperty("stateId")
	private Integer stateId;
	
	@ApiModelProperty(required = true, example = "2021-04-01")
	@JsonProperty("startDate")
	private String startDate;
	
	@ApiModelProperty(required = true, example = "2021-04-30")
	@JsonProperty("endDate")
	private String endDate;

}