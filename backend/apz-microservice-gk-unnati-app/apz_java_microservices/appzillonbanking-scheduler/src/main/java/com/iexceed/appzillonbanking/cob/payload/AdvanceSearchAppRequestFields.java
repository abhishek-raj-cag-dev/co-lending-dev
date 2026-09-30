package com.iexceed.appzillonbanking.cob.payload;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class AdvanceSearchAppRequestFields {

	@ApiModelProperty(required = false, position = 2, example = "995544871")
	@JsonProperty("mobileNo")
	private String mobileNo;
	
	@ApiModelProperty(required = false, position = 4, example = "CASA")
	@JsonProperty("product")
	private String product;
	
	@ApiModelProperty(required = false, position = 5, example = "")
	@JsonProperty("subProduct")
	private List<String> subProduct;
	
	@ApiModelProperty(required = false, position = 6, example = "")
	@JsonProperty("applicationStatus")
	private List<String> applicationStatus;
	
	@ApiModelProperty(required = false, position = 7, example = "06-01-2022")
	@JsonProperty("startDate")
	private String startDate;
	
	@ApiModelProperty(required = false, position = 8, example = "09-07-2023")
	@JsonProperty("endDate")
	private String endDate;
	
	@ApiModelProperty(required = true, position = 9, example = "john")
	@JsonProperty("userId")
	private String userId;

}