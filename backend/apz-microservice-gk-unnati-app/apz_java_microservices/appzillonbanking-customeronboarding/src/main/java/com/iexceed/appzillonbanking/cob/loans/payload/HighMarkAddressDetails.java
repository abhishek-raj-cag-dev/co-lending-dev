package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class HighMarkAddressDetails {
	
	@ApiModelProperty(required = true, example = "Present")
	@JsonProperty("type")
	private String type;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("addressLine1")
	private String addressLine1;
	
	@ApiModelProperty(required = false, example = "")
	@JsonProperty("addressLine2")
	private String addressLine2;
	
	@ApiModelProperty(required = false, example = "")
	@JsonProperty("addressLine3")
	private String addressLine3;
	
	@ApiModelProperty(required = false, example = "")
	@JsonProperty("city")
	private String city;
	
	@ApiModelProperty(required = false, example = "")
	@JsonProperty("state")
	private String state;
	
	@ApiModelProperty(required = false, example = "")
	@JsonProperty("pincode")
	private String pincode;

}
