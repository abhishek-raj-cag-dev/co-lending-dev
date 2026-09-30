package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ValidateVoterIdRequestFields {

	@ApiModelProperty(required = true, position = 1, example = "876876876868768")
	@JsonProperty("RequestID")
	private String reqRefNo;
	
	@ApiModelProperty(required = true, example = "XCX2872851")
	@JsonProperty("ID")
	private String kycId;
	
}