package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IncomeCalculatorKiranaRequestFields {

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("businessLocation")
	private String businessLocation;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("typeOfShop")
	private String typeOfShop;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("marketClassification")
	private String marketClassification;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("areaOfTheShop")
	private String areaOfTheShop;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("areaOfTheGodown")
	private String areaOfTheGodown;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("occupancyLevelOfShopAndGodown")
	private String occupancyLevelOfShopAndGodown;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("monthlyNetBusinessIncome")
	private String monthlyNetBusinessIncome;

}
