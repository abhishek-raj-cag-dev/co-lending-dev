package com.iexceed.appzillonbanking.cob.cards.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ApplyCreditCardRequest {

	@JsonProperty("appId")
	private String appId;	
	
	@JsonProperty("interfaceName")
	private String interfaceName;
	
	@JsonProperty("userId")
	private String userId;
	
	@JsonProperty("requestObj")
	private ApplyCreditCardRequestFields requestObj;
}