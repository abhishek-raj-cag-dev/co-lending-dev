package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WipDedupeCheckRequestFields {
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("customerId")
	private String customerId;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("primaryKycId")
	private String primaryKycId;

}
