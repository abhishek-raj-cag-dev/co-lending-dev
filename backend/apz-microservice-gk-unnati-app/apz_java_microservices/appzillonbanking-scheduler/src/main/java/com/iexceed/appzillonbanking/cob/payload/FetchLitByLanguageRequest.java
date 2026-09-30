package com.iexceed.appzillonbanking.cob.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class FetchLitByLanguageRequest {

	@JsonProperty("interfaceName")
	private String interfaceName;
	
	@JsonProperty("requestObj")
	private FetchLitByLanguageRequestFields requestObj;
}