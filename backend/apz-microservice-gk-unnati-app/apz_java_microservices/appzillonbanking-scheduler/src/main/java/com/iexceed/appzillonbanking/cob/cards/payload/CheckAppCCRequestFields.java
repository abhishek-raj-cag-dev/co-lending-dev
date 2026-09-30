package com.iexceed.appzillonbanking.cob.cards.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CheckAppCCRequestFields {
	
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
	
	@ApiModelProperty(required = true, position = 6, example = "CASA")
	@JsonProperty("productGroupCode")
	private String productGroupCode;

	@ApiModelProperty(required = true, position = 7, example = "22114451")
	@JsonProperty("customerId")
	private String customerId;	
	
	@ApiModelProperty(required = true, position = 8, example = "Y")
	@JsonProperty("isExistingCustomer")
	private String isExistingCustomer;
}