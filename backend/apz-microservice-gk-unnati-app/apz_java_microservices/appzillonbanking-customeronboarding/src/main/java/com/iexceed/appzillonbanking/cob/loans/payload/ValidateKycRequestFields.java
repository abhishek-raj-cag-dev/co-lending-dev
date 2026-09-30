package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ValidateKycRequestFields {

	@ApiModelProperty(required = true, example = "voter")
	@JsonProperty("kycType")
	private String kycType;
	
	@ApiModelProperty(required = true, example = "XCX2872851")
	@JsonProperty("kycId")
	private String kycId;
	
	@ApiModelProperty(required = false, example = "dd/mm/yyyy")
	@JsonProperty("dob")
	private String dob;
}