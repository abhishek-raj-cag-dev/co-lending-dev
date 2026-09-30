package com.iexceed.appzillonbanking.cob.core.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class FetchProductDetailsRequest {
	@JsonProperty("requestObj")
	private FetchProductDetailsFields requestObj;

	@JsonProperty("interfaceName")
	private String interfaceName;
	
}