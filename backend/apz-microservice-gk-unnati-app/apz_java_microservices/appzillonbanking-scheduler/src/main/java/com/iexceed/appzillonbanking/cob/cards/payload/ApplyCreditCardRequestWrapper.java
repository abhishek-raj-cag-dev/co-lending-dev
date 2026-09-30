package com.iexceed.appzillonbanking.cob.cards.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ApplyCreditCardRequestWrapper {

	@JsonProperty("apiRequest")	
	private ApplyCreditCardRequest apiRequest;
}