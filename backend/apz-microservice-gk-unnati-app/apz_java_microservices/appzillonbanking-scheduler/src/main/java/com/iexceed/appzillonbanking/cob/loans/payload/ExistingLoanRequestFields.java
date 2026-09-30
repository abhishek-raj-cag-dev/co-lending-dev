package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ExistingLoanRequestFields {

	@ApiModelProperty(required = true, example = "6102857")
	@JsonProperty("memberId")
	private String memberId;
	
}