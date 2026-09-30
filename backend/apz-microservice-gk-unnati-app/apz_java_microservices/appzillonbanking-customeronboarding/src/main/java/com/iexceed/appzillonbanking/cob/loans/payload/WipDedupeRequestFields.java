package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WipDedupeRequestFields {
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("customerType")
	private String customerType;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("customerId")
	private String customerId;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("primaryKycId")
	private String primaryKycId;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("pid")
	private String pid;

}
