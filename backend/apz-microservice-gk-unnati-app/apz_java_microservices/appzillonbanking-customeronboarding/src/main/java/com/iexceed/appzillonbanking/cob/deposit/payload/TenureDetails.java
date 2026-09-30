package com.iexceed.appzillonbanking.cob.deposit.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class TenureDetails {
	
	@ApiModelProperty(required = true, example = "D")
	@JsonProperty("tenureType")
	private String tenureType;
	
	@ApiModelProperty(required = true, example = "39")
	@JsonProperty("tenureValue")
	private int tenureValue;
}