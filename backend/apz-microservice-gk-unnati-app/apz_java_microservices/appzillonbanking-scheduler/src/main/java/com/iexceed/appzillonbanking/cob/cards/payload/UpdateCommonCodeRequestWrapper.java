package com.iexceed.appzillonbanking.cob.cards.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateCommonCodeRequestWrapper {
	
	@JsonProperty("apiRequest")
	private UpdateCommonCodeRequest apiRequest;

}
