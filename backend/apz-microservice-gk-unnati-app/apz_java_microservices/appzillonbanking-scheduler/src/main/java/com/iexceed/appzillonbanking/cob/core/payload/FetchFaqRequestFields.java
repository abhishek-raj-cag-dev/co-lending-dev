package com.iexceed.appzillonbanking.cob.core.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class FetchFaqRequestFields {
	
	@JsonProperty("product")
	private String product;
	
	@JsonProperty("stage")
	private String stage;
}