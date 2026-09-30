package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BREAddressDetails {
	
	@Override
	public String toString() {
		return "BREAddressDetails [addrType=" + addrType + ", addrLine1=" + addrLine1 + ", city=" + city + ", state="
				+ state + ", district=" + district + ", pinCode=" + pinCode + "]";
	}

	@ApiModelProperty(required = true, example = "1")
	@JsonProperty("addrType")
	private String addrType;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("addrLine1")
	private String addrLine1;
	
	@ApiModelProperty(required = false, example = "")
	@JsonProperty("city")
	private String city;
	
	@ApiModelProperty(required = false, example = "")
	@JsonProperty("state")
	private String state;	
	
	@ApiModelProperty(required = false, example = "")
	@JsonProperty("district")
	private String district;
	
	@ApiModelProperty(required = false, example = "")
	@JsonProperty("pinCode")
	private String pinCode;

}
