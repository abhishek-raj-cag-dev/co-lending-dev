package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IncomeCalculatorRequest {

	@ApiModelProperty(required = true, position = 1, example = "validateKyc")
	@JsonProperty("interfaceName")
	private String interfaceName;

	@ApiModelProperty(required = true, position = 1, example = "Dairy/Kirana/Tailoring")
	@JsonProperty("incomeType")
	private String incomeType;
	/*
	 * @ApiModelProperty(required = true, position = 2, example = "APZCOB")
	 * 
	 * @JsonProperty("appId") private String appId;
	 */

	@JsonProperty("requestObj")
	private IncomeCalculatorRequestFields requestObj;

}
