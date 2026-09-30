package com.iexceed.appzillonbanking.cob.deposit.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DeleteNomineeRequest {
	
	@JsonProperty("appId")
	private String appId;	
	
	@JsonProperty("interfaceName")
	private String interfaceName;
	
	@JsonProperty("requestObj")
	private DeleteNomineeRequestFields requestObj;
	
	
}