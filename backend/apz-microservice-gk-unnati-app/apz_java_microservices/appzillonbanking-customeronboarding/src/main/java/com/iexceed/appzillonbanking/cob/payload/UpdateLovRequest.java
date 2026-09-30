package com.iexceed.appzillonbanking.cob.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateLovRequest {
	
	@JsonProperty("requestObj")
	private UpdateLovRequestFields requestObj;
	
	
}