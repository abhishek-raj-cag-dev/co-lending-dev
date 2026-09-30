package com.iexceed.appzillonbanking.cob.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CheckApplicationRequestFields {

	@ApiModelProperty(required = true, position = 1, example = "APZCOB")
	@JsonProperty("appId")
	private String appId;
	
	@ApiModelProperty(required = true, position = 2, example = "9955114477")
	@JsonProperty("mobileNumber")
	private String mobileNumber;
	
	@ApiModelProperty(required = true, position = 3, example = "usabh5584")
	@JsonProperty("nationalId")
	private String nationalId;	
	
	@ApiModelProperty(required = true, position = 4, example = "BGHVF8743P")
	@JsonProperty("pan")
	private String pan;
	
	@ApiModelProperty(required = true, position = 5, example = "sample@domain.com")
	@JsonProperty("emailId")
	private String emailId;	
	
	@ApiModelProperty(required = true, position = 6, example = "Y")
	@JsonProperty("productChanged")
	private String productChanged;
	
	@ApiModelProperty(required = true, position = 7, example = "")
	@JsonProperty("customerDataFields")
	private CustomerDataFields customerDataFields;	
	
	@ApiModelProperty(required = true, position = 8, example = "CASA")
	@JsonProperty("productGroupCode")
	private String productGroupCode;
	
	@ApiModelProperty(required = true, position = 9, example = "DEPOSIT")
	@JsonProperty("mainProductGroupCode")
	private String mainProductGroupCode;
	
	@ApiModelProperty(required = true, position = 10, example = "APZ001")
	@JsonProperty("branchCode")
	private String branchCode;
}