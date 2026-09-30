package com.iexceed.appzillonbanking.cob.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iexceed.appzillonbanking.cob.core.payload.FundAccountRequestFields;

import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class FundAccountRequest {
	
	@JsonProperty("interfaceName")
	private String interfaceName;
	
	@JsonProperty("requestObj")
	private FundAccountRequestFields requestObj;
}