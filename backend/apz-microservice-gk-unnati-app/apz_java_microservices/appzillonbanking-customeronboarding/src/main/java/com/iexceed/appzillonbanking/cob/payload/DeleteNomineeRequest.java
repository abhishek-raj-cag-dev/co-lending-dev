package com.iexceed.appzillonbanking.cob.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class DeleteNomineeRequest {
	@JsonProperty("requestObj")
	private DeleteNomineeRequestFields requestObj;
	
	@JsonProperty("interfaceName")
	private String interfaceName;
}