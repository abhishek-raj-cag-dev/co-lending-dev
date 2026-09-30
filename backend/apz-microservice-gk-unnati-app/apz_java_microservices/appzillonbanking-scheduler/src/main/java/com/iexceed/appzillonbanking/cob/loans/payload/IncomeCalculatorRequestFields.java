package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IncomeCalculatorRequestFields {

	@ApiModelProperty(required = false, example = "")
	@JsonProperty("dairyRequestFields")
	private IncomeCalculatorDairyRequestFields dairyRequestFields;

	@ApiModelProperty(required = false, example = "")
	@JsonProperty("kiranaRequestFields")
	private IncomeCalculatorKiranaRequestFields kiranaRequestFields;
	
	@ApiModelProperty(required = false, example = "")
	@JsonProperty("tailoringRequestFields")
	private IncomeCalculatorTailoringRequestFields tailoringRequestFields;
	
	@ApiModelProperty(required = false, example = "")
	@JsonProperty("wageRequestFields")
	private IncomeCalculatorWageRequestFields wageRequestFields;
	
	@ApiModelProperty(required = false, example = "")
	@JsonProperty("rentalIncomeRequestFields")
	private IncomeCalculatorRentalIncomeRequestFields rentalIncomeRequestFields;

	@ApiModelProperty(required = false, example = "")
	@JsonProperty("agricultureRequestFields")
	private IncomeCalculatorAgricultureRequestFields agricultureRequestFields;

}
