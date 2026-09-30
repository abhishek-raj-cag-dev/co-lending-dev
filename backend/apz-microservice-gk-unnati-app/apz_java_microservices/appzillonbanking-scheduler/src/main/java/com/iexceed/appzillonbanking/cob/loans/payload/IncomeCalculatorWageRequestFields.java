package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IncomeCalculatorWageRequestFields {

	@ApiModelProperty(required = false, example = "")
	@JsonProperty("state")
	private String state;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("branchId")
	private String branchId;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("gender")
	private String gender;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("workingDays")
	private int workingDays;

}
