package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class HighMarkCheckCBRequestFields {

	@ApiModelProperty(required = true, example = "A01Unnati005445882303231251")
	@JsonProperty("loanId")
	private String loanId;
	
	@ApiModelProperty(required = true, example = "1")
	@JsonProperty("statusCode")
	private String statusCode;
	
	@ApiModelProperty(required = true, example = "1")
	@JsonProperty("caglOs")
	private String caglOs;
	
	
}
