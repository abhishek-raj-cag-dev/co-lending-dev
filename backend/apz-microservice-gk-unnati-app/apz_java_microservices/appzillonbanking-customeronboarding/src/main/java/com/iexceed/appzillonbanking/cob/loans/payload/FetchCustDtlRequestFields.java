package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class FetchCustDtlRequestFields {
	
	@ApiModelProperty(required = true, example = "APZCOB")
	@JsonProperty("appId")
	private String appId;	

	@ApiModelProperty(required = true, example = "22114451")
	@JsonProperty("customerId")
	private String customerId;	
	
	@ApiModelProperty(required = true, example = "DEPOSIT")
	@JsonProperty("productGroupCode")
	private String productGroupCode;
}