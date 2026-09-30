package com.iexceed.appzillonbanking.cob.cards.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FetchEligibleCardsReqWrapper {

	@JsonProperty("apiRequest")
	private FetchEligibleCardsReq apiRequest;

}